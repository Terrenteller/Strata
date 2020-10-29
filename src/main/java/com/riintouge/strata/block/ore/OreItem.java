package com.riintouge.strata.block.ore;

import com.riintouge.strata.Strata;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class OreItem extends Item
{
    protected IOreInfo oreInfo;

    public OreItem( IOreInfo oreInfo )
    {
        this.oreInfo = oreInfo;

        ResourceLocation resource = Strata.resource( oreInfo.oreName() );
        setRegistryName( resource );
        setUnlocalizedName( resource.toString() );

        setCreativeTab( Strata.ORE_ITEM_TAB );
    }

    // ItemBlock overrides

    @Override
    public String getUnlocalizedName()
    {
        // Strata localization doesn't make a distinction between blocks and items.
        return super.getUnlocalizedName().replaceFirst( "item." , "" );
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
        return oreInfo.burnTime();
    }
}
