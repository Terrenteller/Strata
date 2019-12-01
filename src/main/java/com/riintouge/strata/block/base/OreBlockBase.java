package com.riintouge.strata.block.base;

import com.riintouge.strata.Strata;
import com.riintouge.strata.block.*;
import com.riintouge.strata.property.IMetaPropertyProvider;
import com.riintouge.strata.property.UnlistedPropertyHostRock;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.property.IExtendedBlockState;

import javax.annotation.Nullable;
import java.util.Random;

public abstract class OreBlockBase extends Block implements IMetaPropertyProvider
{
    public OreBlockBase( Material material )
    {
        super( material );

        setCreativeTab( Strata.BLOCK_TAB );
    }

    // Block overrides

    @Override
    protected boolean canSilkHarvest()
    {
        return false;
    }

    @Override
    public boolean canSilkHarvest( World world , BlockPos pos , IBlockState state , EntityPlayer player )
    {
        return false;
    }

    @Override
    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer.Builder( this )
            .add( getMetaPropertyProvider().property() )
            .add( UnlistedPropertyHostRock.PROPERTY )
            .build();
    }

    @Override
    public TileEntity createTileEntity( World world , IBlockState state )
    {
        return new DynamicOreHostTileEntity();
    }

    @Override
    public IBlockState getExtendedState( IBlockState state , IBlockAccess world , BlockPos pos )
    {
        IExtendedBlockState extendedState = (IExtendedBlockState)state;
        TileEntity entity = world.getTileEntity( pos );
        if( entity instanceof DynamicOreHostTileEntity )
        {
            String cachedHost = ((DynamicOreHostTileEntity)entity).getCachedHost();
            extendedState = extendedState.withProperty( UnlistedPropertyHostRock.PROPERTY , cachedHost );
        }
        return extendedState;
    }

    @Override
    public Item getItemDropped( IBlockState state , Random rand , int fortune )
    {
        return net.minecraft.init.Items.AIR;
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
            Item.getItemFromBlock( this ),
            1,
            this.getMetaFromState( world.getBlockState( pos ) ) );
    }

    @Override
    public void getSubBlocks( CreativeTabs itemIn , NonNullList< ItemStack > items )
    {
        for( int index = 0 ; index < getMetaPropertyProvider().propertyValues().length ; index++ )
            items.add( new ItemStack( this , 1 , index ) );
    }

    @Override
    public void harvestBlock( World world, EntityPlayer player , BlockPos pos , IBlockState state , @Nullable TileEntity entity , ItemStack tool )
    {
        super.harvestBlock( world , player , pos , state , entity , tool );
        world.setBlockToAir( pos );
    }

    @Override
    public boolean hasTileEntity( IBlockState state )
    {
        return true;
    }

    @Override
    public boolean removedByPlayer( IBlockState state , World world , BlockPos pos , EntityPlayer player , boolean willHarvest )
    {
        // See BlockFlowerPot for details about this logic.
        // tldr: If it will harvest, delay deletion of the block until after harvestBlock.
        // BlockFlowerPot says getDrops, but that does not appear to be called.
        return willHarvest || super.removedByPlayer( state , world , pos , player , willHarvest );
    }

    @Override
    public void updateTick( World world , BlockPos pos , IBlockState state , Random rand )
    {
        super.updateTick( world , pos , state , rand );

        // Only the server should poll
        if( !world.isRemote )
            ((DynamicOreHostTileEntity)world.getTileEntity( pos ) ).pollHost();
    }
}
