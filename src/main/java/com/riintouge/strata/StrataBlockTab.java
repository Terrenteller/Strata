package com.riintouge.strata;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class StrataBlockTab extends CreativeTabs
{
    public StrataBlockTab()
    {
        super( "strataBlocksTab" );
    }

    @Override
    public ItemStack getTabIconItem()
    {
        return new ItemStack( Item.REGISTRY.getObject( new ResourceLocation( Strata.modid , "schist" ) ) );
    }
}
