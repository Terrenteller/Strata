package com.riintouge.strata;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class StrataBuildingBlocksTab extends CreativeTabs
{
    public StrataBuildingBlocksTab()
    {
        super( "strataBuildingBlocksTab" );
    }

    @Override
    public ItemStack getTabIconItem()
    {
        return new ItemStack( Item.REGISTRY.getObject( new ResourceLocation( Strata.modid , "gneiss_stonewall" ) ) );
    }
}
