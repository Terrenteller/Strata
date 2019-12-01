package com.riintouge.strata;

import com.riintouge.strata.item.ore.WeakStoneOreItem;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;

public class StrataItemTab extends CreativeTabs
{
    public StrataItemTab()
    {
        super( "oreTab" );
    }

    @Override
    public ItemStack getTabIconItem()
    {
        return new ItemStack( WeakStoneOreItem.INSTANCE );
    }
}
