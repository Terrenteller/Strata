package com.riintouge.strata.gui;

import com.riintouge.strata.Strata;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class StrataBuildingBlocksTab extends CreativeTabs
{
    public StrataBuildingBlocksTab()
    {
        super( "strataBuildingBlocksTab" );
    }

    // CreativeTabs overrides

    @Override
    public ItemStack getTabIconItem()
    {
        return new ItemStack( Item.REGISTRY.getObject( Strata.resource( "gneiss_stonewall" ) ) );
    }
}
