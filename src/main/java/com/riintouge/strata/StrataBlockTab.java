package com.riintouge.strata;

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
        return new ItemStack( GenericStoneRegistry.INSTANCE.find( "schist" ).tiles.get( StoneBlockType.STONE ).getBlock() );
    }
}
