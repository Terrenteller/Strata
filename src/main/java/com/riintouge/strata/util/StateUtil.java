package com.riintouge.strata.util;

import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;

import java.util.Optional;

public class StateUtil
{
    public static IExtendedBlockState getCompleteBlockState( IBlockState state , IBlockAccess blockAccess , BlockPos pos )
    {
        return state instanceof IExtendedBlockState ? (IExtendedBlockState)state.getBlock().getExtendedState( state , blockAccess , pos ) : null;
    }

    public static < T extends Comparable< T > >
    T getValue( IBlockState blockState , IProperty< T > property )
    {
        if( blockState == null || property == null )
            return null;

        return (T)blockState.getProperties().get( property );
    }

    public static < T > T getValue( IBlockState blockState , PropertyEnum< ? > property , T defaultValue )
    {
        if( blockState == null || property == null )
            return defaultValue;

        T optionalValue = (T)blockState.getProperties().get( property );
        return optionalValue != null ? optionalValue : defaultValue;
    }

    public static < T > T getValue( IBlockState blockState , IBlockAccess blockAccess , BlockPos pos , IUnlistedProperty< T > property )
    {
        if( blockState instanceof IExtendedBlockState && property != null )
        {
            IExtendedBlockState extendedState = (IExtendedBlockState)blockState.getBlock().getExtendedState( blockState , blockAccess , pos );
            Optional< T > optionalValue = (Optional< T >)extendedState.getUnlistedProperties().get( property );

            return optionalValue.orElse( null );
        }

        return null;
    }

    public static < T > T getValue( IBlockState blockState , IUnlistedProperty< T > property )
    {
        return getValue( blockState , property , null );
    }

    public static < T > T getValue( IBlockState blockState , IUnlistedProperty< T > property , T defaultValue )
    {
        if( blockState instanceof IExtendedBlockState && property != null )
        {
            IExtendedBlockState extendedState = (IExtendedBlockState)blockState;
            Optional< T > optionalValue = (Optional< T >)extendedState.getUnlistedProperties().get( property );

            if( optionalValue.isPresent() )
                return optionalValue.get();
        }

        return defaultValue;
    }

    public static < T > T getValue( IBlockState blockState , IBlockAccess blockAccess , BlockPos pos , IUnlistedProperty< T > property , T defaultValue )
    {
        if( blockState instanceof IExtendedBlockState && property != null )
        {
            IExtendedBlockState extendedState = (IExtendedBlockState)blockState.getBlock().getExtendedState( blockState , blockAccess , pos );
            Optional< T > optionalValue = (Optional< T >)extendedState.getUnlistedProperties().get( property );

            if( optionalValue.isPresent() )
                return optionalValue.get();
        }

        return defaultValue;
    }

    public static < T extends Comparable< T > , V extends T >
    IBlockState withValue( IBlockState state , IProperty< T > property , V value )
    {
        return state.withProperty( property , value );
    }

    public static < T > IBlockState withValue( IBlockState state , IUnlistedProperty< T > unlistedProperty , T value )
    {
        if( !( state instanceof IExtendedBlockState ) )
            return state;

        IExtendedBlockState extendedState = (IExtendedBlockState)state;
        return extendedState.withProperty( unlistedProperty , value );
    }
}
