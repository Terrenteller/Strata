package com.riintouge.strata.block.geo;

import net.minecraft.block.BlockSlab;
import net.minecraft.item.ItemSlab;
import net.minecraft.item.ItemStack;

public class GeoItemBlockSlab extends ItemSlab
{
    protected IGeoTileInfo tileInfo;

    public GeoItemBlockSlab( IGeoTileInfo tileInfo , BlockSlab singleSlab , BlockSlab doubleSlab )
    {
        super( singleSlab , singleSlab , doubleSlab );
        this.tileInfo = tileInfo;

        String blockRegistryName = block.getRegistryName().toString();
        setRegistryName( blockRegistryName );
        setUnlocalizedName( blockRegistryName );
    }

    // ItemBlock overrides

    @Override
    public String getUnlocalizedName()
    {
        return this.block.getUnlocalizedName().replaceAll( "tile." , "" );
    }

    @Override
    public String getUnlocalizedName( ItemStack stack )
    {
        return getUnlocalizedName();
    }

    // Item overrides

    @Override
    public String getItemStackDisplayName( ItemStack stack )
    {
        return tileInfo.localizedName();
    }
}
