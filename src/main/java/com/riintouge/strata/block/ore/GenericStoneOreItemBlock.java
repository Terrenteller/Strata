package com.riintouge.strata.block.ore;

import com.riintouge.strata.Strata;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class GenericStoneOreItemBlock extends ItemBlock
{
    public GenericStoneOreItemBlock( Block block )
    {
        super( block );

        String blockRegistryName = block.getRegistryName().toString();
        setRegistryName( blockRegistryName );
        setUnlocalizedName( blockRegistryName );

        setCreativeTab( Strata.ITEM_TAB );
    }

    // ItemBlock overrides

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
        // Ore item blocks should not be treated as blocks, especially if fortune is involved!
        return player != null && player.isCreative()
            ? super.onItemUse( player , worldIn , pos , hand , facing , hitX , hitY , hitZ )
            : EnumActionResult.FAIL;
    }

    @Override
    public String getUnlocalizedName( ItemStack stack )
    {
        // Block.getUnlocalizedName tacks on a prefix for some reason.
        // Strata localization doesn't make a distinction between blocks and items.
        // Proxy ores need to report their wrapped block's unmodified, unlocalized name.
        return this.block.getUnlocalizedName().replaceFirst( "tile." , "" );
    }
}
