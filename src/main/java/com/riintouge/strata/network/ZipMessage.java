package com.riintouge.strata.network;

import io.netty.buffer.ByteBuf;

import java.io.*;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

public abstract class ZipMessage extends DataStreamMessage
{
    @Override
    public void toBytes( ByteBuf buf )
    {
        try
        {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            toBytes( new DataOutputStream( byteArrayOutputStream ) );

            byte[] originalData = byteArrayOutputStream.toByteArray();
            buf.writeInt( originalData.length );

            Deflater deflater = new Deflater( Deflater.BEST_COMPRESSION , false );
            deflater.setInput( originalData );
            deflater.finish();

            // Be aware of, but don't worry about, CPacketCustomPayload length restrictions here.
            // Let network code throw exceptions where it wants.
            byte[] compressedData = new byte[ Short.MAX_VALUE ];
            while( !deflater.finished() )
            {
                int bytesWritten = deflater.deflate( compressedData );
                buf.writeBytes( compressedData , 0 , bytesWritten );
            }
            deflater.end();
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
            byte[] originalData = new byte[ buf.readInt() ];
            byte[] compressedBytes = new byte[ buf.readableBytes() ];
            buf.readBytes( compressedBytes );

            Inflater inflater = new Inflater();
            inflater.setInput( compressedBytes );
            inflater.inflate( originalData );
            inflater.end();

            fromBytes( new DataInputStream( new ByteArrayInputStream( originalData ) ) );
        }
        catch( Exception e )
        {
            caughtException = e;
        }
    }
}
