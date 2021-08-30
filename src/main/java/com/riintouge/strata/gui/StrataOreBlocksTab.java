package com.riintouge.strata.gui;

import com.riintouge.strata.block.ore.OreRegistry;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;

public class StrataOreBlocksTab extends CreativeTabs
{
    public StrataOreBlocksTab()
    {
        super( "strataOreBlocksTab" );
    }

    @Override
    public ItemStack getTabIconItem()
    {
        return new ItemStack( OreRegistry.INSTANCE.find( "banded_iron" ).getItemBlock() );
    }
}
