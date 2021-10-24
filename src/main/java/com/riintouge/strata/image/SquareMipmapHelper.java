package com.riintouge.strata.image;

import com.riintouge.strata.util.Util;
import net.minecraft.client.renderer.texture.TextureUtil;

public class SquareMipmapHelper
{
    public final int[][] allMipmaps = new int[ 32 ][];

    public SquareMipmapHelper( int[][] mipmaps )
    {
        int smallestMipmapIndex = 31;
        for( int[] mipmap : mipmaps )
        {
            if( mipmap.length == 0 )
                continue;

            int edgeLength = Util.squareRootOfPowerOfTwo( mipmap.length );
            int mipmapIndex = Util.whichPowerOfTwo( edgeLength );
            allMipmaps[ mipmapIndex ] = mipmap;
            smallestMipmapIndex = Math.min( smallestMipmapIndex , mipmapIndex );
        }

        if( smallestMipmapIndex > 0 )
        {
            int smallestMipmapEdgeLength = 1 << smallestMipmapIndex;
            int[][] requestedMipmaps = new int[ smallestMipmapIndex + 1 ][];
            requestedMipmaps[ 0 ] = allMipmaps[ smallestMipmapIndex ];
            int[][] missingMipmaps = TextureUtil.generateMipmapData( smallestMipmapIndex , smallestMipmapEdgeLength , requestedMipmaps );

            for( int index = 0 ; index < smallestMipmapIndex ; index++ )
                allMipmaps[ index ] = missingMipmaps[ smallestMipmapIndex - index ];
        }
    }

    public int[] mipmapForEdgeLength( int edgeLength )
    {
        return allMipmaps[ Util.whichPowerOfTwo( edgeLength ) ];
    }
}
