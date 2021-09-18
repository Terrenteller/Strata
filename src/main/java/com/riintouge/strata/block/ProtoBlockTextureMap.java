package com.riintouge.strata.block;

import com.riintouge.strata.Strata;
import com.riintouge.strata.image.LayeredTexture;
import com.riintouge.strata.image.LayeredTextureLayer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
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
        public final EnumFacing facing;
        public final EnumFacing[] applicableFacings;

        Layer( Layer parentLayer , EnumFacing facing , EnumFacing ... applicableFacings )
        {
            this.parentLayer = parentLayer;
            this.resourceLocation = new ResourceLocation( Strata.modid , String.format( "meta/%s" , this.toString().toLowerCase() ) );
            this.facing = facing;
            this.applicableFacings = applicableFacings;
        }

        public String resourceLocationSuffix()
        {
            // FIXME: It's not clear underscores are used as separators in texture resource locations throughout Strata.
            // Even worse, what about meta values?
            return this == ALL ? "" : "_" + resourceLocationComponent();
        }

        public String resourceLocationComponent()
        {
            return toString().toLowerCase();
        }
    }

    public ProtoBlockTextureMap( String baseRegistryName , LayeredTextureLayer[][] layerLayers )
    {
        this.baseRegistryName = baseRegistryName;
        this.layerLayers = layerLayers;
    }

    @SideOnly( Side.CLIENT )
    public TextureAtlasSprite getTexture( EnumFacing facing )
    {
        return (TextureAtlasSprite)layerTextures[ getActualMultiFacing( facing ).ordinal() ];
    }

    @SideOnly( Side.CLIENT )
    public void stitchTextures( TextureMap textureMap )
    {
        for( Layer layer : Layer.values() )
        {
            LayeredTextureLayer[] textureLayers = layerLayers[ layer.ordinal() ];
            if( textureLayers != null )
            {
                System.out.println( "Stitching " + baseRegistryName + layer.resourceLocationSuffix() );
                LayeredTexture layerTexture = new LayeredTexture(
                    new ResourceLocation( Strata.modid , baseRegistryName + layer.resourceLocationSuffix() ),
                    textureLayers );
                layerTextures[ layer.ordinal() ] = layerTexture;
                textureMap.setTextureEntry( layerTexture );
            }
        }
    }

    // IMultiFacingResourceMap overrides

    @Nonnull
    @Override
    public Layer getActualMultiFacing( Layer multiFacing )
    {
        for( Layer displayLayer = multiFacing ; displayLayer != null ; displayLayer = displayLayer.parentLayer )
            if( layerLayers[ displayLayer.ordinal() ] != null )
                return displayLayer;

        return Layer.ALL;
    }

    @Nonnull
    @Override
    public Layer getActualMultiFacing( EnumFacing facing )
    {
        if( facing != null )
            for( Layer layer : Layer.values() )
                if( layer.facing == facing )
                    return getActualMultiFacing( layer );

        return Layer.ALL;
    }

    @Nonnull
    @Override
    public ResourceLocation get( Layer multiFacing )
    {
        ResourceLocation resourceLocation = getOrDefault( multiFacing , null );
        return resourceLocation != null
            ? resourceLocation
            : new ResourceLocation( Strata.modid , baseRegistryName + ProtoBlockTextureMap.Layer.ALL.resourceLocationSuffix() );
    }

    @Override
    public ResourceLocation getOrDefault( Layer multiFacing , ResourceLocation defaultValue )
    {
        return layerLayers[ multiFacing.ordinal() ] != null
            ? new ResourceLocation( Strata.modid , baseRegistryName + getActualMultiFacing( multiFacing ).resourceLocationSuffix() )
            : defaultValue;
    }

    // IResourceLocationMap overrides

    @Nonnull
    @Override
    public ResourceLocation get( ResourceLocation resourceLocation )
    {
        // Unlike other getters, we don't want to re-map resources we're not meant to
        return getOrDefault( resourceLocation , resourceLocation );
    }

    @Override
    public ResourceLocation getOrDefault( ResourceLocation resourceLocation , ResourceLocation defaultValue )
    {
        for( Layer layer : Layer.values() )
            if( resourceLocation.equals( layer.resourceLocation ) )
                return new ResourceLocation( Strata.modid , baseRegistryName + getActualMultiFacing( layer ).resourceLocationSuffix() );

        return defaultValue;
    }

    @Nonnull
    @Override
    public Collection< ResourceLocation > getAll()
    {
        List< ResourceLocation > resourceLocations = new ArrayList<>();
        for( Layer layer : Layer.values() )
            if( layerLayers[ layer.ordinal() ] != null )
                resourceLocations.add( new ResourceLocation( Strata.modid , baseRegistryName + layer.resourceLocationSuffix() ) );

        return resourceLocations;
    }

    // IFacingResourceLocationMap overrides

    @Nonnull
    @Override
    public ResourceLocation get( EnumFacing facing )
    {
        ResourceLocation resourceLocation = getOrDefault( facing , null );
        return resourceLocation != null
            ? resourceLocation
            : new ResourceLocation( Strata.modid , baseRegistryName + ProtoBlockTextureMap.Layer.ALL.resourceLocationSuffix() );
    }

    @Override
    public ResourceLocation getOrDefault( EnumFacing facing , ResourceLocation defaultValue )
    {
        if( facing != null )
            for( Layer layer : Layer.values() )
                if( layer.facing == facing )
                    return new ResourceLocation( Strata.modid , baseRegistryName + getActualMultiFacing( layer ).resourceLocationSuffix() );

        return defaultValue;
    }
}
