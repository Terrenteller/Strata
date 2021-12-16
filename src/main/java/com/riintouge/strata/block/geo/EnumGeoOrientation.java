package com.riintouge.strata.block.geo;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public enum EnumGeoOrientation implements IStringSerializable
{
    // Placed by natural means. Does not have an inherent orientation.
    NATURAL ( 0 , 0 , "natural" , 0 ),

    // Placed against the ground. Faces up and in the direction the placer is facing.
    UP_NORTH ( 4 , 7 , "up_north" , 5 ),
    UP_EAST  ( 5 , 4 , "up_east"  , 6 ),
    UP_SOUTH ( 6 , 5 , "up_south" , 7 ),
    UP_WEST  ( 7 , 6 , "up_west"  , 4 ),

    // Placed against the side of a block. Faces away from the side.
    SIDE_NORTH (  8 , 11 , "side_north" ,  9 ),
    SIDE_EAST  (  9 ,  8 , "side_east"  , 10 ),
    SIDE_SOUTH ( 10 ,  9 , "side_south" , 11 ),
    SIDE_WEST  ( 11 , 10 , "side_west"  ,  8 ),

    // Placed against the ceiling. Faces down and in the direction the placer is facing.
    DOWN_NORTH ( 12 , 15 , "down_north" , 13 ),
    DOWN_EAST  ( 13 , 12 , "down_east"  , 14 ),
    DOWN_SOUTH ( 14 , 13 , "down_south" , 15 ),
    DOWN_WEST  ( 15 , 14 , "down_west"  , 12 );

    public static final EnumGeoOrientation[] VALUES = new EnumGeoOrientation[ 16 ];

    public final int meta;
    public final int leftRotIndex;
    public final String name;
    public final int rightRotIndex;

    EnumGeoOrientation( int meta , int leftRotIndex , String name , int rightRotIndex )
    {
        this.meta = meta;
        this.leftRotIndex = leftRotIndex;
        this.name = name;
        this.rightRotIndex = rightRotIndex;
    }

    public int getIndex()
    {
        return this.meta;
    }

    public EnumGeoOrientation mirror( Mirror mirror )
    {
        return mirror == Mirror.NONE ? this : rotate( Rotation.CLOCKWISE_180 );
    }

    @Nullable
    public EnumGeoOrientation rotate( @Nonnull EnumFacing facing )
    {
        if( this == NATURAL )
            return null;

        switch( facing )
        {
            case UP:
                return rotate( Rotation.CLOCKWISE_90 );
            case DOWN:
                return rotate( Rotation.COUNTERCLOCKWISE_90 );
            case NORTH:
            {
                switch( this )
                {
                    case SIDE_NORTH:
                    case SIDE_SOUTH:
                        return null;
                    case UP_EAST:
                    case UP_SOUTH:
                    case UP_NORTH:
                        return UP_WEST;
                    case UP_WEST:
                        return SIDE_WEST;
                    case SIDE_WEST:
                        return DOWN_WEST;
                    case DOWN_WEST:
                    case DOWN_SOUTH:
                    case DOWN_NORTH:
                        return DOWN_EAST;
                    case DOWN_EAST:
                        return SIDE_EAST;
                    case SIDE_EAST:
                        return UP_EAST;
                }
            }
            case EAST:
            {
                switch( this )
                {
                    case SIDE_EAST:
                    case SIDE_WEST:
                        return null;
                    case UP_SOUTH:
                    case UP_WEST:
                    case UP_EAST:
                        return UP_NORTH;
                    case UP_NORTH:
                        return SIDE_NORTH;
                    case SIDE_NORTH:
                        return DOWN_NORTH;
                    case DOWN_NORTH:
                    case DOWN_WEST:
                    case DOWN_EAST:
                        return DOWN_SOUTH;
                    case DOWN_SOUTH:
                        return SIDE_SOUTH;
                    case SIDE_SOUTH:
                        return UP_SOUTH;
                }
            }
            case SOUTH:
            {
                switch( this )
                {
                    case SIDE_NORTH:
                    case SIDE_SOUTH:
                        return null;
                    case UP_WEST:
                    case UP_NORTH:
                    case UP_SOUTH:
                        return UP_EAST;
                    case UP_EAST:
                        return SIDE_EAST;
                    case SIDE_EAST:
                        return DOWN_EAST;
                    case DOWN_EAST:
                    case DOWN_NORTH:
                    case DOWN_SOUTH:
                        return DOWN_WEST;
                    case DOWN_WEST:
                        return SIDE_WEST;
                    case SIDE_WEST:
                        return UP_WEST;
                }
            }
            case WEST:
            {
                switch( this )
                {
                    case SIDE_EAST:
                    case SIDE_WEST:
                        return null;
                    case UP_NORTH:
                    case UP_EAST:
                    case UP_WEST:
                        return UP_SOUTH;
                    case UP_SOUTH:
                        return SIDE_SOUTH;
                    case SIDE_SOUTH:
                        return DOWN_SOUTH;
                    case DOWN_SOUTH:
                    case DOWN_EAST:
                    case DOWN_WEST:
                        return DOWN_NORTH;
                    case DOWN_NORTH:
                        return SIDE_NORTH;
                    case SIDE_NORTH:
                        return UP_NORTH;
                }
            }
        }

        return null;
    }

    public EnumGeoOrientation rotate( Rotation rot )
    {
        switch( rot )
        {
            case CLOCKWISE_90:
                return VALUES[ this.rightRotIndex ];
            case CLOCKWISE_180:
                return VALUES[ VALUES[ this.rightRotIndex ].rightRotIndex ];
            case COUNTERCLOCKWISE_90:
                return VALUES[ this.leftRotIndex ];
            default:
                return this;
        }
    }

    // IStringSerializable overrides

    @Override
    public String getName()
    {
        return this.name;
    }

    // Enum overrides

    @Override
    public String toString()
    {
        return this.name;
    }

    // Statics

    static
    {
        for( EnumGeoOrientation orientation : values() )
            VALUES[ orientation.meta ] = orientation;
    }

    public static EnumGeoOrientation placedAgainst( EnumFacing blockSide , EnumFacing placerFacing )
    {
        switch( blockSide )
        {
            case UP:
            {
                switch( placerFacing )
                {
                    case NORTH:
                        return UP_NORTH;
                    case SOUTH:
                        return UP_SOUTH;
                    case EAST:
                        return UP_EAST;
                    case WEST:
                        return UP_WEST;
                }
            }
            case DOWN:
            {
                switch( placerFacing )
                {
                    case NORTH:
                        return DOWN_NORTH;
                    case SOUTH:
                        return DOWN_SOUTH;
                    case EAST:
                        return DOWN_EAST;
                    case WEST:
                        return DOWN_WEST;
                }
            }
            case NORTH:
                return SIDE_NORTH;
            case EAST:
                return SIDE_EAST;
            case SOUTH:
                return SIDE_SOUTH;
            case WEST:
                return SIDE_WEST;
        }

        return NATURAL;
    }
}
