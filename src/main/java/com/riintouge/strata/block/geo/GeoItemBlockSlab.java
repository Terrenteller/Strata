package com.riintouge.strata.block.geo;

import net.minecraft.block.BlockSlab;
import net.minecraft.item.ItemSlab;
import net.minecraft.item.ItemStack;

public class GeoItemBlockSlab extends ItemSlab
{
    public GeoItemBlockSlab( BlockSlab singleSlab , BlockSlab doubleSlab )
    {
        super( singleSlab , singleSlab , doubleSlab );

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
