package com.riintouge.strata.block.ore;

import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.lang3.NotImplementedException;

import java.util.Random;

public class RedstoneOreBlock extends OreBlock
{
    public RedstoneOreBlock( IOreInfo oreInfo )
    {
        super( oreInfo );
        this.oreInfo = oreInfo;

        setTickRandomly( true );
    }

    protected boolean getRedstoneActivationState( IBlockAccess world , BlockPos pos )
    {
        OreBlockTileEntity tileEntity = getTileEntity( world , pos );
        return tileEntity != null && tileEntity.isActive();
    }

    protected void setRedstoneActivationState( World worldIn , BlockPos pos , boolean activated )
    {
        if( worldIn.isRemote )
        {
            if( activated )
                spawnRedstoneParticles( worldIn , pos );
        }
        else
        {
            OreBlockTileEntity tileEntity = getTileEntity( worldIn , pos );
            if( tileEntity != null )
                tileEntity.setActive( activated );
        }
    }

    // OreBlock overrides

    @Override
    public IExtendedBlockState getCompleteExtendedState(
        OreBlockTileEntity entity,
        IBlockState state,
        IBlockAccess world,
        BlockPos pos )
    {
        return super.getCompleteExtendedState( entity , state , world , pos )
            .withProperty( UnlistedPropertyActiveState.PROPERTY , entity != null && entity.isActive() );
    }

    @Override
    public IExtendedBlockState getDefaultExtendedState( IBlockState state )
    {
        return ( (IExtendedBlockState)state )
            .withProperty( UnlistedPropertyHostRock.PROPERTY , UnlistedPropertyHostRock.DEFAULT )
            .withProperty( UnlistedPropertyActiveState.PROPERTY , UnlistedPropertyActiveState.DEFAULT );
    }

    // Block overrides

    @Override
    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer.Builder( this )
            .add( UnlistedPropertyHostRock.PROPERTY )
            .add( UnlistedPropertyActiveState.PROPERTY )
            .build();
    }

    @Deprecated
    @Override
    public int getLightValue( IBlockState state )
    {
        throw new NotImplementedException( "Use the positional overload instead!" );
    }

    @Override
    public int getLightValue( IBlockState state , IBlockAccess world , BlockPos pos )
    {
        boolean active = getRedstoneActivationState( world , pos );
        return (int)( 15.0f * ( active ? 0.625 : 0.0f ) );
    }

    @Override
    public boolean isToolEffective( String type , IBlockState state )
    {
        // Vanilla behaviour
        return false;
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
        this.setRedstoneActivationState( worldIn , pos , true );

        return super.onBlockActivated( worldIn , pos , state , playerIn , hand , facing , hitX , hitY , hitZ );
    }

    @Override
    public void onBlockClicked( World worldIn , BlockPos pos , EntityPlayer playerIn )
    {
        super.onBlockClicked( worldIn , pos , playerIn );

        this.setRedstoneActivationState( worldIn , pos , true );
    }

    @Override
    public void onEntityWalk( World worldIn , BlockPos pos , Entity entityIn )
    {
        super.onEntityWalk( worldIn , pos , entityIn );

        this.setRedstoneActivationState( worldIn , pos , true );
    }

    @Override
    @SideOnly( Side.CLIENT )
    public void randomDisplayTick( IBlockState stateIn , World worldIn , BlockPos pos , Random rand )
    {
        super.randomDisplayTick( stateIn , worldIn , pos , rand );

        if( getRedstoneActivationState( worldIn , pos ) )
            spawnRedstoneParticles( worldIn , pos );
    }

    @Override
    public void updateTick( World worldIn , BlockPos pos , IBlockState state , Random rand )
    {
        super.updateTick( worldIn , pos , state , rand );

        if( !worldIn.isRemote )
            setRedstoneActivationState( worldIn , pos , false );
    }

    // spawnRedstoneParticles originated from BlockRedstoneOre.java (Forge 1.12.2-14.23.4.2705).
    // No modifications to this method were made for diff purposes.

    private void spawnRedstoneParticles(World worldIn, BlockPos pos)
    {
        Random random = worldIn.rand;
        double d0 = 0.0625D;

        for (int i = 0; i < 6; ++i)
        {
            double d1 = (double)((float)pos.getX() + random.nextFloat());
            double d2 = (double)((float)pos.getY() + random.nextFloat());
            double d3 = (double)((float)pos.getZ() + random.nextFloat());

            if (i == 0 && !worldIn.getBlockState(pos.up()).isOpaqueCube())
            {
                d2 = (double)pos.getY() + 0.0625D + 1.0D;
            }

            if (i == 1 && !worldIn.getBlockState(pos.down()).isOpaqueCube())
            {
                d2 = (double)pos.getY() - 0.0625D;
            }

            if (i == 2 && !worldIn.getBlockState(pos.south()).isOpaqueCube())
            {
                d3 = (double)pos.getZ() + 0.0625D + 1.0D;
            }

            if (i == 3 && !worldIn.getBlockState(pos.north()).isOpaqueCube())
            {
                d3 = (double)pos.getZ() - 0.0625D;
            }

            if (i == 4 && !worldIn.getBlockState(pos.east()).isOpaqueCube())
            {
                d1 = (double)pos.getX() + 0.0625D + 1.0D;
            }

            if (i == 5 && !worldIn.getBlockState(pos.west()).isOpaqueCube())
            {
                d1 = (double)pos.getX() - 0.0625D;
            }

            if (d1 < (double)pos.getX() || d1 > (double)(pos.getX() + 1) || d2 < 0.0D || d2 > (double)(pos.getY() + 1) || d3 < (double)pos.getZ() || d3 > (double)(pos.getZ() + 1))
            {
                worldIn.spawnParticle(EnumParticleTypes.REDSTONE, d1, d2, d3, 0.0D, 0.0D, 0.0D);
            }
        }
    }
}
