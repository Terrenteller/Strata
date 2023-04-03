package com.riintouge.strata.util;

import com.sun.istack.internal.NotNull;

import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ReflectionUtil
{
    @Nullable
    public static Field findFieldByType( Class clazz , Class type , boolean allowAssignable )
    {
        for( Class curClazz = clazz ; curClazz != Object.class ; curClazz = curClazz.getSuperclass() )
            for( Field field : curClazz.getDeclaredFields() )
                if( field.getType().equals( type ) || ( allowAssignable && field.getType().isAssignableFrom( type ) ) )
                    return field;

        return null;
    }

    @NotNull
    public static List< Field > findFieldsByType( Class clazz , Class type , boolean allowAssignable )
    {
        List< Field > fields = new ArrayList<>();

        for( Class curClazz = clazz ; curClazz != Object.class ; curClazz = curClazz.getSuperclass() )
            for( Field field : curClazz.getDeclaredFields() )
                if( field.getType().equals( type ) || ( allowAssignable && field.getType().isAssignableFrom( type ) ) )
                    fields.add( field );

        return fields;
    }

    @Nullable
    public static Method findMethodByTypes(
        Class< ? > clazz,
        Class< ? > returnType,
        boolean allowAssignableTypes,
        Class< ? > ... parameters )
    {
        for( Class curClazz = clazz ; curClazz != Object.class ; curClazz = curClazz.getSuperclass() )
        {
            for( Method method : curClazz.getDeclaredMethods() )
            {
                Class< ? > methodReturnType = method.getReturnType();
                Class< ? >[] methodParameterTypes = method.getParameterTypes();

                if( !allowAssignableTypes )
                {
                    if( returnType.equals( methodReturnType ) && Arrays.equals( parameters , methodParameterTypes ) )
                        return method;

                    continue;
                }

                // Assignable direction is important
                if( !returnType.isAssignableFrom( methodReturnType ) )
                    continue;
                else if( parameters == null )
                    return method;
                else if( methodParameterTypes.length != parameters.length )
                    continue;

                for( int index = 0 ; ; index++ )
                    if( index == methodParameterTypes.length )
                        return method;
                    else if( !methodParameterTypes[ index ].isAssignableFrom( parameters[ index ] ) )
                        break;
            }
        }

        return null;
    }

    @Nullable
    public static Boolean unfinalizeField( Field field )
    {
        Boolean wasFinal = null;

        try
        {
            Field modifiersField = Field.class.getDeclaredField( "modifiers" );
            modifiersField.setAccessible( true );
            wasFinal = FlagUtil.check( field.getModifiers() , Modifier.FINAL );
            modifiersField.setInt( field , field.getModifiers() & ~Modifier.FINAL );
        }
        catch( Exception e )
        {
            // Ignore
        }

        return wasFinal;
    }
}
