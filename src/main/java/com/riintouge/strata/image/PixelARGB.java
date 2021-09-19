package com.riintouge.strata.image;

public class PixelARGB
{
    private static final int ALPHA = 0;
    private static final int RED = 1;
    private static final int GREEN = 2;
    private static final int BLUE = 3;

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

    public PixelARGB( int alpha , int red , int green , int blue )
    {
        setAlpha( alpha );
        setRed( red );
        setGreen( green );
        setBlue( blue );
    }

    public void setPixel( int pixel ) { argb = decompose( pixel ); }

    public int getAlpha() { return argb[ ALPHA ]; }
    public void setAlpha( int alpha ) { argb[ ALPHA ] = clamp( alpha ); }

    public int getRed() { return argb[ RED ]; }
    public void setRed( int red ) { argb[ RED ] = clamp( red ); }

    public int getGreen() { return argb[ GREEN ]; }
    public void setGreen( int green ) { argb[ GREEN ] = clamp( green ); }

    public int getBlue() { return argb[ BLUE ]; }
    public void setBlue( int blue ) { argb[ BLUE ] = clamp( blue ); }

    public int getIndex( int index ) { return argb[ index ]; }
    public void setIndex( int index , int value ) { argb[ index ] = value; }

    public int toInt()
    {
        int pixel = 0;
        pixel |= ( argb[ ALPHA ] << 24 ) & 0xFF000000;
        pixel |= ( argb[ RED ] << 16 ) & 0x00FF0000;
        pixel |= ( argb[ GREEN ] << 8 ) & 0x0000FF00;
        pixel |= ( argb[ BLUE ] ) & 0x000000FF;

        return pixel;
    }

    private int clamp( int value )
    {
        return value < 0 ? 0 : ( value > 255 ? 255 : value );
    }

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
