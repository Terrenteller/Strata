package com.riintouge.strata.gui;

import com.riintouge.strata.Strata;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class StrataBlockSamplesTab extends CreativeTabs
{
    public StrataBlockSamplesTab()
    {
        super( "strataBlockSamplesTab" );
    }

    // CreativeTabs overrides

    @Override
    public ItemStack getTabIconItem()
    {
        return new ItemStack( Item.REGISTRY.getObject( Strata.resource( "gneiss_sample" ) ) );
    }
}
