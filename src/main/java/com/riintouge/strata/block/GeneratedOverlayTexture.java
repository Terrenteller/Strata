package com.riintouge.strata.block;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.Vector;
import java.util.function.Function;

public class GeneratedOverlayTexture extends TextureAtlasSprite
{
    private ResourceLocation baseResource;
    private ResourceLocation overlayResource;

    public GeneratedOverlayTexture( ResourceLocation base , ResourceLocation overlay , String name )
    {
        super( name );

        this.baseResource = base;
        this.overlayResource = overlay;
    }

    @Override
    public Collection<ResourceLocation> getDependencies()
    {
        Vector<ResourceLocation> deps = new Vector<>();
        deps.add( baseResource );
        deps.add( overlayResource );

        return deps;
    }

    @Override
    public boolean hasCustomLoader( @Nonnull IResourceManager manager , @Nonnull ResourceLocation location )
    {
        return true;
    }

    @Override
    public boolean load( @Nonnull IResourceManager manager , @Nonnull ResourceLocation location , @Nonnull Function< ResourceLocation , TextureAtlasSprite > textureGetter )
    {
        //System.out.println( "GeneratedOverlayTexture::load()" );

        final TextureAtlasSprite baseTexture = textureGetter.apply( baseResource );
        final TextureAtlasSprite overlayTexture = textureGetter.apply( overlayResource );
        width = baseTexture.getIconWidth();
        height = baseTexture.getIconHeight();
        final int[][] pixels = new int[ Minecraft.getMinecraft().gameSettings.mipmapLevels + 1 ][];
        pixels[ 0 ] = new int[ width * height ];

        //System.out.println( "Base resource is: " + baseResource.toString() );
        final int[][] baseTextureFrame = baseTexture.getFrameTextureData( 0 );
        //System.out.println( "Overlay resource is: " + overlayResource.toString() );
        final int[][] overlayTextureFrame = overlayTexture.getFrameTextureData( 0 );

        PixelARGB basePixel = new PixelARGB();
        PixelARGB overlayPixel = new PixelARGB();
        PixelARGB blendPixel = new PixelARGB();
        for( int pixel = 0 ; pixel < width * height ; pixel++ )
        {
            basePixel.setPixel( baseTextureFrame[ 0 ][ pixel ] );
            overlayPixel.setPixel( overlayTextureFrame[ 0 ][ pixel ] );

            // TODO: Yeah, yeah, base needs to be scaled by itself
            // TODO: What did I mean by this?
            double baseStrength = ( 255 - overlayPixel.getAlpha() ) / 255.0;
            double overlayStrength = overlayPixel.getAlpha() / 255.0;
            blendPixel.setAlpha( 255 );
            for( int index = 1 ; index < 4 ; index++ )
            {
                int blendedColour = (int)( ( basePixel.getIndex( index ) * baseStrength ) + ( overlayPixel.getIndex( index ) * overlayStrength ) );
                blendPixel.setIndex( index , blendedColour );
            }

            pixels[ 0 ][ pixel ] = blendPixel.toInt();

            /*
            final int dumpPixelIndex = -1; // Debug
            if( pixel == dumpPixelIndex )
            {
                System.out.println( "original base = " + basePixel.toString() );
                System.out.println( "original over = " + overlayPixel.toString() );
                System.out.println( "final pixel   = " + blendPixel.toString() );
            }
            */
        }

        this.clearFramesTextureData();
        this.framesTextureData.add( pixels );

        // What does it mean for this to be stitched or not?
        return false;
    }
}
