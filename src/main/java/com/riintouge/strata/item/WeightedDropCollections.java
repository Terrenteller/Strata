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
            Pair< MetaResourceLocation , IDropFormula > drop = dropGroup.getRandomObject( random );
            if( drop == null )
                continue;

            int dropAmount = drop.getValue().getAmount( random , harvestTool , pos );
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
                // How can we ever have an item stack if the block doesn't have an item? Will this code ever run?
                Block block = Block.getBlockFromName( metaResource.resourceLocation.toString() );
                ItemStack itemStack = new ItemStack( block , Math.min( dropAmount , 64 ) , metaResource.meta );
                drops.add( itemStack );
            }
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
