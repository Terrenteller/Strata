package com.riintouge.strata.util;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.EnumFacing;

import java.awt.*;

public class OverlayBakedQuadUtil
{
    // Use a minuscule difference to hide Z-fighting. Another solution would be to compute sides individually
    // to not bleed onto adjacent, parallel faces, but that would introduce gaps on edges.
    public final static float AXIS_MIN = 0.0f - 0.001f;
    public final static float AXIS_MAX = 1.0f + 0.001f;

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
}
