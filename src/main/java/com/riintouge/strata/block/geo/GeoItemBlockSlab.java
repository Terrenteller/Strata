package com.riintouge.strata.block.geo;

import com.riintouge.strata.block.SpecialBlockPropertyFlags;
import net.minecraft.block.BlockSlab;
import net.minecraft.item.ItemSlab;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

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
        String name = tileInfo.localizedName();
        return name != null ? name : tileInfo.registryName().toString();
    }

    @Override
    @SideOnly( Side.CLIENT )
    public boolean hasEffect( ItemStack stack )
    {
        ItemStack equivalentItemStack = tileInfo.equivalentItemStack();
        return ( equivalentItemStack != null && equivalentItemStack.hasEffect() )
            || ( tileInfo.specialBlockPropertyFlags() & SpecialBlockPropertyFlags.HAS_EFFECT ) > 0
            || super.hasEffect( stack );
    }
}
