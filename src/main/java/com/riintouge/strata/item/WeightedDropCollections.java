package com.riintouge.strata.item;

import com.riintouge.strata.block.MetaResourceLocation;
import com.riintouge.strata.misc.WeightedCollection;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;

public class WeightedDropCollections
{
    protected final Map< String , WeightedCollection< Pair< MetaResourceLocation , IFortuneDistribution > > > dropsByGroup = new HashMap<>();

    public WeightedDropCollections()
    {
        // Nothing to do
    }

    public void addDropToGroup( MetaResourceLocation itemResource , IFortuneDistribution distribution , int weight , String id )
    {
        this.dropsByGroup
            .computeIfAbsent( id , s -> new WeightedCollection<>() )
            .add( new ImmutablePair<>( itemResource , distribution ) , weight );
    }

    public List< ItemStack > collectRandomDrops( Random random , int fortuneLevel )
    {
        List< ItemStack > drops = new ArrayList<>();
        for( WeightedCollection< Pair< MetaResourceLocation , IFortuneDistribution > > dropGroup : dropsByGroup.values() )
        {
            Pair< MetaResourceLocation , IFortuneDistribution > drop = dropGroup.getRandomItem( random );
            if( drop == null )
                continue;

            int dropAmount = drop.getValue().getAmount( random , fortuneLevel );
            if( dropAmount <= 0 )
                continue;

            MetaResourceLocation metaResource = drop.getKey();
            Item item = Item.getByNameOrId( metaResource.resourceLocation.toString() );
            if( item != null )
            {
                ItemStack itemStack = new ItemStack( item , Math.min( dropAmount , item.getItemStackLimit() ) , metaResource.meta );
                drops.add( itemStack );
            }
            else
            {
                Block block = Block.getBlockFromName( metaResource.resourceLocation.toString() );
                ItemStack itemStack = new ItemStack( block , Math.min( dropAmount , 64 ) , metaResource.meta );
                drops.add( itemStack );
            }
        }

        return drops;
    }
}
