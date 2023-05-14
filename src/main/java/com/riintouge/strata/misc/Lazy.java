package com.riintouge.strata.misc;

public class Lazy< T >
{
    private final LambdaNoThrow< T > lambda;
    private boolean evaluated = false;
    private T resolvedValue = null;

    public Lazy( LambdaNoThrow< T > lambda )
    {
        this.lambda = lambda;
    }

    public void invalidate()
    {
        synchronized( lambda )
        {
            evaluated = false;
            resolvedValue = null;
        }
    }

    public T value()
    {
        synchronized( lambda )
        {
            if( !evaluated )
            {
                resolvedValue = lambda.invoke();
                evaluated = true;
            }

            return resolvedValue;
        }
    }
}
