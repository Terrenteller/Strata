package com.riintouge.strata.util;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.EnumFacing;

import javax.vecmath.Vector3f;
import java.awt.*;

// This code is heavily modified from TheGreyGhosts's wonderful repo here:
// https://github.com/TheGreyGhost/MinecraftByExample

public class BakedQuadUtil
{
    public final static float AXIS_MIN = 0.0f;
    public final static float AXIS_MAX = 1.0f;

    public final static Vector3f TOP_LEFT_FRONT     = new Vector3f( AXIS_MIN , AXIS_MAX , AXIS_MAX );
    public final static Vector3f TOP_LEFT_BACK      = new Vector3f( AXIS_MIN , AXIS_MAX , AXIS_MIN );
    public final static Vector3f BOTTOM_LEFT_FRONT  = new Vector3f( AXIS_MIN , AXIS_MIN , AXIS_MAX );
    public final static Vector3f BOTTOM_LEFT_BACK   = new Vector3f( AXIS_MIN , AXIS_MIN , AXIS_MIN );
    public final static Vector3f BOTTOM_RIGHT_FRONT = new Vector3f( AXIS_MAX , AXIS_MIN , AXIS_MAX );
    public final static Vector3f BOTTOM_RIGHT_BACK  = new Vector3f( AXIS_MAX , AXIS_MIN , AXIS_MIN );
    public final static Vector3f TOP_RIGHT_FRONT    = new Vector3f( AXIS_MAX , AXIS_MAX , AXIS_MAX );
    public final static Vector3f TOP_RIGHT_BACK     = new Vector3f( AXIS_MAX , AXIS_MAX , AXIS_MIN );

    public final static int[] RAW_TOP_LEFT_FRONT     = new int[] { Float.floatToRawIntBits( AXIS_MIN ) , Float.floatToRawIntBits( AXIS_MAX ) , Float.floatToRawIntBits( AXIS_MAX ) };
    public final static int[] RAW_TOP_LEFT_BACK      = new int[] { Float.floatToRawIntBits( AXIS_MIN ) , Float.floatToRawIntBits( AXIS_MAX ) , Float.floatToRawIntBits( AXIS_MIN ) };
    public final static int[] RAW_BOTTOM_LEFT_FRONT  = new int[] { Float.floatToRawIntBits( AXIS_MIN ) , Float.floatToRawIntBits( AXIS_MIN ) , Float.floatToRawIntBits( AXIS_MAX ) };
    public final static int[] RAW_BOTTOM_LEFT_BACK   = new int[] { Float.floatToRawIntBits( AXIS_MIN ) , Float.floatToRawIntBits( AXIS_MIN ) , Float.floatToRawIntBits( AXIS_MIN ) };
    public final static int[] RAW_BOTTOM_RIGHT_FRONT = new int[] { Float.floatToRawIntBits( AXIS_MAX ) , Float.floatToRawIntBits( AXIS_MIN ) , Float.floatToRawIntBits( AXIS_MAX ) };
    public final static int[] RAW_BOTTOM_RIGHT_BACK  = new int[] { Float.floatToRawIntBits( AXIS_MAX ) , Float.floatToRawIntBits( AXIS_MIN ) , Float.floatToRawIntBits( AXIS_MIN ) };
    public final static int[] RAW_TOP_RIGHT_FRONT    = new int[] { Float.floatToRawIntBits( AXIS_MAX ) , Float.floatToRawIntBits( AXIS_MAX ) , Float.floatToRawIntBits( AXIS_MAX ) };
    public final static int[] RAW_TOP_RIGHT_BACK     = new int[] { Float.floatToRawIntBits( AXIS_MAX ) , Float.floatToRawIntBits( AXIS_MAX ) , Float.floatToRawIntBits( AXIS_MIN ) };

    // In order of EnumFacing: down up north south west east
    // Points are in order counter-clockwise starting from the top left
    public final static Vector3f[] SIDE_DOWN  = new Vector3f[] { BOTTOM_LEFT_FRONT , BOTTOM_LEFT_BACK   , BOTTOM_RIGHT_BACK  , BOTTOM_RIGHT_FRONT };
    public final static Vector3f[] SIDE_UP    = new Vector3f[] { TOP_LEFT_BACK     , TOP_LEFT_FRONT     , TOP_RIGHT_FRONT    , TOP_RIGHT_BACK     };
    public final static Vector3f[] SIDE_NORTH = new Vector3f[] { TOP_RIGHT_BACK    , BOTTOM_RIGHT_BACK  , BOTTOM_LEFT_BACK   , TOP_LEFT_BACK      };
    public final static Vector3f[] SIDE_SOUTH = new Vector3f[] { TOP_LEFT_FRONT    , BOTTOM_LEFT_FRONT  , BOTTOM_RIGHT_FRONT , TOP_RIGHT_FRONT    };
    public final static Vector3f[] SIDE_WEST  = new Vector3f[] { TOP_LEFT_BACK     , BOTTOM_LEFT_BACK   , BOTTOM_LEFT_FRONT  , TOP_LEFT_FRONT     };
    public final static Vector3f[] SIDE_EAST  = new Vector3f[] { TOP_RIGHT_FRONT   , BOTTOM_RIGHT_FRONT , BOTTOM_RIGHT_BACK  , TOP_RIGHT_BACK     };
    public final static Vector3f[][] SIDE_POINTS = new Vector3f[][] { SIDE_DOWN , SIDE_UP , SIDE_NORTH , SIDE_SOUTH , SIDE_WEST , SIDE_EAST };

    public final static int[][] RAW_SIDE_DOWN  = new int[][] { RAW_BOTTOM_LEFT_FRONT , RAW_BOTTOM_LEFT_BACK   , RAW_BOTTOM_RIGHT_BACK  , RAW_BOTTOM_RIGHT_FRONT };
    public final static int[][] RAW_SIDE_UP    = new int[][] { RAW_TOP_LEFT_BACK     , RAW_TOP_LEFT_FRONT     , RAW_TOP_RIGHT_FRONT    , RAW_TOP_RIGHT_BACK     };
    public final static int[][] RAW_SIDE_NORTH = new int[][] { RAW_TOP_RIGHT_BACK    , RAW_BOTTOM_RIGHT_BACK  , RAW_BOTTOM_LEFT_BACK   , RAW_TOP_LEFT_BACK      };
    public final static int[][] RAW_SIDE_SOUTH = new int[][] { RAW_TOP_LEFT_FRONT    , RAW_BOTTOM_LEFT_FRONT  , RAW_BOTTOM_RIGHT_FRONT , RAW_TOP_RIGHT_FRONT    };
    public final static int[][] RAW_SIDE_WEST  = new int[][] { RAW_TOP_LEFT_BACK     , RAW_BOTTOM_LEFT_BACK   , RAW_BOTTOM_LEFT_FRONT  , RAW_TOP_LEFT_FRONT     };
    public final static int[][] RAW_SIDE_EAST  = new int[][] { RAW_TOP_RIGHT_FRONT   , RAW_BOTTOM_RIGHT_FRONT , RAW_BOTTOM_RIGHT_BACK  , RAW_TOP_RIGHT_BACK     };
    public final static int[][][] RAW_SIDE_POINTS = new int[][][] { RAW_SIDE_DOWN , RAW_SIDE_UP , RAW_SIDE_NORTH , RAW_SIDE_SOUTH , RAW_SIDE_WEST , RAW_SIDE_EAST };

    public final static int[] PACKED_NORMALS = new int[] { 0x8100 , 0x7F00 , 0x810000 , 0x7F0000 , 0x81 , 0x7F };

    public static BakedQuad createBakedQuadForFace( int itemRenderLayer , TextureAtlasSprite texture , EnumFacing face )
    {
        return new BakedQuad(
            sideToVertexData( face , texture ),
            itemRenderLayer,
            face,
            texture,
            true,
            net.minecraft.client.renderer.vertex.DefaultVertexFormats.ITEM );
    }

    public static int[] sideToVertexData( EnumFacing face , TextureAtlasSprite texture )
    {
        int[][] rawSidePoints = RAW_SIDE_POINTS[ face.getIndex() ];
        int white = Color.WHITE.getRGB();
        int interpolatedZeroWidth = Float.floatToRawIntBits( texture.getInterpolatedU( 0 ) );
        int interpolatedZeroHeight = Float.floatToRawIntBits( texture.getInterpolatedV( 0 ) );
        int interpolatedWidth = Float.floatToRawIntBits( texture.getInterpolatedU( texture.getIconWidth() ) );
        int interpolatedHeight = Float.floatToRawIntBits( texture.getInterpolatedV( texture.getIconHeight() ) );
        int packedNormal = PACKED_NORMALS[ face.getIndex() ];

        return new int[] {
            rawSidePoints[ 0 ][ 0 ] , rawSidePoints[ 0 ][ 1 ] , rawSidePoints[ 0 ][ 2 ] , white , interpolatedZeroWidth , interpolatedZeroHeight , packedNormal ,
            rawSidePoints[ 1 ][ 0 ] , rawSidePoints[ 1 ][ 1 ] , rawSidePoints[ 1 ][ 2 ] , white , interpolatedZeroWidth , interpolatedHeight , packedNormal ,
            rawSidePoints[ 2 ][ 0 ] , rawSidePoints[ 2 ][ 1 ] , rawSidePoints[ 2 ][ 2 ] , white , interpolatedWidth , interpolatedHeight , packedNormal ,
            rawSidePoints[ 3 ][ 0 ] , rawSidePoints[ 3 ][ 1 ] , rawSidePoints[ 3 ][ 2 ] , white , interpolatedWidth , interpolatedZeroHeight , packedNormal
        };
    }

    public static int[] vertexToInts( Vector3f vec , int color , TextureAtlasSprite texture , float u , float v , int packedNormal )
    {
        return vertexToInts( vec.x , vec.y , vec.z , color , texture , u , v , packedNormal );
    }

    public static int[] vertexToInts( float x , float y , float z , int color , TextureAtlasSprite texture , float u , float v , int packedNormal )
    {
        return new int[] {
            Float.floatToRawIntBits( x ),
            Float.floatToRawIntBits( y ),
            Float.floatToRawIntBits( z ),
            color,
            Float.floatToRawIntBits( texture.getInterpolatedU( u ) ),
            Float.floatToRawIntBits( texture.getInterpolatedV( v ) ),
            packedNormal
        };
    }

    // Calculate the normal vector based on four input coordinates.
    // Assumes that the quad is coplanar but should produce a 'reasonable' answer even if not.
    public static int computePackedNormal( Vector3f topLeft , Vector3f bottomLeft , Vector3f bottomRight , Vector3f topRight )
    {
        Vector3f cornerAverage1 = new Vector3f( bottomRight );
        Vector3f cornerAverage2 = new Vector3f( topRight );
        Vector3f normal = new Vector3f();

        cornerAverage1.sub( topLeft );
        cornerAverage2.sub( bottomLeft );
        normal.cross( cornerAverage1 , cornerAverage2 );
        normal.normalize();

        int x = ( (int)( normal.x * 127 ) ) & 0xFF;
        int y = ( (int)( normal.y * 127 ) ) & 0xFF;
        int z = ( (int)( normal.z * 127 ) ) & 0xFF;
        return ( z << 16 ) | ( y << 8 ) | x;
    }
}
