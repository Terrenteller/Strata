package com.riintouge.strata.util;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Stack;
import java.util.function.Function;

public class RPNExpression
{
    private static final HashMap< String , IRPNOperator > BUILTIN_OPERATORS = new HashMap<>();

    static
    {
        // Numeric

        BUILTIN_OPERATORS.put( "+" , stack -> stack.push( stack.pop() + stack.pop() ) );

        BUILTIN_OPERATORS.put( "-" , stack ->
        {
            double right = stack.pop();
            double left = stack.pop();
            stack.push( left - right );
        } );

        BUILTIN_OPERATORS.put( "*" , stack -> stack.push( stack.pop() * stack.pop() ) );

        BUILTIN_OPERATORS.put( "/" , stack ->
        {
            double right = stack.pop();
            double left = stack.pop();
            stack.push( left / right );
        } );

        BUILTIN_OPERATORS.put( "%" , stack ->
        {
            double right = stack.pop();
            double left = stack.pop();
            stack.push( left % right );
        } );

        BUILTIN_OPERATORS.put( "^" , stack ->
        {
            double right = stack.pop();
            double left = stack.pop();
            stack.push( Math.pow( left , right ) );
        } );

        // Comparison

        BUILTIN_OPERATORS.put( ">" , stack ->
        {
            double right = stack.pop();
            double left = stack.pop();
            stack.push( left > right ? 1.0 : 0.0 );
        } );

        BUILTIN_OPERATORS.put( ">=" , stack ->
        {
            double right = stack.pop();
            double left = stack.pop();
            stack.push( left >= right ? 1.0 : 0.0 );
        } );

        BUILTIN_OPERATORS.put( "<" , stack ->
        {
            double right = stack.pop();
            double left = stack.pop();
            stack.push( left < right ? 1.0 : 0.0 );
        } );

        BUILTIN_OPERATORS.put( "<=" , stack ->
        {
            double right = stack.pop();
            double left = stack.pop();
            stack.push( left <= right ? 1.0 : 0.0 );
        } );

        BUILTIN_OPERATORS.put( "==" , stack ->
        {
            double right = stack.pop();
            double left = stack.pop();
            stack.push( left == right ? 1.0 : 0.0 );
        } );

        BUILTIN_OPERATORS.put( "!=" , stack ->
        {
            double right = stack.pop();
            double left = stack.pop();
            stack.push( left != right ? 1.0 : 0.0 );
        } );

        BUILTIN_OPERATORS.put( "!" , stack -> stack.push( stack.pop() == 0.0 ? 1.0 : 0.0 ) );

        BUILTIN_OPERATORS.put( "?" , stack ->
        {
            if( stack.pop() == 0.0 )
                stack.pop();
        } );

        BUILTIN_OPERATORS.put( "?:" , stack ->
        {
            if( stack.pop() == 0.0 )
            {
                double falseResult = stack.pop();
                stack.pop();
                stack.push( falseResult );
            }
            else
                stack.pop();
        } );

        // Functions

        BUILTIN_OPERATORS.put( "abs"   , stack -> stack.push( Math.abs( stack.pop() ) ) );
        BUILTIN_OPERATORS.put( "ceil"  , stack -> stack.push( Math.ceil( stack.pop() ) ) );
        BUILTIN_OPERATORS.put( "dec"   , stack -> stack.push( stack.pop() - 1.0 ) );
        BUILTIN_OPERATORS.put( "dup"   , stack -> stack.push( stack.peek() ) );
        BUILTIN_OPERATORS.put( "floor" , stack -> stack.push( Math.floor( stack.pop() ) ) );
        BUILTIN_OPERATORS.put( "fmod"  , stack -> stack.push( stack.pop() % 1.0f ) );
        BUILTIN_OPERATORS.put( "inc"   , stack -> stack.push( stack.pop() + 1.0 ) );
        BUILTIN_OPERATORS.put( "log"   , stack -> stack.push( Math.log( stack.pop() ) ) );
        BUILTIN_OPERATORS.put( "log10" , stack -> stack.push( Math.log10( stack.pop() ) ) );
        BUILTIN_OPERATORS.put( "log1p" , stack -> stack.push( Math.log1p( stack.pop() ) ) );
        BUILTIN_OPERATORS.put( "neg"   , stack -> stack.push( stack.pop() * -1.0 ) );
        BUILTIN_OPERATORS.put( "max"   , stack -> stack.push( Math.max( stack.pop() , stack.pop() ) ) );
        BUILTIN_OPERATORS.put( "min"   , stack -> stack.push( Math.min( stack.pop() , stack.pop() ) ) );
        BUILTIN_OPERATORS.put( "pop"   , Stack::pop );
        BUILTIN_OPERATORS.put( "rand"  , stack -> stack.push( Math.random() ) );
        BUILTIN_OPERATORS.put( "recip" , stack -> stack.push( 1.0 / stack.pop() ) );
        BUILTIN_OPERATORS.put( "round" , stack -> stack.push( (double)Math.round( stack.pop() ) ) );
        BUILTIN_OPERATORS.put( "sign"  , stack -> stack.push( Math.signum( stack.pop() ) ) );
        BUILTIN_OPERATORS.put( "sqrt"  , stack -> stack.push( Math.sqrt( stack.pop() ) ) );

        // Aliases

        BUILTIN_OPERATORS.put( "add" , BUILTIN_OPERATORS.get( "+" ) );
        BUILTIN_OPERATORS.put( "sub" , BUILTIN_OPERATORS.get( "-" ) );
        BUILTIN_OPERATORS.put( "mul" , BUILTIN_OPERATORS.get( "*" ) );
        BUILTIN_OPERATORS.put( "div" , BUILTIN_OPERATORS.get( "/" ) );
        BUILTIN_OPERATORS.put( "mod" , BUILTIN_OPERATORS.get( "%" ) );
        BUILTIN_OPERATORS.put( "pow" , BUILTIN_OPERATORS.get( "^" ) );

        BUILTIN_OPERATORS.put( "gt"     , BUILTIN_OPERATORS.get( ">"  ) );
        BUILTIN_OPERATORS.put( "ge"     , BUILTIN_OPERATORS.get( ">=" ) );
        BUILTIN_OPERATORS.put( "lt"     , BUILTIN_OPERATORS.get( "<"  ) );
        BUILTIN_OPERATORS.put( "le"     , BUILTIN_OPERATORS.get( "<=" ) );
        BUILTIN_OPERATORS.put( "eq"     , BUILTIN_OPERATORS.get( "==" ) );
        BUILTIN_OPERATORS.put( "ne"     , BUILTIN_OPERATORS.get( "!=" ) );
        BUILTIN_OPERATORS.put( "not"    , BUILTIN_OPERATORS.get( "!"  ) );
        BUILTIN_OPERATORS.put( "if"     , BUILTIN_OPERATORS.get( "?"  ) );
        BUILTIN_OPERATORS.put( "ifelse" , BUILTIN_OPERATORS.get( "?:" ) );
    }

    protected final String expression;

    public RPNExpression( String expression )
    {
        this.expression = expression;
    }

    public double evaluate( Function< String , Double > variableGetter ) throws IllegalArgumentException
    {
        Stack< Double > stack = new Stack<>();

        for( String token : expression.trim().split( " +" ) )
        {
            IRPNOperator operator = getOperatorForToken( token );
            if( operator != null )
            {
                operator.apply( stack );
                continue;
            }

            try
            {
                Double variable = variableGetter.apply( token );

                if( variable != null )
                    stack.push( variable );
                else
                    stack.push( Double.parseDouble( token ) );
            }
            catch( Exception e )
            {
                String message = String.format(
                    "Failed to process RPN token '%s' in expression '%s'!",
                    token,
                    expression );
                throw new IllegalArgumentException( message , e );
            }
        }

        return stack.isEmpty() ? 0.0 : stack.peek();
    }

    @Nullable
    public IRPNOperator getOperatorForToken( String token )
    {
        return BUILTIN_OPERATORS.getOrDefault( token , null );
    }

    // Interfaces

    public interface IRPNOperator
    {
        void apply( Stack< Double > rpn );
    }
}
