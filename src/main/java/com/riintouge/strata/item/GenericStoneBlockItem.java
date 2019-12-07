package com.riintouge.strata.item;

import com.riintouge.strata.Strata;
import com.riintouge.strata.block.GenericStoneBlock;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class GenericStoneBlockItem extends ItemBlock
{
    public GenericStoneBlockItem( GenericStoneBlock block )
    {
        super( block );

        String blockRegistryName = block.getRegistryName().toString();
        setRegistryName( blockRegistryName );
        setUnlocalizedName( blockRegistryName );

        setCreativeTab( Strata.ITEM_TAB );
    }

    // ItemBlock overrides

    @Override
    public String getUnlocalizedName( ItemStack stack )
    {
        return this.block.getUnlocalizedName().replaceAll( "tile." , "" );
    }
}
