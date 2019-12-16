package com.riintouge.strata;

import net.minecraft.util.ResourceLocation;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MetaResourceLocation implements Comparable< MetaResourceLocation >
{
    private static final String ResourcePattern = String.format( "^(.+?)[:_](.+)[:_]([0-9]+)$" , Strata.modid );
    private static final int DomainGroup = 1;
    private static final int PathGroup = 2;
    private static final int MetaGroup = 3;
    private static final Pattern ResourceRegex = Pattern.compile( ResourcePattern );

    public ResourceLocation resourceLocation;
    public int meta;

    public MetaResourceLocation( ResourceLocation resourceLocation , int meta )
    {
        this.resourceLocation = resourceLocation;
        this.meta = meta;
    }

    public MetaResourceLocation( String resourceString )
    {
        Matcher matcher = ResourceRegex.matcher( resourceString );
        if( !matcher.find() )
            throw new IllegalArgumentException( resourceString );

        this.resourceLocation = new ResourceLocation( matcher.group( DomainGroup ) , matcher.group( PathGroup ) );
        this.meta = Integer.parseInt( matcher.group( MetaGroup ) );
        if( this.meta < 0 || this.meta > 16 )
            throw new IllegalArgumentException( resourceString );
    }

    public boolean equals( MetaResourceLocation other )
    {
        return compareTo( other ) == 0;
    }

    // Comparable overrides

    @Override
    public int compareTo( MetaResourceLocation other )
    {
        int comparison = resourceLocation.compareTo( other.resourceLocation );
        if( comparison != 0 )
            return comparison;

        if( meta > other.meta )
            return 1;
        else if( meta < other.meta )
            return -1;

        return 0;
    }

    // Object overrides

    @Override
    public String toString()
    {
        return String.format( "%s:%d" , resourceLocation.toString() , meta );
    }
}
