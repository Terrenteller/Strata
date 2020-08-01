package com.riintouge.strata.block.geo;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class GeoItemBlock extends ItemBlock
{
    public GeoItemBlock( Block block )
    {
        super( block );

        String blockRegistryName = block.getRegistryName().toString();
        setRegistryName( blockRegistryName );
        setUnlocalizedName( blockRegistryName );
    }

    // ItemBlock overrides

    @Override
    public String getUnlocalizedName( ItemStack stack )
    {
        return this.block.getUnlocalizedName().replaceAll( "tile." , "" );
    }
}
