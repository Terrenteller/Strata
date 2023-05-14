package com.riintouge.strata.util;

import javax.annotation.Nullable;

public class StringUtil
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
}
