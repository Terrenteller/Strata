package com.riintouge.strata.property;

import com.riintouge.strata.GenericStoneRegistry;
import com.riintouge.strata.block.StateUtil;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;

public class UnlistedPropertyHostRock implements IUnlistedProperty< String >
{
    public static UnlistedPropertyHostRock PROPERTY = new UnlistedPropertyHostRock();
    public static String DEFAULT = "";

    @Override
    public String getName()
    {
        return "UnlistedPropertyHostRock";
    }

    @Override
    public boolean isValid( String value )
    {
        return true;
    }

    @Override
    public Class< String > getType()
    {
        return String.class;
    }

    @Override
    public String valueToString( String value )
    {
        return value;
    }

    public static String determineHostAdjacent( IBlockAccess worldIn , BlockPos pos )
    {
        String bestHost = DEFAULT;

        for( EnumFacing facing : EnumFacing.VALUES )
        {
            BlockPos adjPos = pos.offset( facing );
            IBlockState adjState = worldIn.getBlockState( adjPos );

            // TODO: Add support for vanilla blocks by prioritizing a lookup over block state.
            // What about netherrack or clay? Or gravel?
            // If ores "generate" in FallingBlockBase they should break when they "fall"

            String stoneName = adjState.getBlock().getRegistryName().getResourcePath();
            if( GenericStoneRegistry.INSTANCE.contains( stoneName ) )
                return stoneName;

            if( !( adjState instanceof IExtendedBlockState ) )
                continue;

            String adjHost = StateUtil.getValue( adjState , worldIn , adjPos , UnlistedPropertyHostRock.PROPERTY , DEFAULT );
            if( !adjHost.equalsIgnoreCase( DEFAULT ) )
                return adjHost;

            //if( adjState.getBlock() instanceof IPropertyHostProvider )
            //{
            //    System.out.println( "is IPropertyHostProvider" );
            //    IPropertyHostProvider hostProvider = (IPropertyHostProvider)adjState.getBlock();
            //    PropertyEnum propertyEnum = hostProvider.getHostProvidingProperty();
            //    IMetaEnum hostValue = (IMetaEnum)adjState.getProperties().get( propertyEnum );
            //    adjHost = hostValue.getName();
            //    System.out.println( "value is " + adjHost );
            //    if( !adjHost.equalsIgnoreCase( DEFAULT ) )
            //        return adjHost;
            //}

            // TODO: Vanilla hosts, but prioritize our own stone
        }

        return bestHost;
    }

    /*
    // Further disabled due to more refactor. Left here for very legacy reference.
    // Disabled due to PropertyHostRegistry refactor. Left here for legacy reference.
    // Can probably be removed. Might be useful for a single shot upon load to reduce the burden of polling.
    static PropertyHostEnum determineHostRecursive( IBlockAccess worldIn , BlockPos pos )
    {
        Vector<EnumFacing> forbiddenDirections = new Vector<>();
        return determineHostRecursive( worldIn , pos , forbiddenDirections );
    }

    static PropertyHostEnum determineHostRecursive( IBlockAccess worldIn , BlockPos pos , Vector<EnumFacing> forbiddenDirections )
    {
        PropertyHostEnum hostValue = PropertyHostEnum.UNDEFINED;

        for( EnumFacing facing : EnumFacing.VALUES )
        {
            if( forbiddenDirections.contains( facing ) )
                continue;

            BlockPos adjPos = pos.offset( facing );
            IBlockState adjBlockState = worldIn.getBlockState( adjPos );

            IMetaEnum weakStoneBlockMeta = (IMetaEnum)adjBlockState
                .getProperties()
                .getOrDefault( IWeakStoneBlockMeta.PROP_META , null );
            if( weakStoneBlockMeta != null )
            {
                int index = PropertyHostEnum.WEAK_STONE_OFFSET + weakStoneBlockMeta.getMeta();
                return PropertyHostEnum.values()[ index ];
            }

            IMetaEnum mediumStoneBlockMeta = (IMetaEnum)adjBlockState
                .getProperties()
                .getOrDefault( IMediumStoneBlockMeta.PROP_META , null );
            if( mediumStoneBlockMeta != null )
            {
                int index = PropertyHostEnum.MEDIUM_STONE_OFFSET + mediumStoneBlockMeta.getMeta();
                return PropertyHostEnum.values()[ index ];
            }

            IMetaEnum strongStoneBlockMeta = (IMetaEnum)adjBlockState
                .getProperties()
                .getOrDefault( IStrongStoneBlockMeta.PROP_META , null );
            if( strongStoneBlockMeta != null )
            {
                int index = PropertyHostEnum.STRONG_STONE_OFFSET + strongStoneBlockMeta.getMeta();
                return PropertyHostEnum.values()[ index ];
            }

            IMetaEnum veryStrongStoneBlockMeta = (IMetaEnum)adjBlockState
                .getProperties()
                .getOrDefault( IVeryStrongStoneBlockMeta.PROP_META , null );
            if( veryStrongStoneBlockMeta != null )
            {
                int index = PropertyHostEnum.VERY_STRONG_STONE_OFFSET + veryStrongStoneBlockMeta.getMeta();
                return PropertyHostEnum.values()[ index ];
            }

            // Recurse through adjacent ores looking for a real host. This is the expensive case
            // FIXME: This could be heavily optimized. This quick and dirty algorithm can hit blocks multiple times.
            PropertyHostEnum adjHostProp = (PropertyHostEnum)adjBlockState
                .getProperties()
                .getOrDefault( PROPERTY_HOST , null );
            if( adjHostProp != null )
            {
                if( adjHostProp != PropertyHostEnum.UNDEFINED )
                    return PropertyHostEnum.values()[ adjHostProp.getMeta() ];

                EnumFacing oppositeFacing = facing.getOpposite();
                if( !forbiddenDirections.contains( oppositeFacing ) )
                    forbiddenDirections.add( oppositeFacing );

                PropertyHostEnum host = determineHostRecursive(
                    worldIn,
                    pos.offset( facing ),
                    forbiddenDirections );
                if( host != PropertyHostEnum.UNDEFINED )
                    return PropertyHostEnum.values()[ host.getMeta() ];

                forbiddenDirections.remove( oppositeFacing );
            }
        }

        return PropertyHostEnum.UNDEFINED;
    }
    */
}
