package com.riintouge.strata.util;

import javax.annotation.Nullable;
import java.util.Arrays;

public final class DebugUtil
{
    public static void printCallStack()
    {
        System.out.println( Arrays.toString( Thread.currentThread().getStackTrace() ) );
    }

    public static void prettyPrintCallStack()
    {
        StackTraceElement[] elements = Thread.currentThread().getStackTrace();
        for( int index = 2 ; index < elements.length ; index++ )
            System.out.println( "\t" + elements[ index ].toString() );
    }

    public static void printCaller()
    {
        StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
        // 0 is getStackTrace
        // 1 is us
        // 2 is what called us
        // 3 is what called our caller
        System.out.println( stackTraceElements[ 2 ].getMethodName() + " called by " + stackTraceElements[ 3 ].toString() );
    }

    public static void printCallerCaller()
    {
        StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
        // 0 is getStackTrace
        // 1 is us
        // 2 is what called us
        // 3 is what called our caller
        // 4 is ...yeah
        System.out.println( stackTraceElements[ 2 ].getMethodName() + " eventually called from " + stackTraceElements[ 4 ].toString() );
    }

    public static String prettyPrintThrowable( Throwable t , @Nullable String title )
    {
        StringBuilder message = new StringBuilder();
        String details = t.getMessage();
        String exceptionClassName = t.getClass().getName();

        if( title != null )
        {
            message.append( String.format( title , exceptionClassName ) );
            if( !StringUtil.isNullOrEmpty( details ) )
            {
                message.append( "\nMessage:\n\t" );
                message.append( details );
            }
        }
        else if( !StringUtil.isNullOrEmpty( details ) )
            message.append( String.format( "%s: %s" , exceptionClassName , details ) );
        else
            message.append( String.format( "Non-descript %s!" , exceptionClassName ) );

        message.append( "\nStacktrace:" );
        for( StackTraceElement element : t.getStackTrace() )
        {
            message.append( "\n\t" );
            message.append( element.toString() );
        }

        for( Throwable cause = t.getCause() ; cause != null ; cause = cause.getCause() )
        {
            message.append( String.format( "\nCaused by %s:" , cause.getClass().getName() ) );

            details = cause.getMessage();
            if( !StringUtil.isNullOrEmpty( details ) )
            {
                message.append( "\nMessage:\n\t" );
                message.append( details );
                message.append( "\nStacktrace:" );
            }

            for( StackTraceElement element : cause.getStackTrace() )
            {
                message.append( "\n\t" );
                message.append( element.toString() );
            }
        }

        return message.toString();
    }
}
