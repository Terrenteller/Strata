package com.riintouge.strata.image;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.riintouge.strata.Strata;
import com.riintouge.strata.util.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

@SideOnly( Side.CLIENT )
public class LayeredTexture extends TextureAtlasSprite
{
    protected static final Cache< ResourceLocation , SquareMipmapHelper > MIPMAP_CACHE;
    protected static final Pair< int[][] , Integer > MISSING_FRAME_INFO;

    protected final LayeredTextureLayer[] layers;

    static
    {
        MIPMAP_CACHE = CacheBuilder.newBuilder().expireAfterWrite( 1 , TimeUnit.MINUTES ).build();

        int[][] frame = new int[ Minecraft.getMinecraft().gameSettings.mipmapLevels + 1 ][];
        frame[ 0 ] = TextureUtil.MISSING_TEXTURE_DATA;
        int edgeLength = Util.squareRootOfPowerOfTwo( TextureUtil.MISSING_TEXTURE_DATA.length );

        MISSING_FRAME_INFO = new ImmutablePair<>( frame , edgeLength );
    }

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
            ResourceLocation resource = layers[ 0 ].resource;
            TextureAtlasSprite texture = textureGetter.apply( resource );
            clearFramesTextureData();

            if( texture.getFrameCount() > 0 )
            {
                width = texture.getIconWidth();
                height = texture.getIconHeight();

                for( int index = 0 ; index < texture.getFrameCount() ; index++ )
                    framesTextureData.add( texture.getFrameTextureData( index ) );
            }
            else
            {
                Pair< int[][] , Integer > firstFrameInfo = getFirstFrameInfoOrMissing( resource , texture );
                width = firstFrameInfo.getValue();
                height = firstFrameInfo.getValue();
                framesTextureData.add( firstFrameInfo.getKey() );
            }
        }
        else if( layers.length > 1 )
        {
            SquareMipmapHelper[] mipmapHelpers = new SquareMipmapHelper[ layers.length ];

            for( int index = 0 ; index < layers.length ; index++ )
            {
                ResourceLocation layerResource = layers[ index ].resource;
                TextureAtlasSprite layerTexture = textureGetter.apply( layerResource );
                Pair< int[][] , Integer > firstFrameInfo = getFirstFrameInfoOrMissing( layerResource , layerTexture );
                width = height = ( index == 0 ? firstFrameInfo.getValue() : Math.min( width , firstFrameInfo.getValue() ) );
                mipmapHelpers[ index ] = getOrCreateMipmapHelper( layerResource , firstFrameInfo.getKey() );
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

            clearFramesTextureData();
            framesTextureData.add( result );
        }

        // What does it mean for this to be stitched or not?
        return false;
    }

    // Statics

    public static Pair< int[][] , Integer > getFirstFrameInfoOrMissing( ResourceLocation resource , TextureAtlasSprite texture )
    {
        try
        {
            // Unloading a resource pack supplying the only instance of a necessary texture
            // will result in a non-null, 0x0 texture with non-null, zero-length data.
            // Attempting to index into no frames will result in an IndexOutOfBoundsException.
            if( texture.getFrameCount() == 0 )
                throw new IllegalArgumentException(
                    String.format(
                        "Layered texture resource \"%s\" has no frames! Does the resource exist? Did it get removed?",
                        resource.toString() ) );

            if( texture.getIconWidth() != texture.getIconHeight() )
                throw new IllegalArgumentException(
                    String.format(
                        "Layered texture resource \"%s\" must be square!",
                        resource.toString() ) );

            if( !Util.isPowerOfTwo( texture.getIconWidth() ) )
                throw new IllegalArgumentException(
                    String.format(
                        "Layered texture resource \"%s\" size must be a power of two!",
                        resource.toString() ) );

            return new ImmutablePair<>( texture.getFrameTextureData( 0 ) , texture.getIconWidth() );
        }
        catch( Exception e )
        {
            Strata.LOGGER.error( e.getMessage() );
        }

        return MISSING_FRAME_INFO;
    }
}
