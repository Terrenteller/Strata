package com.riintouge.strata.image;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.Vector;
import java.util.function.Function;

public class LayeredTexture extends TextureAtlasSprite
{
    private LayeredTextureLayer[] layers;

    public LayeredTexture( ResourceLocation registryName , LayeredTextureLayer[] layers )
    {
        super( registryName.toString() );
        System.out.println( "LayeredTexture // " + registryName.toString() );

        this.layers = layers;
    }

    @Override
    public Collection< ResourceLocation > getDependencies()
    {
        Vector< ResourceLocation > resources = new Vector<>();
        for( LayeredTextureLayer layer : layers )
            resources.add( layer.resource );

        return resources;
    }

    @Override
    public boolean hasCustomLoader( @Nonnull IResourceManager manager , @Nonnull ResourceLocation location )
    {
        return true;
    }

    @Override
    public boolean load(
        @Nonnull IResourceManager manager,
        @Nonnull ResourceLocation location,
        @Nonnull Function< ResourceLocation , TextureAtlasSprite > textureGetter )
    {
        System.out.println( "LayeredTexture::load()" );

        if( layers.length == 1 )
        {
            final TextureAtlasSprite texture = textureGetter.apply( layers[ 0 ].resource );
            width = texture.getIconWidth();
            height = texture.getIconHeight();

            this.clearFramesTextureData();
            for( int index = 0 ; index < texture.getFrameCount() ; index++ )
                this.framesTextureData.add( texture.getFrameTextureData( index ) );
        }
        else if( layers.length > 1 )
        {
            final int[][][] rawLayers = new int[ layers.length ][][];

            for( int index = 0 ; index < layers.length ; index++ )
            {
                ResourceLocation layerResource = layers[ index ].resource;
                TextureAtlasSprite layerTexture = textureGetter.apply( layerResource );
                rawLayers[ index ] = layerTexture.getFrameTextureData( 0 );

                if( index == layers.length - 1 )
                {
                    width = layerTexture.getIconWidth();
                    height = layerTexture.getIconHeight();
                }
            }

            final int[][] pixels = new int[ Minecraft.getMinecraft().gameSettings.mipmapLevels + 1 ][];
            pixels[ 0 ] = new int[ width * height ];

            for( int pixel = 0 ; pixel < width * height ; pixel++ )
            {
                int blendPixel = rawLayers[ rawLayers.length - 1 ][ 0 ][ pixel ];

                for( int layer = rawLayers.length - 2 ; layer >= 0 ; layer-- )
                {
                    blendPixel = layers[ layer ].blendMode.blend(
                        rawLayers[ layer ][ 0 ][ pixel ],
                        layers[ layer ].opacity ,
                        blendPixel );
                }

                pixels[ 0 ][ pixel ] = blendPixel;
            }

            this.clearFramesTextureData();
            this.framesTextureData.add( pixels );

        }

        // What does it mean for this to be stitched or not?
        return false;
    }
}
