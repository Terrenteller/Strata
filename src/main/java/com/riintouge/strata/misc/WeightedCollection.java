package com.riintouge.strata.misc;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class WeightedCollection< T >
{
    protected final List< Pair< T , Integer > > weightedItems = new ArrayList<>();
    protected int totalWeight = 0;

    public void add( T object , int weight )
    {
        weightedItems.add( new ImmutablePair<>( object , weight ) );
        totalWeight += weight;
    }

    @Nullable
    public T getRandomItem( Random random )
    {
        if( weightedItems.size() > 1 )
        {
            int randomWeight = random.nextInt( totalWeight );
            int cumulativeWeight = 0;
            for( Pair< T , Integer > item : weightedItems )
            {
                int itemWeight = item.getValue();
                if( randomWeight < ( cumulativeWeight + itemWeight ) )
                    return item.getKey();
                else
                    cumulativeWeight += itemWeight;
            }
        }
        else if( weightedItems.size() == 1 )
            return weightedItems.get( 0 ).getKey();

        return null;
    }
}
