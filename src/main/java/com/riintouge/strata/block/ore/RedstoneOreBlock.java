package com.riintouge.strata.block.ore;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Random;

public class RedstoneOreBlock extends ActivatableOreBlock
{
    protected static int DORMANT_LIGHT_LEVEL = 0;
    protected static int ACTIVE_LIGHT_LEVEL = 9;

    public RedstoneOreBlock( IOreInfo oreInfo )
    {
        super( oreInfo );
    }

    // ActivatableOreBlock overrides

    protected void setActive( World worldIn , BlockPos pos , boolean activated )
    {
        super.setActive( worldIn , pos , activated );

        if( worldIn.isRemote && activated )
            spawnRedstoneParticles( worldIn , pos );
    }

    // OreBlock overrides

    @Override
    public int getLightValue( IBlockState state , IBlockAccess world , BlockPos pos )
    {
        int lightValue = super.getLightValue( state , world , pos );
        return lightValue >= ACTIVE_LIGHT_LEVEL
            ? lightValue // Shortcut
            : Math.max( lightValue , isActive( world , pos ) ? ACTIVE_LIGHT_LEVEL : DORMANT_LIGHT_LEVEL );
    }

    @Override
    public boolean isToolEffective( String type , IBlockState state )
    {
        // Block.isToolEffective() suggests pickaxes are not meant to be effective on redstone ore, but in practise
        // that code path never executes. Nothing calls ForgeHooks.isToolEffective() and ItemPickaxe.getDestroySpeed()
        // will never call ItemTool.getDestroySpeed() because the material of redstone ore is rock.
        return super.isToolEffective( type , state );
    }

    @Override
    @SideOnly( Side.CLIENT )
    public void randomDisplayTick( IBlockState stateIn , World worldIn , BlockPos pos , Random rand )
    {
        super.randomDisplayTick( stateIn , worldIn , pos , rand );

        if( isActive( worldIn , pos ) )
            spawnRedstoneParticles( worldIn , pos );
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
