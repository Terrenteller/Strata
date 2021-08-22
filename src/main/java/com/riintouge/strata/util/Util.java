package com.riintouge.strata.util;

import java.util.function.Supplier;

public class Util
{
    public static boolean isPowerOfTwo( int value )
    {
        return value > 0 && ( value & ( value - 1 ) ) == 0;
    }

    public static String[] splitKV( String line )
    {
        int index = line.indexOf( ' ' );
        return index == -1
            ? new String[] { line , "" }
            : new String[] { index == 0 ? "" : line.substring( 0 , index ) , line.substring( index + 1 ) };
    }

    public static < T > T coalesce( T a , T b )
    {
        return a != null ? a : b;
    }

    public static < T > T lazyCoalesce( T a , Supplier< T > b )
    {
        return a != null ? a : b.get();
    }

    public static < T > T argumentNullCheck( T value , String name )
    {
        if( value != null )
            return value;

        throw new IllegalArgumentException( name );
    }
}
