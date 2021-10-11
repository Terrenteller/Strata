package com.riintouge.strata.misc;

import io.netty.buffer.ByteBuf;

import java.nio.charset.Charset;

public class ByteBufStream implements IDataStream
{
    protected final ByteBuf buf;

    public ByteBufStream( ByteBuf buf )
    {
        this.buf = buf;
    }

    // IDataStream overrides

    @Override
    public boolean readBoolean() throws IndexOutOfBoundsException
    {
        return buf.readBoolean();
    }

    @Override
    public ByteBufStream write( boolean value ) throws IndexOutOfBoundsException
    {
        buf.writeBoolean( value );
        return this;
    }

    @Override
    public char readChar() throws IndexOutOfBoundsException
    {
        return buf.readChar();
    }

    @Override
    public ByteBufStream write( char value ) throws IndexOutOfBoundsException
    {
        buf.writeChar( value );
        return this;
    }

    @Override
    public double readDouble() throws IndexOutOfBoundsException
    {
        return buf.readChar();
    }

    @Override
    public ByteBufStream write( double value ) throws IndexOutOfBoundsException
    {
        buf.writeDouble( value );
        return this;
    }

    @Override
    public float readFloat() throws IndexOutOfBoundsException
    {
        return buf.readFloat();
    }

    @Override
    public ByteBufStream write( float value ) throws IndexOutOfBoundsException
    {
        buf.writeFloat( value );
        return this;
    }

    @Override
    public int readInt() throws IndexOutOfBoundsException
    {
        return buf.readInt();
    }

    @Override
    public ByteBufStream write( int value ) throws IndexOutOfBoundsException
    {
        buf.writeInt( value );
        return this;
    }

    @Override
    public long readLong() throws IndexOutOfBoundsException
    {
        return buf.readLongLE();
    }

    @Override
    public ByteBufStream write( long value ) throws IndexOutOfBoundsException
    {
        buf.writeLongLE( value );
        return this;
    }

    @Override
    public short readShort() throws IndexOutOfBoundsException
    {
        return buf.readShort();
    }

    @Override
    public ByteBufStream write( short value ) throws IndexOutOfBoundsException
    {
        buf.writeShort( value );
        return this;
    }

    @Override
    public String readString() throws IndexOutOfBoundsException
    {
        int length = buf.readInt();
        return buf.readCharSequence( length , Charset.defaultCharset() ).toString();
    }

    @Override
    public ByteBufStream write( String value ) throws IndexOutOfBoundsException
    {
        byte[] data = value.getBytes();
        buf.writeInt( data.length );
        buf.writeCharSequence( value.subSequence( 0 , data.length ) , Charset.defaultCharset() );
        return this;
    }
}
