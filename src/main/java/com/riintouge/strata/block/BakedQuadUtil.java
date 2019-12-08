package com.riintouge.strata.block;

import com.google.common.primitives.Ints;
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

    // In order of EnumFacing: down up north south west east
    // Points are in order counter-clockwise starting from the top left
    public final static Vector3f[] SIDE_DOWN  = new Vector3f[] { BOTTOM_LEFT_FRONT , BOTTOM_LEFT_BACK   , BOTTOM_RIGHT_BACK  , BOTTOM_RIGHT_FRONT };
    public final static Vector3f[] SIDE_UP    = new Vector3f[] { TOP_LEFT_BACK     , TOP_LEFT_FRONT     , TOP_RIGHT_FRONT    , TOP_RIGHT_BACK     };
    public final static Vector3f[] SIDE_NORTH = new Vector3f[] { TOP_RIGHT_BACK    , BOTTOM_RIGHT_BACK  , BOTTOM_LEFT_BACK   , TOP_LEFT_BACK      };
    public final static Vector3f[] SIDE_SOUTH = new Vector3f[] { TOP_LEFT_FRONT    , BOTTOM_LEFT_FRONT  , BOTTOM_RIGHT_FRONT , TOP_RIGHT_FRONT    };
    public final static Vector3f[] SIDE_WEST  = new Vector3f[] { TOP_LEFT_BACK     , BOTTOM_LEFT_BACK   , BOTTOM_LEFT_FRONT  , TOP_LEFT_FRONT     };
    public final static Vector3f[] SIDE_EAST  = new Vector3f[] { TOP_RIGHT_FRONT   , BOTTOM_RIGHT_FRONT , BOTTOM_RIGHT_BACK  , TOP_RIGHT_BACK     };

    public final static Vector3f[][] SIDE_POINTS = new Vector3f[][] { SIDE_DOWN , SIDE_UP , SIDE_NORTH , SIDE_SOUTH , SIDE_WEST , SIDE_EAST };

    public static BakedQuad createBakedQuadForFace( int itemRenderLayer , TextureAtlasSprite texture , EnumFacing face )
    {
        Vector3f[] sidePoints = SIDE_POINTS[ face.getIndex() ];

        return new BakedQuad(
            sideToVertexData( sidePoints , texture ),
            itemRenderLayer,
            face,
            texture,
            true,
            net.minecraft.client.renderer.vertex.DefaultVertexFormats.ITEM );
    }

    private static int[] sideToVertexData( Vector3f[] vec , TextureAtlasSprite texture )
    {
        int packedNormal = computePackedNormal( vec[ 0 ] , vec[ 1 ] , vec[ 2 ] , vec[ 3 ] );
        float width = texture.getIconWidth();
        float height = texture.getIconHeight();

        return Ints.concat(
            vertexToInts( vec[ 0 ] , Color.WHITE.getRGB() , texture ,     0 ,      0 , packedNormal ),
            vertexToInts( vec[ 1 ] , Color.WHITE.getRGB() , texture ,     0 , height , packedNormal ),
            vertexToInts( vec[ 2 ] , Color.WHITE.getRGB() , texture , width , height , packedNormal ),
            vertexToInts( vec[ 3 ] , Color.WHITE.getRGB() , texture , width ,      0 , packedNormal ) );
    }

    private static int[] vertexToInts( Vector3f vec , int color , TextureAtlasSprite texture , float u , float v , int normal )
    {
        return vertexToInts( vec.x , vec.y , vec.z , color , texture , u , v , normal );
    }

    private static int[] vertexToInts( float x , float y , float z , int color , TextureAtlasSprite texture , float u , float v , int normal )
    {
        return new int[] {
            Float.floatToRawIntBits( x ),
            Float.floatToRawIntBits( y ),
            Float.floatToRawIntBits( z ),
            color,
            Float.floatToRawIntBits( texture.getInterpolatedU( u ) ),
            Float.floatToRawIntBits( texture.getInterpolatedV( v ) ),
            normal
        };
    }

    // Calculate the normal vector based on four input coordinates.
    // Assumes that the quad is coplanar but should produce a 'reasonable' answer even if not.
    private static int computePackedNormal( Vector3f topLeft , Vector3f bottomLeft , Vector3f bottomRight , Vector3f topRight )
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
