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

public class OreBlockRunningEffectMessage implements IMessage
{
    protected float xPos;
    protected float yPos;
    protected float zPos;
    protected float xSpeed;
    protected float ySpeed;
    protected float zSpeed;

    public OreBlockRunningEffectMessage()
    {
        // Nothing to do, but required
    }

    public OreBlockRunningEffectMessage( float xPos , float yPos , float zPos , float xSpeed , float ySpeed , float zSpeed )
    {
        this.xPos = xPos;
        this.yPos = yPos;
        this.zPos = zPos;
        this.xSpeed = xSpeed;
        this.ySpeed = ySpeed;
        this.zSpeed = zSpeed;
    }

    // IMessage overrides

    @Override
    public void toBytes( ByteBuf buf )
    {
        buf.writeFloat( xPos );
        buf.writeFloat( yPos );
        buf.writeFloat( zPos );
        buf.writeFloat( xSpeed );
        buf.writeFloat( ySpeed );
        buf.writeFloat( zSpeed );
    }

    @Override
    public void fromBytes( ByteBuf buf )
    {
        xPos = buf.readFloat();
        yPos = buf.readFloat();
        zPos = buf.readFloat();
        xSpeed = buf.readFloat();
        ySpeed = buf.readFloat();
        zSpeed = buf.readFloat();
    }

    // Nested classes

    public static final class Handler implements IMessageHandler< OreBlockRunningEffectMessage , IMessage >
    {
        @Override
        public IMessage onMessage( OreBlockRunningEffectMessage message , MessageContext ctx )
        {
            // This should always match ParticleManager.world which we can't access and use here
            WorldClient world = Minecraft.getMinecraft().world;
            if( world == null )
                return null;

            // This message does not carry host or ore block IDs to keep things generic for "type" safety
            // (because I don't know if or how block IDs are synchronized between client and server).
            // Instead, we grab the block beneath the center of running and go from there.
            // We may still end up with an air block and no particles if running on a ledge.
            // This matches vanilla behaviour.
            BlockPos blockPos = new BlockPos( message.xPos , Math.floor( message.yPos ) - 0.01f , message.zPos );
            Block block = world.getBlockState( blockPos ).getBlock();
            if( !( block instanceof OreBlock ) )
                return null;

            OreBlock oreBlock = (OreBlock)block;
            oreBlock.addRunningEffects( world , blockPos , message.xPos , message.yPos , message.zPos , message.xSpeed , message.ySpeed , message.zSpeed );

            return null;
        }
    }
}
