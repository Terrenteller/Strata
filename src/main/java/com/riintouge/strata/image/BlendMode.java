package com.riintouge.strata.image;

// BEWARE: GIMP has two sets of image blending modes: Legacy and Default.
// If you're writing new code and it doesn't match what you see in GIMP,
// fear not. You may be using the wrong mode. That being said, legacy is legacy,
// and is planned for removal. Try comparing results with other image blending
// software such as Imagemagick.

public enum BlendMode
{
    NORMAL,
    LIGHTEN,
    DARKEN;

    public int blend( int top , float opacity , int bottom )
    {
        switch( this )
        {
            case NORMAL:
                return normal( top , opacity , bottom );
            case LIGHTEN:
                return lighten( top , opacity , bottom );
            case DARKEN:
                return darken( top , opacity , bottom );
        }

        return 0;
    }

    // FIXME: Some sources say the correct formula is something like:
    // C = B < 128 ? ( 2 * A * B / 255 ) : 255 - 2 * ( 255 - A ) * ( 255 - B ) / 255
    public static int normal( int top , float opacity , int bottom )
    {
        final float opacityN = opacity / 100.0f;
        final int topAlpha = (int)( ( ( top >>> 24 ) & 0xFF ) * opacityN );
        final int topRed   =          ( top >>> 16 ) & 0xFF;
        final int topGreen =          ( top >>>  8 ) & 0xFF;
        final int topBlue  =            top          & 0xFF;

        final int bottomAlpha = ( bottom >>> 24 ) & 0xFF;
        final int bottomRed   = ( bottom >>> 16 ) & 0xFF;
        final int bottomGreen = ( bottom >>>  8 ) & 0xFF;
        final int bottomBlue  =   bottom          & 0xFF;

        final int targetAlpha = clamp( topAlpha + ( bottomAlpha * ( 255 - topAlpha ) ) );
        final float commonBottomAlphaDivisor = bottomAlpha * ( 255 - topAlpha ) / 255.0f;
        final float commonAlphaDivisor = Math.round( ( ( 255 * topAlpha ) + ( bottomAlpha * ( 255 - topAlpha ) ) ) / 255.0f );
        final int targetRed   = clamp( ( topRed   * topAlpha + bottomRed   * commonBottomAlphaDivisor ) / commonAlphaDivisor );
        final int targetGreen = clamp( ( topGreen * topAlpha + bottomGreen * commonBottomAlphaDivisor ) / commonAlphaDivisor );
        final int targetBlue  = clamp( ( topBlue  * topAlpha + bottomBlue  * commonBottomAlphaDivisor ) / commonAlphaDivisor );

        return 0
            | ( targetAlpha << 24 ) & 0xFF000000
            | ( targetRed   << 16 ) & 0x00FF0000
            | ( targetGreen <<  8 ) & 0x0000FF00
            |   targetBlue          & 0x000000FF;
    }

    // c = ( topAlpha * max( topC , bottomC ) + ( 1.0 - topAlpha ) * bottomC )
    public static int lighten( int top , float opacity , int bottom )
    {
        final float opacityN = opacity / 100.0f;
        final float topAlpha = ( ( ( top >>> 24 ) & 0xFF ) / 255.0f ) * opacityN;
        final float topRed   = (   ( top >>> 16 ) & 0xFF ) / 255.0f;
        final float topGreen = (   ( top >>>  8 ) & 0xFF ) / 255.0f;
        final float topBlue  = (     top          & 0xFF ) / 255.0f;

        final float bottomAlpha = ( ( bottom >>> 24 ) & 0xFF ) / 255.0f;
        final float bottomRed   = ( ( bottom >>> 16 ) & 0xFF ) / 255.0f;
        final float bottomGreen = ( ( bottom >>>  8 ) & 0xFF ) / 255.0f;
        final float bottomBlue  = (   bottom          & 0xFF ) / 255.0f;

        final int targetAlpha = clamp( 255.0f * bottomAlpha );
        final int targetRed   = clamp( 255.0f * ( topAlpha * ( Math.max( topRed   , bottomRed   ) ) + ( 1.0f - topAlpha ) * bottomRed   ) );
        final int targetGreen = clamp( 255.0f * ( topAlpha * ( Math.max( topGreen , bottomGreen ) ) + ( 1.0f - topAlpha ) * bottomGreen ) );
        final int targetBlue  = clamp( 255.0f * ( topAlpha * ( Math.max( topBlue  , bottomBlue  ) ) + ( 1.0f - topAlpha ) * bottomBlue  ) );

        return 0
            | ( targetAlpha << 24 ) & 0xFF000000
            | ( targetRed   << 16 ) & 0x00FF0000
            | ( targetGreen <<  8 ) & 0x0000FF00
            |   targetBlue          & 0x000000FF;
    }

    // c = ( topAlpha * min( topC , bottomC ) + ( 1.0 - topAlpha ) * bottomC )
    public static int darken( int top , float opacity , int bottom )
    {
        final float opacityN = opacity / 100.0f;
        final float topAlpha = ( ( ( top >>> 24 ) & 0xFF ) / 255.0f ) * opacityN;
        final float topRed   = (   ( top >>> 16 ) & 0xFF ) / 255.0f;
        final float topGreen = (   ( top >>>  8 ) & 0xFF ) / 255.0f;
        final float topBlue  = (     top          & 0xFF ) / 255.0f;

        final float bottomAlpha = ( ( bottom >>> 24 ) & 0xFF ) / 255.0f;
        final float bottomRed   = ( ( bottom >>> 16 ) & 0xFF ) / 255.0f;
        final float bottomGreen = ( ( bottom >>>  8 ) & 0xFF ) / 255.0f;
        final float bottomBlue  = (   bottom          & 0xFF ) / 255.0f;

        final int targetAlpha = clamp( 255.0f * bottomAlpha );
        final int targetRed   = clamp( 255.0f * ( topAlpha * ( Math.min( topRed   , bottomRed   ) ) + ( 1.0f - topAlpha ) * bottomRed   ) );
        final int targetGreen = clamp( 255.0f * ( topAlpha * ( Math.min( topGreen , bottomGreen ) ) + ( 1.0f - topAlpha ) * bottomGreen ) );
        final int targetBlue  = clamp( 255.0f * ( topAlpha * ( Math.min( topBlue  , bottomBlue  ) ) + ( 1.0f - topAlpha ) * bottomBlue  ) );

        return 0
            | ( targetAlpha << 24 ) & 0xFF000000
            | ( targetRed   << 16 ) & 0x00FF0000
            | ( targetGreen <<  8 ) & 0x0000FF00
            |   targetBlue          & 0x000000FF;
    }

    private static int clamp( float value )
    {
        return clamp( Math.round( value ) );
    }

    private static int clamp( int value )
    {
        return value < 0 ? 0 : ( value > 255 ? 255 : value );
    }
}
