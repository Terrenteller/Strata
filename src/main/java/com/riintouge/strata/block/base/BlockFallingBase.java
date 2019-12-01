package com.riintouge.strata.block.base;

import com.riintouge.strata.Strata;
import com.riintouge.strata.block.StateUtil;
import com.riintouge.strata.property.IMetaPropertyProvider;
import net.minecraft.block.BlockFalling;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public abstract class BlockFallingBase extends BlockFalling implements IMetaPropertyProvider
{
    protected String name;

    public BlockFallingBase( Material material )
    {
        super( material );

        setCreativeTab( Strata.BLOCK_TAB );
    }

    // Block Overrides


    @Override
    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer.Builder( this )
            .add( getMetaPropertyProvider().property() )
            .build();
    }

    @Override
    public int damageDropped( IBlockState state )
    {
        return getMetaFromState( state );
    }

    @Override
    public int getMetaFromState( IBlockState state )
    {
        return StateUtil.getValue( state , getMetaPropertyProvider().property() , getMetaPropertyProvider().propertyValues()[ 0 ] ).getValue();
    }

    @Override
    public ItemStack getPickBlock( IBlockState state , RayTraceResult target , World world , BlockPos pos , EntityPlayer player )
    {
        return new ItemStack(
            Item.getItemFromBlock( this ) ,
            1 ,
            this.getMetaFromState( world.getBlockState( pos ) ) );
    }
}
