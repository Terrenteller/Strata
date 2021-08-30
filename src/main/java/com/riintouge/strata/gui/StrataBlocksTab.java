package com.riintouge.strata.gui;

import com.riintouge.strata.Strata;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class StrataBlocksTab extends CreativeTabs
{
    public StrataBlocksTab()
    {
        super( "strataBlocksTab" );
    }

    @Override
    public ItemStack getTabIconItem()
    {
        return new ItemStack( Item.REGISTRY.getObject( new ResourceLocation( Strata.modid , "gneiss" ) ) );
    }
}
