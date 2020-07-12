package com.riintouge.strata.block;

import com.riintouge.strata.Strata;
import com.riintouge.strata.image.LayeredTexture;
import com.riintouge.strata.image.LayeredTextureLayer;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class GenericCubeTextureMap implements IModelRetexturizerMap
{
    private final String registryName;
    private final LayeredTextureLayer[][] layerLayers = new LayeredTextureLayer[ Layer.values().length ][];

    public enum Layer
    {
        ALL   ( null ),
        CAPS  ( ALL ),
        SIDES ( ALL ),
        UP    ( CAPS ),
        DOWN  ( CAPS ),
        NORTH ( SIDES ),
        SOUTH ( SIDES ),
        EAST  ( SIDES ),
        WEST  ( SIDES );

        public final Layer parentLayer;
        public final ResourceLocation resourceLocation;

        Layer( Layer parentLayer )
        {
            this.parentLayer = parentLayer;
            this.resourceLocation = new ResourceLocation( Strata.modid , String.format( "meta/%s" , this.toString().toLowerCase() ) );
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

        return layer;
    }

    public void set( Layer facing , LayeredTextureLayer[] layers )
    {
        layerLayers[ facing.ordinal() ] = layers;
    }

    public void stitchTextures( String registryName , TextureMap textureMap )
    {
        for( Layer layer : Layer.values() )
        {
            LayeredTextureLayer[] textureLayers = layerLayers[ layer.ordinal() ];
            if( textureLayers != null )
            {
                System.out.println( "Stitching " + registryName + layer.resourceLocationSuffix() );
                textureMap.setTextureEntry(
                    new LayeredTexture(
                        new ResourceLocation( Strata.modid , registryName + layer.resourceLocationSuffix() ),
                        textureLayers ) );
            }
        }
    }

    // IModelRetexturizerMap overrides

    @Nonnull
    @Override
    public ResourceLocation getOrDefault( ResourceLocation modelTextureLocationIn )
    {
        for( Layer layer : Layer.values() )
            if( modelTextureLocationIn.equals( layer.resourceLocation ) )
                return new ResourceLocation( Strata.modid , registryName + getDisplayLayer( layer ).resourceLocationSuffix() );

        return Layer.ALL.resourceLocation;
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
}
