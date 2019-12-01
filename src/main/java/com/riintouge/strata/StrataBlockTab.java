package com.riintouge.strata;

import com.riintouge.strata.init.Blocks;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;

public class StrataBlockTab extends CreativeTabs
{
    public StrataBlockTab()
    {
        super( "blockTab" );
    }

    @Override
    public ItemStack getTabIconItem()
    {
        return new ItemStack( Blocks.weakStone );
    }
}
