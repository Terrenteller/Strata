package com.riintouge.strata.util;

import javax.annotation.Nullable;

public class EnumUtil
{
    @Nullable
    public static < T extends Enum< T > > T valueOfOrNull( Class< T > clazz , @Nullable String value )
    {
        if( !StringUtil.isNullOrEmpty( value ) )
            for( T enumConstant : clazz.getEnumConstants() )
                if( enumConstant.name().equalsIgnoreCase( value ) )
                    return enumConstant;

        return null;
    }
}
