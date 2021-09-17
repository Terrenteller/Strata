package com.riintouge.strata.block;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MetaResourceLocation implements Comparable< MetaResourceLocation >
{
    private static final Pattern ResourceRegex = Pattern.compile( "^([^:]+):([^0-9][^:]*)(?::([0-9]+))?$" );
    private static final int DomainGroup = 1;
    private static final int PathGroup = 2;
    private static final int MetaGroup = 3;

    public ResourceLocation resourceLocation;
    public int meta = 0;

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
        Matcher matcher = ResourceRegex.matcher( resourceString );
        if( !matcher.find() )
            throw new IllegalArgumentException( resourceString );

        this.resourceLocation = new ResourceLocation( matcher.group( DomainGroup ) , matcher.group( PathGroup ) );

        String metaString = matcher.group( MetaGroup );
        if( metaString != null )
        {
            this.meta = Integer.parseInt( metaString );
            if( this.meta < 0 || this.meta > 15 )
                throw new IllegalArgumentException( resourceString );
        }
    }

    public boolean equals( MetaResourceLocation other )
    {
        return compareTo( other ) == 0;
    }

    public ItemStack toItemStack()
    {
        Block resultBlock = Block.REGISTRY.getObject( resourceLocation );
        if( resultBlock != null && resultBlock != Blocks.AIR )
            return new ItemStack( resultBlock , 1 , meta );

        Item resultItem = Item.REGISTRY.getObject( resourceLocation );
        if( resultItem != null && resultItem != Items.AIR )
            return new ItemStack( resultItem , 1 , meta );

        return ItemStack.EMPTY;
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
