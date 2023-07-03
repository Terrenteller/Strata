package com.riintouge.strata.misc;

import com.google.common.collect.ImmutableList;

import java.util.*;

public class ObjectAssociator< T >
{
    public List< Collection< T > > groups = new ArrayList<>();

    public void associate( T a , T b )
    {
        if( a.equals( b ) )
            return;

        Collection< T > groupA = findInternal( a );
        Collection< T > groupB = findInternal( b );

        if( groupA != null && groupB != null )
        {
            if( groupA != groupB )
                mergeAssociations( groupA , groupB );
        }
        else if( groupA != null )
        {
            makeAssociation( b , groupA );
        }
        else if( groupB != null )
        {
            makeAssociation( a , groupB );
        }
        else
        {
            Collection< T > collection = createEmptyCollection();
            makeAssociation( a , collection );
            makeAssociation( b , collection );

            groups.add( collection );
        }
    }

    public Collection< T > getAssociations( T value )
    {
        return Collections.unmodifiableCollection( findInternal( value ) );
    }

    public Collection< Collection< T > > allAssociations()
    {
        ImmutableList.Builder< Collection< T > > listBuilder = ImmutableList.builder();
        for( Collection< T > group : groups )
            listBuilder.add( Collections.unmodifiableCollection( group ) );

        return listBuilder.build();
    }

    public int groups()
    {
        return groups.size();
    }

    protected Collection< T > findInternal( T value )
    {
        for( Collection< T > group : groups )
            if( group.contains( value ) )
                return group;

        return null;
    }

    protected Collection< T > createEmptyCollection()
    {
        return new HashSet<>();
    }

    protected void makeAssociation( T value , Collection< T > collection )
    {
        collection.add( value );
    }

    protected void mergeAssociations( Collection< T > target , Collection< T > extra )
    {
        target.addAll( extra );
        groups.remove( extra );
    }
}
