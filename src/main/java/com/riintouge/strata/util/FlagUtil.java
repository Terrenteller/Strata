package com.riintouge.strata.util;

public class FlagUtil
{
    public static boolean check( byte operand , byte flags )
    {
        return ( operand & flags ) == flags;
    }

    public static byte set( byte operand , byte flags , boolean value )
    {
        return (byte)( value ? ( operand | flags ) : ( operand & ~flags ) );
    }
}
