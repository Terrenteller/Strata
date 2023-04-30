package com.riintouge.strata.item;

import com.riintouge.strata.misc.LambdaNoThrow;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemHelper
{
    public static EnumActionResult onItemUseWithStatisticsFix(
        EntityPlayer player,
        EnumHand hand,
        LambdaNoThrow< EnumActionResult > onItemUseAction )
    {
        Item itemBefore = player.getHeldItem( hand ).getItem();
        EnumActionResult result = onItemUseAction.invoke();

        if( result == EnumActionResult.SUCCESS )
        {
            // Successfully using a finite item will probably call ItemStack.shrink(), even for creative players,
            // and ItemStack.onItemUse() will increment a bogus stat if the stack size becomes zero.
            // For more information, this starts with PlayerControllerMP.processRightClickBlock().
            Item itemAfter = player.getHeldItem( hand ).getItem();
            if( itemAfter != itemBefore )
            {
                player.addStat( StatList.getObjectUseStats( itemAfter ) , -1 );
                player.addStat( StatList.getObjectUseStats( itemBefore ) );
            }
        }

        return result;
    }

    public static EnumActionResult placeItemAsBlock(
        Item item,
        Block block,
        EntityPlayer player,
        World worldIn,
        BlockPos pos,
        EnumHand hand,
        EnumFacing facing,
        float hitX,
        float hitY,
        float hitZ )
    {
        // Most of this method was taken from ItemBlock

        ItemStack itemStack = player.getHeldItem( hand );
        if( itemStack.isEmpty() )
            return EnumActionResult.FAIL;

        Block useBlock = worldIn.getBlockState( pos ).getBlock();
        if( !useBlock.isReplaceable( worldIn , pos ) )
            pos = pos.offset( facing );

        if( !player.canPlayerEdit( pos , facing , itemStack ) || !worldIn.mayPlace( block , pos , false , facing , null ) )
            return EnumActionResult.FAIL;

        int metadata = item.getMetadata( itemStack.getMetadata() );
        IBlockState sampleBlockState = block.getStateForPlacement( worldIn , pos , facing , hitX , hitY , hitZ , metadata , player , hand );
        // tl;dr 11 will cause a block update, send the change to clients, and re-render on the main thread
        if( !worldIn.setBlockState( pos , sampleBlockState , 11 ) )
            return EnumActionResult.FAIL; // The original behaviour is SUCCESS but that's simply not true

        IBlockState placedBlockState = worldIn.getBlockState( pos );
        if( placedBlockState.getBlock() == block )
        {
            ItemBlock.setTileEntityNBT( worldIn , player , pos , itemStack );
            block.onBlockPlacedBy( worldIn , pos , placedBlockState , player , itemStack );

            if( player instanceof EntityPlayerMP )
                CriteriaTriggers.PLACED_BLOCK.trigger( (EntityPlayerMP)player , pos , itemStack );
        }

        SoundType soundtype = placedBlockState.getBlock().getSoundType( placedBlockState , worldIn , pos , player );
        worldIn.playSound(
            player,
            pos,
            soundtype.getPlaceSound(),
            SoundCategory.BLOCKS,
            ( soundtype.getVolume() + 1.0f ) / 2.0f,
            soundtype.getPitch() * 0.8f );

        if( !player.isCreative() )
            itemStack.shrink( 1 );

        return EnumActionResult.SUCCESS;
    }
}
