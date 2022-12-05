package com.riintouge.strata.network;

import com.riintouge.strata.block.ore.OreBlock;
import io.netty.buffer.ByteBuf;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class OreBlockLandingEffectMessage implements IMessage
{
    protected float xPos;
    protected float yPos;
    protected float zPos;
    protected int particleCount;

    public OreBlockLandingEffectMessage()
    {
        // Nothing to do, but required
    }

    public OreBlockLandingEffectMessage( float xPos , float yPos , float zPos , int particleCount )
    {
        this.xPos = xPos;
        this.yPos = yPos;
        this.zPos = zPos;
        this.particleCount = particleCount;
    }

    // IMessage overrides

    @Override
    public void toBytes( ByteBuf buf )
    {
        buf.writeFloat( xPos );
        buf.writeFloat( yPos );
        buf.writeFloat( zPos );
        buf.writeInt( particleCount );
    }

    @Override
    public void fromBytes( ByteBuf buf )
    {
        xPos = buf.readFloat();
        yPos = buf.readFloat();
        zPos = buf.readFloat();
        particleCount = buf.readInt();
    }

    // Nested classes

    public static final class Handler implements IMessageHandler< OreBlockLandingEffectMessage , IMessage >
    {
        @Override
        public IMessage onMessage( OreBlockLandingEffectMessage message , MessageContext ctx )
        {
            // This should always match ParticleManager.world which we can't access and use here
            WorldClient world = Minecraft.getMinecraft().world;
            if( world == null )
                return null;

            // This message does not carry host or ore block IDs to keep things generic for "type" safety
            // (because I don't know if or how block IDs are synchronized between client and server).
            // Instead, we grab the block beneath the center of landing and go from there.
            // We may still end up with an air block and no particles if landing on a ledge.
            // This matches vanilla behaviour.
            BlockPos blockPos = new BlockPos( message.xPos , Math.round( message.yPos ) - 0.01 , message.zPos );
            Block block = world.getBlockState( blockPos ).getBlock();
            if( !( block instanceof OreBlock ) )
                return null;

            OreBlock oreBlock = (OreBlock)block;
            oreBlock.addLandingEffects( world , blockPos , message.xPos , message.yPos , message.zPos , message.particleCount );

            return null;
        }
    }
}
