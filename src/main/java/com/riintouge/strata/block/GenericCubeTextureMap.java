package com.riintouge.strata.block;

import com.riintouge.strata.Strata;
import com.riintouge.strata.image.LayeredTexture;
import com.riintouge.strata.image.LayeredTextureLayer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

// TODO: This class needs an interface. Too many things know about what it really is.
public class GenericCubeTextureMap implements IModelRetexturizerMap , IFacingTextureMap
{
    protected final String registryName;
    protected final LayeredTextureLayer[][] layerLayers = new LayeredTextureLayer[ Layer.values().length ][];
    protected final TextureAtlasSprite[] layerTextures = new TextureAtlasSprite[ Layer.values().length ];

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

    public GenericCubeTextureMap( String registryName )
    {
        this.registryName = registryName;
    }

    public Layer getDisplayLayer( Layer layer )
    {
        for( Layer displayLayer = layer ; displayLayer != null ; displayLayer = displayLayer.parentLayer )
            if( layerLayers[ displayLayer.ordinal() ] != null )
                return displayLayer;

        return Layer.ALL;
    }

    public Layer getDisplayLayer( EnumFacing facing )
    {
        if( facing != null )
            for( Layer layer : Layer.values() )
                if( layer.facing == facing )
                    return layer;

        return Layer.ALL;
    }

    // TODO: move to constructor
    public void set( Layer layer , LayeredTextureLayer[] layers )
    {
        layerLayers[ layer.ordinal() ] = layers;
    }

    public void stitchTextures( TextureMap textureMap )
    {
        for( Layer layer : Layer.values() )
        {
            LayeredTextureLayer[] textureLayers = layerLayers[ layer.ordinal() ];
            if( textureLayers != null )
            {
                System.out.println( "Stitching " + registryName + layer.resourceLocationSuffix() );
                LayeredTexture layerTexture = new LayeredTexture(
                    new ResourceLocation( Strata.modid , registryName + layer.resourceLocationSuffix() ),
                    textureLayers );
                layerTextures[ layer.ordinal() ] = layerTexture;
                textureMap.setTextureEntry( layerTexture );
            }
        }
    }

    @Nonnull
    public ResourceLocation get( Layer layer )
    {
        ResourceLocation resourceLocation = getOrDefault( layer , null );
        return resourceLocation != null
            ? resourceLocation
            : new ResourceLocation( Strata.modid , registryName + GenericCubeTextureMap.Layer.ALL.resourceLocationSuffix() );
    }

    public ResourceLocation getOrDefault( Layer layer , ResourceLocation defaultValue )
    {
        return layerLayers[ layer.ordinal() ] != null
            ? new ResourceLocation( Strata.modid , registryName + getDisplayLayer( layer ).resourceLocationSuffix() )
            : defaultValue;
    }

    public TextureAtlasSprite getTexture( EnumFacing facing )
    {
        for( Layer layer = getDisplayLayer( facing ) ; layer != null ; layer = layer.parentLayer )
            if( layerLayers[ layer.ordinal() ] != null )
                return layerTextures[ layer.ordinal() ];

        return layerTextures[ Layer.ALL.ordinal() ];
    }

    // IModelRetexturizerMap overrides

    @Nonnull
    @Override
    public ResourceLocation getOrDefault( ResourceLocation modelTextureLocationIn )
    {
        for( Layer layer : Layer.values() )
            if( modelTextureLocationIn.equals( layer.resourceLocation ) )
                return new ResourceLocation( Strata.modid , registryName + getDisplayLayer( layer ).resourceLocationSuffix() );

        return modelTextureLocationIn;
    }

    @Nonnull
    @Override
    public Collection< ResourceLocation > getAll()
    {
        List< ResourceLocation > resourceLocations = new ArrayList<>();
        for( Layer layer : Layer.values() )
            if( layerLayers[ layer.ordinal() ] != null )
                resourceLocations.add( new ResourceLocation( Strata.modid , registryName + layer.resourceLocationSuffix() ) );

        return resourceLocations;
    }

    // IFacingTextureMap overrides

    @Nonnull
    @Override
    public ResourceLocation get( EnumFacing facing )
    {
        ResourceLocation resourceLocation = getOrDefault( facing , null );
        return resourceLocation != null
            ? resourceLocation
            : new ResourceLocation( Strata.modid , registryName + GenericCubeTextureMap.Layer.ALL.resourceLocationSuffix() );
    }

    @Override
    public ResourceLocation getOrDefault( EnumFacing facing , ResourceLocation defaultValue )
    {
        if( facing != null )
            for( Layer layer : Layer.values() )
                if( layer.facing == facing )
                    return new ResourceLocation( Strata.modid , registryName + getDisplayLayer( layer ).resourceLocationSuffix() );

        return defaultValue;
    }
}
