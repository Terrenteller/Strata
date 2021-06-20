package com.riintouge.strata.misc;

public class InitializedThreadLocal< T > extends ThreadLocal< T >
{
    private T initialValue;

    public InitializedThreadLocal( T initialValue )
    {
        this.initialValue = initialValue;
    }

    @Override
    protected T initialValue()
    {
        return initialValue;
    }
}
