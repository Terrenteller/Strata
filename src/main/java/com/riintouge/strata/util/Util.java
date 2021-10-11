package com.riintouge.strata.util;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Supplier;
import java.util.zip.CRC32;

public class Util
{
    private static CRC32 crc32 = new CRC32();

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

    @Nullable
    public static < T > T coalesce( @Nullable T a , @Nullable T b )
    {
        return a != null ? a : b;
    }

    @Nullable
    public static < T > T lazyCoalesce( @Nullable T a , @Nonnull Supplier< T > b )
    {
        return a != null ? a : b.get();
    }

    @Nonnull
    public static < T > T argumentNullCheck( @Nullable T value , @Nonnull String name )
    {
        if( value != null )
            return value;

        throw new IllegalArgumentException( name );
    }

    public static synchronized long getCRC32( byte[] value )
    {
        crc32.reset();
        crc32.update( value );
        return crc32.getValue();
    }
}
