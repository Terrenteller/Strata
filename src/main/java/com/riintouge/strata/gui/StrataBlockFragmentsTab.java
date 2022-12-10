package com.riintouge.strata.gui;

import com.riintouge.strata.Strata;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class StrataBlockFragmentsTab extends CreativeTabs
{
    public StrataBlockFragmentsTab()
    {
        super( "strataBlockFragmentsTab" );
    }

    // CreativeTabs overrides

    @Override
    public ItemStack getTabIconItem()
    {
        return new ItemStack( Item.REGISTRY.getObject( Strata.resource( "sodium_bentonite_ball" ) ) );
    }
}
