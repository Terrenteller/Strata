package com.riintouge.strata.block.ore;

import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.property.IExtendedBlockState;

import javax.annotation.Nonnull;
import java.util.Random;

public class ActivatableOreBlock extends OreBlock
{
    public ActivatableOreBlock( IOreInfo oreInfo )
    {
        super( oreInfo );

        setTickRandomly( true );
    }

    protected boolean isActive( IBlockAccess world , BlockPos pos )
    {
        OreBlockTileEntity tileEntity = getTileEntity( world , pos );
        return tileEntity != null && tileEntity.isActive();
    }

    protected void setActive( World worldIn , BlockPos pos , boolean activated )
    {
        if( !worldIn.isRemote )
        {
            OreBlockTileEntity tileEntity = getTileEntity( worldIn , pos );
            if( tileEntity != null )
                tileEntity.setActive( activated );
        }
    }

    // OreBlock overrides

    @Nonnull
    @Override
    public IExtendedBlockState getCompleteExtendedState(
        OreBlockTileEntity entity,
        IBlockState state,
        IBlockAccess world,
        BlockPos pos )
    {
        IExtendedBlockState extendedState = super.getCompleteExtendedState( entity , state , world , pos );
        return entity != null
            ? extendedState.withProperty( UnlistedPropertyOreFlags.PROPERTY , entity.getFlags() )
            : extendedState;
    }

    @Nonnull
    @Override
    public IExtendedBlockState getDefaultExtendedState( IBlockState state )
    {
        return super.getDefaultExtendedState( state )
            .withProperty( UnlistedPropertyOreFlags.PROPERTY , UnlistedPropertyOreFlags.DEFAULT );
    }

    // Block overrides

    @Override
    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer.Builder( this )
            .add( UnlistedPropertyHostRock.PROPERTY )
            .add( UnlistedPropertyOreFlags.PROPERTY )
            .build();
    }

    @Override
    public boolean onBlockActivated(
        World worldIn,
        BlockPos pos,
        IBlockState state,
        EntityPlayer playerIn,
        EnumHand hand,
        EnumFacing facing,
        float hitX,
        float hitY,
        float hitZ )
    {
        super.onBlockActivated( worldIn , pos , state , playerIn , hand , facing , hitX , hitY , hitZ );

        setActive( worldIn , pos , true );

        // Vanilla redstone ore, whose activation behaviour this class was designed to mimic, does not eat clicks
        return false;
    }

    @Override
    public void onBlockClicked( World worldIn , BlockPos pos , EntityPlayer playerIn )
    {
        super.onBlockClicked( worldIn , pos , playerIn );

        setActive( worldIn , pos , true );
    }

    @Override
    public void onEntityWalk( World worldIn , BlockPos pos , Entity entityIn )
    {
        super.onEntityWalk( worldIn , pos , entityIn );

        setActive( worldIn , pos , true );
    }

    @Override
    public void randomTick( World worldIn , BlockPos pos , IBlockState state , Random random )
    {
        // Deactivate before we may melt by calling super (thus invalidating the tile entity)
        setActive( worldIn , pos , false );

        super.randomTick( worldIn , pos , state , random );
    }
}
