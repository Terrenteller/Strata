package com.riintouge.strata.block;

import com.riintouge.strata.image.SquareMipmapHelper;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.ParticleDigging;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.Random;
import java.util.function.Supplier;

@SideOnly( Side.CLIENT )
public class ParticleHelper
{
    public static final int DEFAULT_PARTICLE_COLOR = -16777216; // Taken from BlockFalling

    public static int getParticleFallingColor( ResourceLocation textureResourceLocation )
    {
        TextureAtlasSprite texture = Minecraft.getMinecraft()
            .getTextureMapBlocks()
            .getTextureExtry( textureResourceLocation.toString() );

        if( texture != null )
        {
            SquareMipmapHelper mipmaps = new SquareMipmapHelper( texture.getFrameTextureData( 0 ) );
            return mipmaps.mipmapForEdgeLength( 1 )[ 0 ];
        }

        return DEFAULT_PARTICLE_COLOR;
    }

    public static void addDestroyEffects(
        World world,
        BlockPos pos,
        ParticleManager manager,
        @Nullable Supplier< TextureAtlasSprite > textureGetter )
    {
        IBlockState state = world.getBlockState( pos ).getActualState( world , pos );
        if( state.getBlock() == Blocks.AIR )
            return;

        AxisAlignedBB AABB = state.getBoundingBox( world , pos );
        int blockId = Block.getIdFromBlock( state.getBlock() );

        double xVelocityOrigin = AABB.minX + ( ( AABB.maxX - AABB.minX ) / 2.0d );
        double yVelocityOrigin = AABB.minY + ( ( AABB.maxY - AABB.minY ) / 2.0d );
        double zVelocityOrigin = AABB.minZ + ( ( AABB.maxZ - AABB.minZ ) / 2.0d );

        int xParticlePlanes = (int)Math.ceil( ( AABB.maxX - AABB.minX ) / 0.25d );
        int yParticlePlanes = (int)Math.ceil( ( AABB.maxY - AABB.minY ) / 0.25d );
        int zParticlePlanes = (int)Math.ceil( ( AABB.maxZ - AABB.minZ ) / 0.25d );

        double xBaseOffset = AABB.minX + 0.125;
        double yBaseOffset = AABB.minY + 0.125;
        double zBaseOffset = AABB.minZ + 0.125;

        // When an axis only has a single plane of particles which is co-planar to the side of a full block AABB,
        // move the velocity origin on that axis to the corresponding face so the particles pop away from the side.
        // Otherwise, particles from small blocks like buttons may fly into the block they were attached to.

        if( xParticlePlanes == 1 )
        {
            if( AABB.minX == 0.0d )
            {
                xVelocityOrigin = 0.0d;
            }
            else if( AABB.maxX == 1.0d )
            {
                xVelocityOrigin = 1.0f;
                xBaseOffset = 0.875;
            }
        }

        if( yParticlePlanes == 1 )
        {
            if( AABB.minY == 0.0d )
            {
                yVelocityOrigin = 0.0d;
            }
            else if( AABB.maxY == 1.0d )
            {
                yVelocityOrigin = 1.0f;
                yBaseOffset = 0.875;
            }
        }

        if( zParticlePlanes == 1 )
        {
            if( AABB.minZ == 0.0d )
            {
                zVelocityOrigin = 0.0d;
            }
            else if( AABB.maxZ == 1.0d )
            {
                zVelocityOrigin = 1.0f;
                zBaseOffset = 0.875;
            }
        }

        // This loop sampled from ParticleManager.addBlockDestroyEffects()
        for( int xPlaneIndex = 0 ; xPlaneIndex < xParticlePlanes ; xPlaneIndex++ )
        {
            double xOffset = xBaseOffset + ( 0.25 * xPlaneIndex );

            for( int yPlaneIndex = 0 ; yPlaneIndex < yParticlePlanes ; yPlaneIndex++ )
            {
                double yOffset = yBaseOffset + ( 0.25 * yPlaneIndex );

                for( int zPlaneIndex = 0 ; zPlaneIndex < zParticlePlanes ; zPlaneIndex++ )
                {
                    double zOffset = zBaseOffset + ( 0.25 * zPlaneIndex );

                    ParticleDigging particleDigging = (ParticleDigging)new ParticleDigging.Factory().createParticle(
                        0, // unused
                        world,
                        (double)pos.getX() + xOffset,
                        (double)pos.getY() + yOffset,
                        (double)pos.getZ() + zOffset,
                        xOffset - xVelocityOrigin,
                        yOffset - yVelocityOrigin,
                        zOffset - zVelocityOrigin,
                        blockId );

                    if( textureGetter != null )
                        particleDigging.setParticleTexture( textureGetter.get() );

                    particleDigging.setBlockPos( pos );
                    manager.addEffect( particleDigging );
                }
            }
        }
    }

    public static void createHitParticle(
        IBlockState state,
        World worldObj,
        RayTraceResult target,
        ParticleManager manager,
        Random random,
        TextureAtlasSprite texture )
    {
        BlockPos blockPos = target.getBlockPos();

        // This logic sampled from ParticleManager.addBlockHitEffects()
        double x = (double)blockPos.getX();
        double y = (double)blockPos.getY();
        double z = (double)blockPos.getZ();
        AxisAlignedBB AABB = state.getBoundingBox( worldObj , blockPos );
        double d0 = x + random.nextDouble() * ( AABB.maxX - AABB.minX - 0.2d ) + 0.1d + AABB.minX;
        double d1 = y + random.nextDouble() * ( AABB.maxY - AABB.minY - 0.2d ) + 0.1d + AABB.minY;
        double d2 = z + random.nextDouble() * ( AABB.maxZ - AABB.minZ - 0.2d ) + 0.1d + AABB.minZ;

        switch( target.sideHit.getIndex() )
        {
            case 0: // DOWN
                d1 = y + AABB.minY - 0.1d;
                break;
            case 1: // UP
                d1 = y + AABB.maxY + 0.1d;
                break;
            case 2: // NORTH
                d2 = z + AABB.minZ - 0.1d;
                break;
            case 3: // SOUTH
                d2 = z + AABB.maxZ + 0.1d;
                break;
            case 4: // WEST
                d0 = x + AABB.minX - 0.1d;
                break;
            case 5: // EAST
                d0 = x + AABB.maxX + 0.1d;
                break;
            default:
                return;
        }

        ParticleDigging particleDigging = (ParticleDigging)new ParticleDigging.Factory().createParticle(
            0, // unused
            worldObj,
            d0,
            d1,
            d2,
            0.0d,
            0.0d,
            0.0d,
            Block.getIdFromBlock( state.getBlock() ) );

        particleDigging
            .setBlockPos( blockPos )
            .multiplyVelocity( 0.2f )
            .multipleParticleScaleBy( 0.6f )
            .setParticleTexture( texture );

        manager.addEffect( particleDigging );
    }
}
