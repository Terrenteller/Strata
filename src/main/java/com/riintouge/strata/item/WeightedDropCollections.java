package com.riintouge.strata.item;

import com.google.common.collect.Iterables;
import com.riintouge.strata.misc.MetaResourceLocation;
import com.riintouge.strata.misc.WeightedCollection;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;

public class WeightedDropCollections
{
    protected final Map< String , WeightedCollection< Pair< MetaResourceLocation , IDropFormula > > > dropsByGroup = new HashMap<>();

    public WeightedDropCollections()
    {
        // Nothing to do
    }

    public void addDropToGroup( MetaResourceLocation itemResource , IDropFormula dropFormula , int weight , String id )
    {
        this.dropsByGroup
            .computeIfAbsent( id , s -> new WeightedCollection<>() )
            .add( new ImmutablePair<>( itemResource , dropFormula ) , weight );
    }

    public List< ItemStack > collectRandomDrops( Random random , ItemStack harvestTool , BlockPos pos )
    {
        List< ItemStack > drops = new ArrayList<>();

        for( WeightedCollection< Pair< MetaResourceLocation , IDropFormula > > dropGroup : dropsByGroup.values() )
        {
            ItemStack drop = null;

            while( true )
            {
                Pair< MetaResourceLocation , IDropFormula > dropPair = dropGroup.getRandomObject( random );
                if( dropPair == null )
                    break;

                int dropAmount = dropPair.getValue().getAmount( random , harvestTool , pos );
                if( dropAmount <= 0 )
                    break;

                MetaResourceLocation metaResource = dropPair.getKey();
                Item item = Item.getByNameOrId( metaResource.resourceLocation.toString() );
                if( item != null )
                {
                    drop = new ItemStack( item , Math.min( dropAmount , item.getItemStackLimit() ) , metaResource.meta );
                    break;
                }
                else
                {
                    // How can we get an item stack of the block if it doesn't have an item?
                    // Perhaps there is a name difference? Will this code produce a meaningful result?
                    Block block = Block.getBlockFromName( metaResource.resourceLocation.toString() );
                    if( block != null )
                    {
                        drop = new ItemStack( block , Math.min( dropAmount , 64 ) , metaResource.meta );
                        break;
                    }
                }

                dropGroup.remove( dropPair );
            }

            if( drop != null && !drop.isEmpty() )
                drops.add( drop );
        }

        return drops;
    }

    public ItemStack getSingleRandomDrop( Random random )
    {
        int groupIndex = random.nextInt( dropsByGroup.size() );
        WeightedCollection< Pair< MetaResourceLocation , IDropFormula > > dropGroup = Iterables.get( dropsByGroup.values() , groupIndex );
        Pair< MetaResourceLocation , IDropFormula > dropPair = dropGroup.getRandomObject( random , ( pair ) ->
        {
            MetaResourceLocation metaResourceLocation = pair.getKey();
            ItemStack itemStack = new ItemStack( Item.REGISTRY.getObject( metaResourceLocation.resourceLocation ) , 1 , metaResourceLocation.meta );
            return !itemStack.isEmpty();
        } );

        return new ItemStack( Item.REGISTRY.getObject( dropPair.getKey().resourceLocation ) , 1 , dropPair.getKey().meta );
    }
}
