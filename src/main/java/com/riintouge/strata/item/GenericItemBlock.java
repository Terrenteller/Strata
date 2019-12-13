package com.riintouge.strata.item;

import com.riintouge.strata.Strata;
import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class GenericItemBlock extends ItemBlock
{
    public GenericItemBlock( Block block )
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
