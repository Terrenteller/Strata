package com.riintouge.strata;

import com.riintouge.strata.block.ore.OreRegistry;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;

public class StrataOreItemsTab extends CreativeTabs
{
    public StrataOreItemsTab()
    {
        super( "strataOreItemsTab" );
    }

    @Override
    public ItemStack getTabIconItem()
    {
        return new ItemStack( OreRegistry.INSTANCE.find( "tantalite" ).getItem() );
    }
}
