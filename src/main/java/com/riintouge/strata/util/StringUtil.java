package com.riintouge.strata.util;

import javax.annotation.Nullable;
import java.util.function.Function;

public final class StringUtil
{
    public static boolean isNullOrEmpty( @Nullable String value )
    {
        return value == null || value.isEmpty();
    }

    public static boolean endsWithCaseInsensitive( @Nullable String value , @Nullable String ending )
    {
        try
        {
            return value.substring( value.length() - ending.length() ).equalsIgnoreCase( ending );
        }
        catch( Exception e )
        {
            return false;
        }
    }

    public static < T > String join( String glue , Iterable< T > iterable , Function< T , String > function )
    {
        StringBuilder builder = new StringBuilder();

        boolean first = true;
        for( T item : iterable )
        {
            if( !first )
                builder.append( glue );
            first = false;

            builder.append( function.apply( item ) );
        }

        return builder.toString();
    }
}
