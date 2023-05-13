package com.riintouge.strata.block.geo;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Random;

public enum EnumGeoOrientation implements IStringSerializable
{
    // Placed by natural means. Does not have an inherent orientation.
    NATURAL ( 0 , 0 , "natural" , 0 ),

    UP_NORTH ( 4 , 7 , "up_north" , 5 ),
    UP_EAST  ( 5 , 4 , "up_east"  , 6 ),
    UP_SOUTH ( 6 , 5 , "up_south" , 7 ),
    UP_WEST  ( 7 , 6 , "up_west"  , 4 ),

    SIDE_NORTH (  8 , 11 , "side_north" ,  9 ),
    SIDE_EAST  (  9 ,  8 , "side_east"  , 10 ),
    SIDE_SOUTH ( 10 ,  9 , "side_south" , 11 ),
    SIDE_WEST  ( 11 , 10 , "side_west"  ,  8 ),

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

    public static EnumGeoOrientation placedAgainst( @Nonnull EnumFacing blockSide , @Nullable EnumFacing placerHorizontalFacing )
    {
        EnumFacing adjustedPlacerHorizontalFacing = placerHorizontalFacing;
        if( placerHorizontalFacing == null
            || placerHorizontalFacing.getHorizontalIndex() < 0
            || placerHorizontalFacing.getHorizontalIndex() >= EnumFacing.HORIZONTALS.length )
        {
            // The placer doesn't know about its horizontal facing or it is not applicable (straight up or down).
            // Unfortunately, we do care. Make something up because we can't return NATURAL.
            adjustedPlacerHorizontalFacing = EnumFacing.HORIZONTALS[ new Random().nextInt( 4 ) ];
        }

        if( blockSide == EnumFacing.UP )
        {
            switch( adjustedPlacerHorizontalFacing )
            {
                case NORTH:
                    return UP_SOUTH;
                case SOUTH:
                    return UP_NORTH;
                case EAST:
                    return UP_WEST;
                case WEST:
                    return UP_EAST;
            }
        }
        else if( blockSide == EnumFacing.DOWN )
        {
            switch( adjustedPlacerHorizontalFacing )
            {
                case NORTH:
                    return DOWN_SOUTH;
                case SOUTH:
                    return DOWN_NORTH;
                case EAST:
                    return DOWN_WEST;
                case WEST:
                    return DOWN_EAST;
            }
        }
        else
        {
            switch( adjustedPlacerHorizontalFacing )
            {
                case NORTH:
                    return SIDE_SOUTH;
                case EAST:
                    return SIDE_WEST;
                case SOUTH:
                    return SIDE_NORTH;
                case WEST:
                    return SIDE_EAST;
            }
        }

        // Anything besides worldgen adding the block is not considered natural so we can't return NATURAL.
        // We shouldn't be able to get here anyway.
        throw new IllegalStateException(
            String.format(
                "No appropriate geo orientation for block placed facing \"%s\" against side \"%s\"!",
                blockSide.toString(),
                placerHorizontalFacing.toString() ) );
    }
}
