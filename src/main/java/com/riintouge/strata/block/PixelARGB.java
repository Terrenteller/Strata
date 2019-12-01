package com.riintouge.strata.block;

public class PixelARGB
{
    private int argb[] = new int[ 4 ];

    public PixelARGB()
    {
        for( int index = 0 ; index < 4 ; index++ )
            argb[ index ] = 255;
    }

    public PixelARGB( int pixel )
    {
        setPixel( pixel );
    }

    public void setPixel( int pixel ) { argb = decompose( pixel ); }

    public int getAlpha() { return argb[ 0 ]; }
    public void setAlpha( int alpha ) { argb[ 0 ] = clamp( alpha ); }

    public int getRed() { return argb[ 1 ]; }
    public void setRed( int red ) { argb[ 1 ] = clamp( red ); }

    public int getGreen() { return argb[ 2 ]; }
    public void setGreen( int green ) { argb[ 2 ] = clamp( green ); }

    public int getBlue() { return argb[ 3 ]; }
    public void setBlue( int blue ) { argb[ 3 ] = clamp( blue ); }

    public int getIndex( int index ) { return argb[ index ]; }
    public void setIndex( int index , int value ) { argb[ index ] = value; }

    public int toInt()
    {
        int pixel = 0;
        pixel |= ( argb[ 0 ] << 24 ) & 0xFF000000;
        pixel |= ( argb[ 1 ] << 16 ) & 0x00FF0000;
        pixel |= ( argb[ 2 ] << 8 ) & 0x0000FF00;
        pixel |= ( argb[ 3 ] ) & 0x000000FF;

        return pixel;
    }

    private int clamp( int value ) { return value < 0 ? 0 : ( value > 255 ? 255 : value ); }

    private int[] decompose( int pixel )
    {
        int alpha = ( pixel >>> 24 ) & 0xFF;
        int red = ( pixel >>> 16 ) & 0xFF;
        int green = ( pixel >>> 8 ) & 0xFF;
        int blue = pixel & 0xFF;

        return new int[] { alpha , red , green , blue };
    }

    // Object overrides

    @Override
    public String toString()
    {
        return String.format(
            "PixelARGB %d ( %d , %d , %d , %d )",
            toInt(),
            getAlpha(),
            getRed(),
            getGreen(),
            getBlue() );
    }
}
