package com.riintouge.strata.image;

public class PixelARGB
{
    public static final int ALPHA = 0;
    public static final int RED = 1;
    public static final int GREEN = 2;
    public static final int BLUE = 3;

    protected final byte argb[] = new byte[ 4 ];

    public PixelARGB()
    {
        for( int index = 0 ; index < 4 ; index++ )
            argb[ index ] = (byte)255;
    }

    public PixelARGB( int pixel )
    {
        setValue( pixel );
    }

    public PixelARGB( byte alpha , byte red , byte green , byte blue )
    {
        setAlpha( alpha );
        setRed( red );
        setGreen( green );
        setBlue( blue );
    }

    public int getValue()
    {
        return 0
            | ( argb[ ALPHA ] << 24 ) & 0xFF000000
            | ( argb[   RED ] << 16 ) & 0x00FF0000
            | ( argb[ GREEN ] <<  8 ) & 0x0000FF00
            |   argb[  BLUE ]         & 0x000000FF;
    }

    public void setValue( int pixel )
    {
        argb[ ALPHA ] = (byte)( ( pixel >>> 24 ) & 0xFF );
        argb[   RED ] = (byte)( ( pixel >>> 16 ) & 0xFF );
        argb[ GREEN ] = (byte)( ( pixel >>>  8 ) & 0xFF );
        argb[  BLUE ] = (byte)(   pixel          & 0xFF );
    }

    public byte[] getValues()
    {
        byte[] values = new byte[ argb.length ];
        return getValues( values );
    }

    public byte[] getValues( byte[] values )
    {
        assert values.length == 4;
        System.arraycopy( argb , 0 , values , 0 , argb.length );
        return values;
    }

    public void setValues( byte[] values )
    {
        assert values.length == 4;
        System.arraycopy( values , 0 , argb , 0 , argb.length );
    }

    public byte getAlpha() { return argb[ ALPHA ]; }
    public void setAlpha( byte alpha ) { argb[ ALPHA ] = alpha; }

    public byte getRed() { return argb[ RED ]; }
    public void setRed( byte red ) { argb[ RED ] = red; }

    public byte getGreen() { return argb[ GREEN ]; }
    public void setGreen( byte green ) { argb[ GREEN ] = green; }

    public byte getBlue() { return argb[ BLUE ]; }
    public void setBlue( byte blue ) { argb[ BLUE ] = blue; }

    public byte getIndex( int index ) { return argb[ index ]; }
    public void setIndex( int index , byte value ) { argb[ index ] = value; }

    // Object overrides

    @Override
    public String toString()
    {
        return String.format(
            "PixelARGB %d ( %d , %d , %d , %d )",
            getValue(),
            getAlpha(),
            getRed(),
            getGreen(),
            getBlue() );
    }
}
