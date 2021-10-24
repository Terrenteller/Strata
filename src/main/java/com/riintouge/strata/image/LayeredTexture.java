package com.riintouge.strata.image;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.riintouge.strata.util.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.Vector;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

@SideOnly( Side.CLIENT )
public class LayeredTexture extends TextureAtlasSprite
{
    protected static final Cache< ResourceLocation , SquareMipmapHelper > MIPMAP_CACHE = CacheBuilder.newBuilder()
        .expireAfterWrite( 1 , TimeUnit.MINUTES )
        .build();

    protected final LayeredTextureLayer[] layers;

    public LayeredTexture( ResourceLocation registryName , LayeredTextureLayer[] layers )
    {
        super( registryName.toString() );
        this.layers = layers;
    }

    public SquareMipmapHelper getOrCreateMipmapHelper( ResourceLocation resourceLocation , int[][] originalMipmaps )
    {
        SquareMipmapHelper mipmapHelper = MIPMAP_CACHE.getIfPresent( resourceLocation );
        if( mipmapHelper == null )
        {
            mipmapHelper = new SquareMipmapHelper( originalMipmaps );
            MIPMAP_CACHE.put( resourceLocation , mipmapHelper );
        }

        return mipmapHelper;
    }

    protected int[] blend( BlendMode blendMode , int[] top , float opacity , int[] bottom )
    {
        assert top.length == bottom.length : "Cannot blend pixel data of different lengths!";

        int[] blendedPixels = new int[ bottom.length ];
        for( int index = 0 ; index < bottom.length ; index++ )
            blendedPixels[ index ] = blendMode.blend( top[ index ] , opacity , bottom[ index ] );

        return blendedPixels;
    }

    // TextureAtlasSprite overrides

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
        if( layers.length == 1 )
        {
            TextureAtlasSprite texture = textureGetter.apply( layers[ 0 ].resource );
            width = texture.getIconWidth();
            height = texture.getIconHeight();

            this.clearFramesTextureData();
            for( int index = 0 ; index < texture.getFrameCount() ; index++ )
                this.framesTextureData.add( texture.getFrameTextureData( index ) );
        }
        else if( layers.length > 1 )
        {
            SquareMipmapHelper[] mipmapHelpers = new SquareMipmapHelper[ layers.length ];

            for( int index = 0 ; index < layers.length ; index++ )
            {
                ResourceLocation layerResource = layers[ index ].resource;
                TextureAtlasSprite layerTexture = textureGetter.apply( layerResource );

                if( layerTexture.getFrameCount() == 0 )
                    throw new IllegalArgumentException(
                        String.format(
                            "Layered texture resource \"%s\" has no frames!",
                            layerResource.toString() ) );

                if( layerTexture.getIconWidth() != layerTexture.getIconHeight() )
                    throw new IllegalArgumentException(
                        String.format(
                            "Layered texture resource \"%s\" must be square!",
                            layerResource.toString() ) );

                if( !Util.isPowerOfTwo( layerTexture.getIconWidth() ) )
                    throw new IllegalArgumentException(
                        String.format(
                            "Layered texture resource \"%s\" size must be a power of two!",
                            layerResource.toString() ) );

                width = height = ( index == 0 ? layerTexture.getIconWidth() : Math.min( width , layerTexture.getIconWidth() ) );
                mipmapHelpers[ index ] = getOrCreateMipmapHelper( layerResource , layerTexture.getFrameTextureData( 0 ) );
            }

            SquareMipmapHelper baseLayerMipmapHelper = mipmapHelpers[ layers.length - 1 ];
            int[][] result = new int[ Minecraft.getMinecraft().gameSettings.mipmapLevels + 1 ][];
            result[ 0 ] = baseLayerMipmapHelper.mipmapForEdgeLength( width );

            for( int overlayIndex = layers.length - 2 ; overlayIndex >= 0 ; overlayIndex-- )
            {
                result[ 0 ] = blend(
                    layers[ overlayIndex ].blendMode,
                    mipmapHelpers[ overlayIndex ].mipmapForEdgeLength( width ),
                    layers[ overlayIndex ].opacity,
                    result[ 0 ] );
            }

            this.clearFramesTextureData();
            this.framesTextureData.add( result );
        }

        // What does it mean for this to be stitched or not?
        return false;
    }
}
