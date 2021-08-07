package com.riintouge.strata.util;

import com.sun.org.apache.bcel.internal.Repository;
import com.sun.org.apache.bcel.internal.classfile.JavaClass;
import com.sun.org.apache.bcel.internal.generic.Type;

import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.stream.Collectors;

public class ReflectionUtil
{
    public static Field findFieldByType( Class clazz , Class type , boolean allowAssignable )
    {
        for( Class curClazz = clazz ; curClazz != Object.class ; curClazz = curClazz.getSuperclass() )
        {
            for( Field field : curClazz.getDeclaredFields() )
            {
                if( field.getType().equals( type ) )
                    return field;

                if( allowAssignable && field.getType().isAssignableFrom( IdentityHashMap.class ) )
                    return field;
            }
        }

        return null;
    }

    public static Method findMethodByTypes(
        Class< ? > clazz,
        @Nullable Boolean methodIsAbstract,
        Class< ? > returnType,
        boolean nonAbstractAllowAssignableTypes,
        Class< ? > ... parameters )
    {
        if( methodIsAbstract == null || !methodIsAbstract )
        {
            for( Class curClazz = clazz ; curClazz != Object.class ; curClazz = curClazz.getSuperclass() )
            {
                for( Method method : curClazz.getDeclaredMethods() )
                {
                    Class< ? > methodReturnType = method.getReturnType();
                    Class< ? >[] methodParameterTypes = method.getParameterTypes();

                    if( !nonAbstractAllowAssignableTypes )
                    {
                        if( returnType.equals( methodReturnType ) && Arrays.equals( parameters , methodParameterTypes ) )
                            return method;

                        break;
                    }

                    // Assignable direction is important
                    if( !returnType.isAssignableFrom( methodReturnType ) )
                        break;
                    else if( parameters == null )
                        return method;
                    else if( methodParameterTypes.length != parameters.length )
                        break;

                    for( int index = 0 ; ; index++ )
                        if( index == methodParameterTypes.length )
                            return method;
                        else if( !methodParameterTypes[ index ].isAssignableFrom( parameters[ index ] ) )
                            break;
                }
            }
        }

        // Class.getDeclaredMethods() does not report abstract methods. Try a little harder...
        if( methodIsAbstract == null || methodIsAbstract )
        {
            Type bcelReturnType = Type.getType( returnType );
            List< Type > bcelParameterTypes = Arrays
                .stream( parameters )
                .map( Type::getType )
                .collect( Collectors.toList() );
            JavaClass bcelClass = Repository.lookupClass( clazz );
            for( com.sun.org.apache.bcel.internal.classfile.Method method : bcelClass.getMethods() )
            {
                if( method.getReturnType().equals( bcelReturnType ) )
                {
                    boolean parametersMatch = Arrays
                        .stream( method.getArgumentTypes() )
                        .collect( Collectors.toList() )
                        .equals( bcelParameterTypes );
                    if( parameters.length == 0 || parametersMatch )
                    {
                        try
                        {
                            return clazz.getDeclaredMethod( method.getName() , parameters );
                        }
                        catch( Exception e )
                        {
                            // Not found
                        }
                    }
                }
            }
        }

        return null;
    }

    public static Boolean unfinalizeField( Field field )
    {
        Boolean wasFinal = null;

        try
        {
            Field modifiersField = Field.class.getDeclaredField( "modifiers" );
            modifiersField.setAccessible( true );
            wasFinal = ( field.getModifiers() & Modifier.FINAL ) > 0;
            modifiersField.setInt( field , field.getModifiers() & ~Modifier.FINAL );
        }
        catch( Exception e )
        {
            // Ignore
        }

        return wasFinal;
    }
}
