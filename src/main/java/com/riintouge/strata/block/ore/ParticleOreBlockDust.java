package com.riintouge.strata.block.ore;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleBlockDust;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.lang3.NotImplementedException;

import javax.annotation.Nullable;

@SideOnly( Side.CLIENT )
public class ParticleOreBlockDust extends ParticleBlockDust
{
    protected ParticleOreBlockDust(
        World worldIn,
        double xCoordIn,
        double yCoordIn,
        double zCoordIn,
        double xSpeedIn,
        double ySpeedIn,
        double zSpeedIn,
        IBlockState state )
    {
        super( worldIn , xCoordIn , yCoordIn , zCoordIn , xSpeedIn , ySpeedIn , zSpeedIn , state );
    }

    // Statics

    @SideOnly( Side.CLIENT )
    public static class Factory implements IParticleFactory
    {
        @Nullable
        public Particle createParticle(
            IBlockState blockState,
            World worldIn,
            double xCoordIn,
            double yCoordIn,
            double zCoordIn,
            double xSpeedIn,
            double ySpeedIn,
            double zSpeedIn )
        {
            ParticleOreBlockDust particle = blockState.getRenderType() != EnumBlockRenderType.INVISIBLE
                ? new ParticleOreBlockDust( worldIn , xCoordIn , yCoordIn , zCoordIn , xSpeedIn , ySpeedIn , zSpeedIn , blockState )
                : null;

            return particle != null ? particle.init() : null;
        }

        // IParticleFactory overrides

        @Nullable
        @Override
        public Particle createParticle(
            int particleID,
            World worldIn,
            double xCoordIn,
            double yCoordIn,
            double zCoordIn,
            double xSpeedIn,
            double ySpeedIn,
            double zSpeedIn,
            int ... arguments )
        {
            throw new NotImplementedException( "This particle should not be registered with ParticleManager so this method should never be called!" );
        }
    }
}

