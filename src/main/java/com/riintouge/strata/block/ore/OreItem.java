package com.riintouge.strata.block.ore;

import com.riintouge.strata.Strata;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class OreItem extends Item
{
    protected IOreInfo oreInfo;
    protected String unlocalizedProxyName = null;

    public OreItem( IOreInfo oreInfo )
    {
        this.oreInfo = oreInfo;

        ResourceLocation resource = Strata.resource( oreInfo.oreName() );
        setRegistryName( resource );
        setUnlocalizedName( resource.toString() );

        setCreativeTab( Strata.ORE_ITEM_TAB );
    }

    // Item overrides

    @Override
    public int getItemBurnTime( ItemStack itemStack )
    {
        return oreInfo.burnTime();
    }

    @Override
    public String getItemStackDisplayName( ItemStack stack )
    {
        return oreInfo.proxyBlockState() != null
            ? super.getItemStackDisplayName( stack )
            : oreInfo.localizedName();
    }

    @Override
    public String getUnlocalizedName()
    {
        if( unlocalizedProxyName == null )
        {
            IBlockState proxyBlockState = oreInfo.proxyBlockState();
            if( proxyBlockState != null )
            {
                Block proxyBlock = proxyBlockState.getBlock();
                Item proxyBlockDroppedItem = proxyBlock.getItemDropped( proxyBlockState , null , 0 );
                if( proxyBlockDroppedItem != null && !proxyBlockDroppedItem.equals( Items.AIR ) )
                {
                    int proxyMeta = proxyBlock.damageDropped( proxyBlockState );
                    return unlocalizedProxyName = new ItemStack( proxyBlockDroppedItem , 1 , proxyMeta ).getUnlocalizedName();
                }
            }

            unlocalizedProxyName = "";
        }
        else if( !unlocalizedProxyName.isEmpty() )
            return unlocalizedProxyName;

        // Strata localization doesn't make a distinction between blocks and items.
        return super.getUnlocalizedName().replaceFirst( "item." , "" );
    }

    @Override
    public String getUnlocalizedName( ItemStack stack )
    {
        return getUnlocalizedName();
    }
}
