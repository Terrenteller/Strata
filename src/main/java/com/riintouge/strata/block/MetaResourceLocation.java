package com.riintouge.strata.block;

import com.sun.istack.internal.NotNull;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.ResourceLocation;

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

    public MetaResourceLocation( String resourceString )
    {
        Matcher matcher = RESOURCE_PATTERN.matcher( resourceString.toLowerCase() );
        if( !matcher.find() )
            throw new IllegalArgumentException( resourceString );

        this.resourceLocation = new ResourceLocation( matcher.group( DOMAIN_GROUP ) , matcher.group( PATH_GROUP ) );

        String metaString = matcher.group( META_GROUP );
        if( metaString != null )
        {
            this.meta = Integer.parseInt( metaString );
            if( this.meta < 0 || this.meta > 15 )
                throw new IllegalArgumentException( resourceString );
        }
        else
            this.meta = 0;
    }

    public MetaResourceLocation( IBlockState blockState )
    {
        this.resourceLocation = blockState.getBlock().getRegistryName();
        this.meta = blockState.getBlock().getMetaFromState( blockState );
    }

    public boolean equals( @Nullable MetaResourceLocation other )
    {
        return other != null && compareTo( other ) == 0;
    }

    // Comparable overrides

    @Override
    public int compareTo( @NotNull MetaResourceLocation other )
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
}
