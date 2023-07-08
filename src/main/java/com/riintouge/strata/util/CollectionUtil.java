package com.riintouge.strata.util;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public final class CollectionUtil
{
    public static < T > Iterable< EnumeratedElement< T > > enumerate( Collection< T > collection )
    {
        return new EnumeratedCollectionIterable<>( collection );
    }

    private final static class EnumeratedCollectionIterable< T > implements Iterable< EnumeratedElement< T > >
    {
        private final Collection< T > collection;

        public EnumeratedCollectionIterable( Collection< T > collection )
        {
            this.collection = collection;
        }

        // Iterable overrides

        @Override
        public Iterator< EnumeratedElement< T > > iterator()
        {
            AtomicInteger index = new AtomicInteger( 0 );

            return collection.stream()
                .map( x -> new EnumeratedElement<>( x , index.getAndIncrement() , collection.size() ) )
                .iterator();
        }
    }

    public static class EnumeratedElement< T >
    {
        public final T element;
        public final int index;
        public final boolean isFirst;
        public final boolean isLast;

        public EnumeratedElement( T element , int index , int size )
        {
            this.element = element;
            this.index = index;
            this.isFirst = index == 0;
            this.isLast = index == ( size - 1 );
        }
    }

    public static < K , V > Iterable< Pair< K , V > > inPairs( Map< K , V > map )
    {
        return new MapPairIterable<>( map );
    }

    private final static class MapPairIterable< K , V > implements Iterable< Pair< K , V > >
    {
        private final Map< K , V > map;

        public MapPairIterable( Map< K , V > map )
        {
            this.map = map;
        }

        // Iterable overrides

        @Override
        public Iterator< Pair< K, V > > iterator()
        {
            return new MapPairs<>( map );
        }
    }

    private final static class MapPairs< K , V > implements Iterator< Pair< K , V > >
    {
        private List< Pair< K , V > > pairs = new ArrayList<>();
        private final Iterator< Pair< K , V > > iterator;

        public MapPairs( Map< K , V > map )
        {
            for( K key : map.keySet() )
                pairs.add( new ImmutablePair<>( key , map.get( key ) ) );

            this.iterator = pairs.iterator();
        }

        // Iterator overrides

        @Override
        public boolean hasNext()
        {
            return iterator.hasNext();
        }

        @Override
        public Pair< K , V > next()
        {
            return iterator.next();
        }

        @Override
        public void remove()
        {
            throw new UnsupportedOperationException();
        }
    }
}
