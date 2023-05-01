package com.riintouge.strata.util;

import java.util.Arrays;

public class DebugUtil
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

    public static void printException( Exception e )
    {
        System.out.println( e.getClass().getName() );
        for( StackTraceElement element : e.getStackTrace() )
            System.out.println( element.toString() );
    }

    public static String prettyPrintException( Exception e , String title )
    {
        StringBuilder message = new StringBuilder( String.format( title , e.getClass().getName() ) );
        message.append( "\nMessage:\n\t" );
        String details = e.getMessage();
        message.append( Util.isNullOrEmpty( details ) ? "N/A" : details );
        message.append( "\nStacktrace:" );
        for( StackTraceElement element : e.getStackTrace() )
        {
            message.append( "\n\t" );
            message.append( element.toString() );
        }

        return message.toString();
    }
}
