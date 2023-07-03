package com.riintouge.strata.util;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public final class CollectionUtil
{
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
