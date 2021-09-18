package com.riintouge.strata.block;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.ParticleDigging;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Random;

@SideOnly( Side.CLIENT )
public class ParticleHelper
{
    public static final int DefaultParticleColor = -16777216; // Taken from BlockFalling

    public static int getParticleFallingColor( ResourceLocation textureResourceLocation )
    {
        TextureAtlasSprite texture = Minecraft.getMinecraft()
            .getTextureMapBlocks()
            .getTextureExtry( textureResourceLocation.toString() );

        if( texture != null )
        {
            // Use the first pixel of the smallest mipmap as the average color
            int[][] frameData = texture.getFrameTextureData( 0 );
            return frameData[ frameData.length - 1 ][ 0 ];
        }

        return DefaultParticleColor;
    }

    public static void addDestroyEffects(
        World world,
        BlockPos pos,
        ParticleManager manager,
        Random random,
        ProtoBlockTextureMap textureMap )
    {
        IBlockState state = world.getBlockState( pos ).getActualState( world , pos );
        AxisAlignedBB AABB = state.getBoundingBox( world , pos );
        int blockId = Block.getIdFromBlock( state.getBlock() );

        double xVelocityOrigin = AABB.minX + ( ( AABB.maxX - AABB.minX ) / 2.0d );
        double yVelocityOrigin = AABB.minY + ( ( AABB.maxY - AABB.minY ) / 2.0d );
        double zVelocityOrigin = AABB.minZ + ( ( AABB.maxZ - AABB.minZ ) / 2.0d );

        int xParticles = (int)Math.ceil( ( AABB.maxX - AABB.minX ) / 0.25d );
        int yParticles = (int)Math.ceil( ( AABB.maxY - AABB.minY ) / 0.25d );
        int zParticles = (int)Math.ceil( ( AABB.maxZ - AABB.minZ ) / 0.25d );

        double xOffset = AABB.minX + 0.125;
        double yOffset = AABB.minY + 0.125;
        double zOffset = AABB.minZ + 0.125;

        // When an axis only has a single layer of particles and either min or max is co-planar to a full block face,
        // move the velocity origin on that axis to the corresponding face so the particles pop away from the side.
        // Otherwise, particles from small blocks like buttons may fly into the block they were attached to.

        if( xParticles == 1 )
        {
            if( AABB.minX == 0.0d )
            {
                xVelocityOrigin = 0.0d;
            }
            else if( AABB.maxX == 1.0d )
            {
                xVelocityOrigin = 1.0f;
                xOffset = 0.875;
            }
        }

        if( yParticles == 1 )
        {
            if( AABB.minY == 0.0d )
            {
                yVelocityOrigin = 0.0d;
            }
            else if( AABB.maxY == 1.0d )
            {
                yVelocityOrigin = 1.0f;
                yOffset = 0.875;
            }
        }

        if( zParticles == 1 )
        {
            if( AABB.minZ == 0.0d )
            {
                zVelocityOrigin = 0.0d;
            }
            else if( AABB.maxZ == 1.0d )
            {
                zVelocityOrigin = 1.0f;
                zOffset = 0.875;
            }
        }

        // This loop sampled from ParticleManager.addBlockDestroyEffects()
        for( int x = 0 ; x < xParticles ; ++x )
        {
            double d0 = xOffset + ( 0.25 * x );

            for( int y = 0 ; y < yParticles ; ++y )
            {
                double d1 = yOffset + ( 0.25 * y );

                for( int z = 0 ; z < zParticles ; ++z )
                {
                    double d2 = zOffset + ( 0.25 * z );

                    ParticleDigging particleDigging = (ParticleDigging)new ParticleDigging.Factory().createParticle(
                        0, // unused
                        world,
                        (double)pos.getX() + d0,
                        (double)pos.getY() + d1,
                        (double)pos.getZ() + d2,
                        d0 - xVelocityOrigin,
                        d1 - yVelocityOrigin,
                        d2 - zVelocityOrigin,
                        blockId );

                    TextureAtlasSprite texture = textureMap.getTexture( EnumFacing.VALUES[ random.nextInt( 6 ) ] );
                    particleDigging.setBlockPos( pos ).setParticleTexture( texture );
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
