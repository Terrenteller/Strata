package com.riintouge.strata.block.geo;

import com.riintouge.strata.block.SpecialBlockPropertyFlags;
import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

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
    public int getItemBurnTime( ItemStack itemStack )
    {
        return tileInfo.type().isPrimary ? tileInfo.burnTime() : 0;
    }

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
