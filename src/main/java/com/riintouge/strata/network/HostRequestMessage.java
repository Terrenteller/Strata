package com.riintouge.strata.network;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public final class HostRequestMessage implements IMessage
{
    public HostRequestMessage()
    {
        // Nothing to do, but required
    }

    // IMessage overrides

    @Override
    public void toBytes( ByteBuf buf )
    {
        // Nothing to do
    }

    @Override
    public void fromBytes( ByteBuf buf )
    {
        // Nothing to do
    }

    // Nested classes

    public static class Handler implements IMessageHandler< HostRequestMessage , IMessage >
    {
        @Override
        public IMessage onMessage( HostRequestMessage message , MessageContext ctx )
        {
            return new HostResponseMessage();
        }
    }
}
