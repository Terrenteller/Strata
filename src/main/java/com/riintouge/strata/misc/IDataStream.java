package com.riintouge.strata.misc;

public interface IDataStream
{
    boolean readBoolean();
    ByteBufStream write( boolean value );

    char readChar();
    ByteBufStream write( char value );

    double readDouble();
    ByteBufStream write( double value );

    float readFloat();
    ByteBufStream write( float value );

    int readInt();
    ByteBufStream write( int value );

    long readLong();
    ByteBufStream write( long value );

    short readShort();
    ByteBufStream write( short value );

    String readString();
    ByteBufStream write( String value );
}
