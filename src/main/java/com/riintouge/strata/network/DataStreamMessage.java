package com.riintouge.strata.network;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

import java.io.*;

public abstract class DataStreamMessage implements IMessage
{
    protected Exception caughtException = null;

    public abstract void toBytes( DataOutputStream stream ) throws IOException;

    public abstract void fromBytes( DataInputStream stream ) throws IOException;

    // IMessage overrides

    @Override
    public void toBytes( ByteBuf buf )
    {
        try
        {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            toBytes( new DataOutputStream( byteArrayOutputStream ) );
            buf.writeBytes( byteArrayOutputStream.toByteArray() );
        }
        catch( Exception e )
        {
            caughtException = e;
        }
    }

    @Override
    public void fromBytes( ByteBuf buf )
    {
        try
        {
            byte[] data = new byte[ buf.readableBytes() ];
            buf.readBytes( data );
            fromBytes( new DataInputStream( new ByteArrayInputStream( data ) ) );
        }
        catch( Exception e )
        {
            caughtException = e;
        }
    }
}
