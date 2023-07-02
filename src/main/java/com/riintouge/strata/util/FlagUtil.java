package com.riintouge.strata.util;

public final class FlagUtil
{
    // byte

    public static boolean check( byte operand , byte flags )
    {
        return ( operand & flags ) != 0;
    }

    public static byte set( byte operand , byte flags , boolean value )
    {
        return (byte)( value ? ( operand | flags ) : ( operand & ~flags ) );
    }

    // short

    public static boolean check( short operand , short flags )
    {
        return ( operand & flags ) != 0;
    }

    public static short set( short operand , short flags , boolean value )
    {
        return (short)( value ? ( operand | flags ) : ( operand & ~flags ) );
    }

    // int

    public static boolean check( int operand , int flags )
    {
        return ( operand & flags ) != 0;
    }

    public static int set( int operand , int flags , boolean value )
    {
        return ( value ? ( operand | flags ) : ( operand & ~flags ) );
    }

    // long

    public static boolean check( long operand , long flags )
    {
        return ( operand & flags ) != 0;
    }

    public static long set( long operand , long flags , boolean value )
    {
        return value ? ( operand | flags ) : ( operand & ~flags );
    }
}
