package com.riintouge.strata;

import com.riintouge.strata.block.GenericStoneTileSet;
import com.riintouge.strata.block.StoneBlockType;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;

public class StrataBlockTab extends CreativeTabs
{
    public StrataBlockTab()
    {
        super( "strataBlocksTab" );
    }

    @Override
    public ItemStack getTabIconItem()
    {
        GenericStoneTileSet tileSet = GenericTileSetRegistry.INSTANCE.find( "schist" , GenericStoneTileSet.class );
        return new ItemStack( tileSet.tiles.get( StoneBlockType.STONE ).getBlock() );
    }
}
