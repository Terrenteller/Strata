package com.riintouge.strata.block.geo;

import com.riintouge.strata.block.SpecialBlockPropertyFlags;
import net.minecraft.block.BlockSlab;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemSlab;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
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
}
