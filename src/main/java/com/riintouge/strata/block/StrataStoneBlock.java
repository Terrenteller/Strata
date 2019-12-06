package com.riintouge.strata.block;

import com.riintouge.strata.GenericStoneRegistry;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;

import java.util.Random;

public class StrataStoneBlock extends GenericStoneBlock
{
    public StrataStoneBlock( IGenericStoneTileSetInfo tileSetInfo )
    {
        super( tileSetInfo , StoneBlockType.STONE );
    }

    @Override
    public Item getItemDropped( IBlockState state , Random rand , int fortune )
    {
        try
        {
            GenericStoneTileSet tileSet = GenericStoneRegistry.INSTANCE.find( tileSetInfo.stoneName() );
            return tileSet.tiles.get( StoneBlockType.COBBLE ).getItem();
        }
        catch( NullPointerException e )
        {
            return super.getItemDropped( state , rand , fortune );
        }
    }
}
