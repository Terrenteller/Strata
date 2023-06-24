package com.riintouge.strata.misc;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Predicate;

public class WeightedCollection< T >
{
    protected final List< Pair< T , Integer > > weightedObjects = new ArrayList<>();
    protected int totalWeight = 0;

    public void add( T object , int weight )
    {
        weightedObjects.add( new ImmutablePair<>( object , weight ) );
        totalWeight += weight;
    }

    @Nullable
    public Pair< T , Integer > find( T object )
    {
        for( Pair< T , Integer > pair : weightedObjects )
            if( pair.getKey().equals( object ) )
                return pair;

        return null;
    }

    public void remove( Pair< T , Integer > pair )
    {
        if( pair != null )
            if( weightedObjects.remove( pair ) )
                totalWeight -= pair.getValue();
    }

    public void remove( T object )
    {
        remove( find( object ) );
    }

    @Nullable
    public T getRandomObject( Random random )
    {
        if( weightedObjects.size() > 1 )
            return getRandomObject( random , weightedObjects , totalWeight );
        else if( weightedObjects.size() == 1 )
            return weightedObjects.get( 0 ).getKey();

        return null;
    }

    @Nullable
    public T getRandomObject( Random random , Predicate< T > allow )
    {
        if( weightedObjects.size() > 1 )
        {
            int weightThreshold = random.nextInt( totalWeight );
            int totalValidWeight = 0;
            List< Pair< T , Integer > > validObjects = new ArrayList<>();

            for( Pair< T , Integer > item : weightedObjects )
            {
                int itemWeight = item.getValue();

                if( allow.test( item.getKey() ) )
                {
                    if( ( totalValidWeight + itemWeight ) > weightThreshold )
                        return item.getKey();

                    validObjects.add( item );
                    totalValidWeight += itemWeight;
                }
            }

            // If the threshold exceeds the sum of valid weights, try again using only the valid objects
            return getRandomObject( random , validObjects , totalValidWeight );
        }
        else if( weightedObjects.size() == 1 )
        {
            T onlyObject = weightedObjects.get( 0 ).getKey();
            return allow.test( onlyObject ) ? onlyObject : null;
        }

        return null;
    }

    @Nullable
    protected T getRandomObject( Random random , List< Pair< T , Integer > > weightedObjects , int totalWeight )
    {
        if( weightedObjects.isEmpty() || totalWeight <= 0 )
            return null;

        int weightThreshold = random.nextInt( totalWeight );
        int cumulativeWeight = 0;

        for( Pair< T , Integer > item : weightedObjects )
        {
            int itemWeight = item.getValue();

            if( ( cumulativeWeight + itemWeight ) > weightThreshold )
                return item.getKey();
            else
                cumulativeWeight += itemWeight;
        }

        return null;
    }
}
