package com.riintouge.strata.block.ore;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class OreItemBlock extends ItemBlock
{
    protected IOreInfo oreInfo;

    public OreItemBlock( IOreInfo oreInfo , Block block )
    {
        super( block );
        this.oreInfo = oreInfo;

        setRegistryName( block.getRegistryName().toString() );
        setUnlocalizedName( block.getUnlocalizedName() );
    }

    // ItemBlock overrides

    @Override
    public String getUnlocalizedName()
    {
        ItemStack proxyBlockItemStack = oreInfo.proxyBlockItemStack();
        if( proxyBlockItemStack != null )
            return proxyBlockItemStack.getItem().getUnlocalizedName( proxyBlockItemStack );

        // Strata localization doesn't make a distinction between blocks and items
        return this.block.getUnlocalizedName().replace( "tile." , "" );
    }


    @Override
    public String getUnlocalizedName( ItemStack stack )
    {
        ItemStack proxyBlockItemStack = oreInfo.proxyBlockItemStack();
        if( proxyBlockItemStack != null )
            return proxyBlockItemStack.getItem().getUnlocalizedName( proxyBlockItemStack );

        return getUnlocalizedName();
    }

    @Override
    public EnumActionResult onItemUse(
        EntityPlayer player,
        World worldIn,
        BlockPos pos,
        EnumHand hand,
        EnumFacing facing,
        float hitX,
        float hitY,
        float hitZ )
    {
        // These items are VERY creative mode only!
        // A separate ore item exists to avoid gamebreaking behaviour like infinite drops!
        if( player == null || !player.isCreative() )
            return EnumActionResult.FAIL;

        Item originalItem = player.getHeldItem( hand ).getItem();
        EnumActionResult result = super.onItemUse( player , worldIn , pos , hand , facing , hitX , hitY , hitZ );

        if( result == EnumActionResult.SUCCESS )
        {
            // ItemBlock.onItemUse() always decrements the stack size, even for creative players.
            // If the stack size becomes zero, it acts like air, and ItemStack.onItemUse() will increment a bogus stat.
            Item bogusItem = player.getHeldItem( hand ).getItem();

            player.addStat( StatList.getObjectUseStats( bogusItem ) , -1 );
            player.addStat( StatList.getObjectUseStats( originalItem ) );
        }

        return result;
    }

    // Item overrides

    @Override
    public String getItemStackDisplayName( ItemStack stack )
    {
        String displayName = null;
        ItemStack proxyBlockItemStack = oreInfo.proxyBlockItemStack();
        if( proxyBlockItemStack != null )
        {
            displayName = proxyBlockItemStack.getItem().getItemStackDisplayName( proxyBlockItemStack );
        }
        else
        {
            String localizedName = oreInfo.localizedName();
            displayName = localizedName != null ? localizedName : getRegistryName().toString();
        }

        // Recolor while preserving other format codes
        return EnumRarity.EPIC.rarityColor.toString()
            + displayName.replaceAll( "§[0-9A-F]" , EnumRarity.EPIC.rarityColor.toString() );
    }

    @Override
    public EnumRarity getRarity( ItemStack stack )
    {
        return EnumRarity.EPIC;
    }
}
