package com.riintouge.strata.block.geo;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class GeoItemBlock extends ItemBlock
{
    protected IGeoTileInfo tileInfo;

    public GeoItemBlock( IGeoTileInfo tileInfo , Block block )
    {
        super( block );
        this.tileInfo = tileInfo;

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

    // Item overrides

    @Override
    public int getItemBurnTime( ItemStack itemStack )
    {
        return tileInfo.type().isPrimary ? tileInfo.burnTime() : 0;
    }
}
