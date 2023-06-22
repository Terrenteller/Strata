package com.riintouge.strata.misc;

import com.riintouge.strata.util.Util;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MetaResourceLocation implements Comparable< MetaResourceLocation >
{
    public static final Pattern RESOURCE_PATTERN = Pattern.compile( "^([^:]+):([^0-9][^:]*)(?::([0-9]+))?$" );
    public static final int DOMAIN_GROUP = 1;
    public static final int PATH_GROUP = 2;
    public static final int META_GROUP = 3;

    public final ResourceLocation resourceLocation;
    public final int meta;

    public MetaResourceLocation( String resourceLocation , int meta )
    {
        this.resourceLocation = new ResourceLocation( resourceLocation );
        this.meta = meta;
    }

    public MetaResourceLocation( ResourceLocation resourceLocation , int meta )
    {
        this.resourceLocation = resourceLocation;
        this.meta = meta;
    }

    public MetaResourceLocation( Pair< ResourceLocation , Integer > resourceLocationAndMeta )
    {
        this.resourceLocation = resourceLocationAndMeta.getLeft();
        this.meta = Util.coalesce( resourceLocationAndMeta.getRight() , 0 );
    }

    public MetaResourceLocation( String resourceString )
    {
        this( parseIntoPair( resourceString ) );
    }

    public MetaResourceLocation( IBlockState blockState )
    {
        Block block = blockState.getBlock();
        this.resourceLocation = block.getRegistryName();
        this.meta = block.getMetaFromState( blockState );
    }

    public boolean equals( @Nullable MetaResourceLocation other )
    {
        return other != null && compareTo( other ) == 0;
    }

    // Comparable overrides

    @Override
    public int compareTo( @Nonnull MetaResourceLocation other )
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
    public boolean equals( Object other )
    {
        return other instanceof MetaResourceLocation && equals( (MetaResourceLocation)other );
    }

    @Override
    public int hashCode()
    {
        return resourceLocation.hashCode() + meta;
    }

    @Override
    public String toString()
    {
        return String.format( "%s:%d" , resourceLocation.toString() , meta );
    }

    // Statics

    public static Pair< ResourceLocation , Integer > parseIntoPair( String resourceString )
    {
        // TODO: What about bracketed variants like strata:diorite[orientation=side_north]
        Matcher matcher = RESOURCE_PATTERN.matcher( resourceString.toLowerCase() );
        if( !matcher.find() )
            throw new IllegalArgumentException( resourceString );

        ResourceLocation resourceLocation = new ResourceLocation( matcher.group( DOMAIN_GROUP ) , matcher.group( PATH_GROUP ) );
        Integer meta = null;

        String metaString = matcher.group( META_GROUP );
        if( metaString != null )
        {
            meta = Integer.parseInt( metaString );
            if( meta < 0 || meta > 15 )
                throw new IllegalArgumentException( resourceString );
        }

        return new ImmutablePair<>( resourceLocation , meta );
    }
}
