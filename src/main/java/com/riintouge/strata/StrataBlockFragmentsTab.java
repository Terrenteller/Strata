package com.riintouge.strata;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class StrataBlockFragmentsTab extends CreativeTabs
{
    public StrataBlockFragmentsTab()
    {
        super( "strataBlockFragmentsTab" );
    }

    @Override
    public ItemStack getTabIconItem()
    {
        return new ItemStack( Item.REGISTRY.getObject( new ResourceLocation( Strata.modid , "sodium_bentonite_ball" ) ) );
    }
}
