package com.riintouge.strata.block;

import com.riintouge.strata.Strata;
import com.riintouge.strata.image.LayeredTexture;
import com.riintouge.strata.image.LayeredTextureLayer;
import com.riintouge.strata.misc.IFacingResourceLocationMap;
import com.riintouge.strata.misc.IMultiFacingResourceMap;
import com.riintouge.strata.misc.IResourceLocationMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ProtoBlockTextureMap implements
    IMultiFacingResourceMap< ProtoBlockTextureMap.Layer >,
    IResourceLocationMap,
    IFacingResourceLocationMap
{
    protected final String baseRegistryName;
    protected final LayeredTextureLayer[][] layerLayers;
    // FIXME: TextureAtlasSprite is client-side only but this has to exist in a server context
    protected final Object[] layerTextures = new Object[ Layer.values().length ];

    public enum Layer
    {
        ALL   ( null  , null             , EnumFacing.VALUES               ),
        CAPS  ( ALL   , null             , EnumFacing.UP , EnumFacing.DOWN ),
        SIDES ( ALL   , null             , EnumFacing.HORIZONTALS          ),
        UP    ( CAPS  , EnumFacing.UP    , EnumFacing.UP                   ),
        DOWN  ( CAPS  , EnumFacing.DOWN  , EnumFacing.DOWN                 ),
        NORTH ( SIDES , EnumFacing.NORTH , EnumFacing.NORTH                ),
        SOUTH ( SIDES , EnumFacing.SOUTH , EnumFacing.SOUTH                ),
        EAST  ( SIDES , EnumFacing.EAST  , EnumFacing.EAST                 ),
        WEST  ( SIDES , EnumFacing.WEST  , EnumFacing.WEST                 );

        public final Layer parentLayer;
        public final ResourceLocation resourceLocation;
        public final String resourceLocationSuffix;
        public final EnumFacing facing;
        public final EnumFacing[] applicableFacings;

        Layer( Layer parentLayer , EnumFacing facing , EnumFacing ... applicableFacings )
        {
            this.parentLayer = parentLayer;
            this.resourceLocation = Strata.resource( String.format( "meta/%s" , this.toString().toLowerCase() ) );
            this.resourceLocationSuffix = parentLayer == null ? "" : "_" + toString().toLowerCase();
            this.facing = facing;
            this.applicableFacings = applicableFacings;
        }
    }

    public ProtoBlockTextureMap( String baseRegistryName , LayeredTextureLayer[][] layerLayers )
    {
        this.baseRegistryName = baseRegistryName;
        this.layerLayers = layerLayers;
    }

    public boolean ownsTextureForLayer( Layer layer )
    {
        LayeredTextureLayer[] textureLayers = layerLayers[ layer.ordinal() ];
        return textureLayers == null // Weird, but if null it must be handled by us because it cannot not be ours
            || textureLayers.length != 1
            || textureLayers[ 0 ].textureResource.getResourceDomain().equalsIgnoreCase( Strata.MOD_ID );
    }

    public ResourceLocation deduplicatedLayerResource( Layer layer )
    {
        return ownsTextureForLayer( layer )
            ? Strata.resource( baseRegistryName + layer.resourceLocationSuffix )
            : layerLayers[ layer.ordinal() ][ 0 ].textureResource;
    }

    @SideOnly( Side.CLIENT )
    public TextureAtlasSprite getTexture( EnumFacing facing )
    {
        Layer actualLayer = getActualMultiFacing( facing );
        if( ownsTextureForLayer( actualLayer ) )
            return (TextureAtlasSprite)layerTextures[ actualLayer.ordinal() ];

        return Minecraft.getMinecraft()
            .getTextureMapBlocks()
            .getAtlasSprite( layerLayers[ actualLayer.ordinal() ][ 0 ].textureResource.toString() );
    }

    @SideOnly( Side.CLIENT )
    public TextureAtlasSprite[] getTextures()
    {
        TextureAtlasSprite[] textures = new TextureAtlasSprite[ 6 ];
        for( EnumFacing facing : EnumFacing.VALUES )
            textures[ facing.getIndex() ] = getTexture( facing );

        return textures;
    }

    @SideOnly( Side.CLIENT )
    public void stitchTextures( TextureMap textureMap , boolean pre )
    {
        for( Layer layer : Layer.values() )
        {
            LayeredTextureLayer[] textureLayers = layerLayers[ layer.ordinal() ];
            if( textureLayers == null )
                continue;

            boolean ownsTextureForLayer = ownsTextureForLayer( layer );
            if( pre && ownsTextureForLayer )
            {
                String resourcePath = baseRegistryName + layer.resourceLocationSuffix;
                Strata.LOGGER.trace( "Stitching " + resourcePath );

                // TODO: Don't generate a texture if a texture already exists, such as from a texture pack.
                // ModelDynBucket might have code for this...
                LayeredTexture layerTexture = new LayeredTexture( Strata.resource( resourcePath ) , textureLayers );
                layerTextures[ layer.ordinal() ] = layerTexture;
                textureMap.setTextureEntry( layerTexture );
            }
            else if( !pre && !ownsTextureForLayer )
                layerTextures[ layer.ordinal() ] = textureMap.getAtlasSprite( textureLayers[ 0 ].textureResource.toString() );
        }
    }

    // IMultiFacingResourceMap overrides

    @Nullable
    @Override
    public Layer getActualMultiFacing( @Nullable Layer multiFacing )
    {
        for( Layer displayLayer = multiFacing ; displayLayer != null ; displayLayer = displayLayer.parentLayer )
            if( layerLayers[ displayLayer.ordinal() ] != null )
                return displayLayer;

        return Layer.ALL;
    }

    @Nullable
    @Override
    public Layer getActualMultiFacing( @Nullable EnumFacing facing )
    {
        if( facing != null )
            for( Layer layer : Layer.values() )
                if( layer.facing == facing )
                    return getActualMultiFacing( layer );

        return Layer.ALL;
    }

    @Nullable
    @Override
    public ResourceLocation get( @Nullable Layer multiFacing )
    {
        ResourceLocation resourceLocation = getOrDefault( multiFacing , null );
        return resourceLocation != null ? resourceLocation : deduplicatedLayerResource( ProtoBlockTextureMap.Layer.ALL );
    }

    @Nullable
    @Override
    public ResourceLocation getOrDefault( @Nullable Layer multiFacing , @Nullable ResourceLocation defaultValue )
    {
        return multiFacing != null && layerLayers[ multiFacing.ordinal() ] != null
            ? deduplicatedLayerResource( getActualMultiFacing( multiFacing ) )
            : defaultValue;
    }

    // IResourceLocationMap overrides

    @Nullable
    @Override
    public ResourceLocation get( @Nullable ResourceLocation originalResource )
    {
        // Unlike other getters, we don't want to re-map resources we're not meant to
        return getOrDefault( originalResource , originalResource );
    }

    @Nullable
    @Override
    public ResourceLocation getOrDefault( @Nullable ResourceLocation originalResource , @Nullable ResourceLocation defaultValue )
    {
        if( originalResource != null )
            for( Layer layer : Layer.values() )
                if( originalResource.equals( layer.resourceLocation ) )
                    return deduplicatedLayerResource( getActualMultiFacing( layer ) );

        return defaultValue;
    }

    @Nonnull
    @Override
    public Collection< ResourceLocation > getAll()
    {
        List< ResourceLocation > resourceLocations = new ArrayList<>();
        for( Layer layer : Layer.values() )
            if( layerLayers[ layer.ordinal() ] != null )
                resourceLocations.add( deduplicatedLayerResource( layer ) );

        return resourceLocations;
    }

    // IFacingResourceLocationMap overrides

    @Nullable
    @Override
    public ResourceLocation get( @Nullable EnumFacing facing )
    {
        ResourceLocation resourceLocation = getOrDefault( facing , null );
        return resourceLocation != null ? resourceLocation : deduplicatedLayerResource( ProtoBlockTextureMap.Layer.ALL );
    }

    @Nullable
    @Override
    public ResourceLocation getOrDefault( @Nullable EnumFacing facing , @Nullable ResourceLocation defaultValue )
    {
        if( facing != null )
            for( Layer layer : Layer.values() )
                if( layer.facing == facing )
                    return deduplicatedLayerResource( getActualMultiFacing( layer ) );

        return defaultValue;
    }
}
