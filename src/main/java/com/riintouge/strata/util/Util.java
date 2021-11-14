package com.riintouge.strata.util;

import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Stack;
import java.util.function.Supplier;
import java.util.zip.CRC32;

public class Util
{
    private static CRC32 crc32 = new CRC32();

    public static boolean isPowerOfTwo( int value )
    {
        return value > 0 && ( value & ( value - 1 ) ) == 0;
    }

    public static int whichPowerOfTwo( int value ) throws IllegalArgumentException
    {
        if( value > 0 )
            for( int power = 0 ; power != 32 ; power++ )
                if( value == ( 1 << power ) )
                    return power;

        throw new IllegalArgumentException( String.format( "%d is not a power of two!" , value ) );
    }

    public static int squareRootOfPowerOfTwo( int value )
    {
        return 1 << ( whichPowerOfTwo( value ) >>> 1 );
    }

    public static int clamp( int min , int value , int max )
    {
        if( value < min )
            return min;
        else if( value > max )
            return max;

        return value;
    }

    public static float clampNormal( float normal )
    {
        if( normal < 0.0f )
            return 0.0f;
        else if( normal > 1.0f )
            return 1.0f;

        return normal;
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
    public static < T > T argumentNullCheck( @Nullable T value , @Nonnull String name ) throws IllegalArgumentException
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

    public static double evaluateRPN( String expr , Pair< String , Double > ... variables ) throws NumberFormatException
    {
        Stack< Double > rpnStack = new Stack<>();

        for( String exprToken : expr.split( " " ) )
        {
            switch( exprToken )
            {
                case "+":
                {
                    rpnStack.push( rpnStack.pop() + rpnStack.pop() );
                    break;
                }
                case "-":
                {
                    double right = rpnStack.pop();
                    double left = rpnStack.pop();
                    rpnStack.push( left - right );
                    break;
                }
                case "*":
                {
                    rpnStack.push( rpnStack.pop() * rpnStack.pop() );
                    break;
                }
                case "/":
                {
                    double right = rpnStack.pop();
                    double left = rpnStack.pop();
                    rpnStack.push( left / right );
                    break;
                }
                default:
                {
                    boolean wasVariable = false;
                    for( Pair< String , Double > variable : variables )
                    {
                        if( exprToken.equals( variable.getKey() ) )
                        {
                            rpnStack.push( variable.getValue() );
                            wasVariable = true;
                        }
                    }

                    if( !wasVariable )
                        rpnStack.push( Double.parseDouble( exprToken ) );
                }
            }
        }

        return rpnStack.peek();
    }
}
