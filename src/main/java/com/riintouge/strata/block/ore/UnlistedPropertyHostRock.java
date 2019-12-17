package com.riintouge.strata.block.ore;

import com.riintouge.strata.block.GenericHostRegistry;
import com.riintouge.strata.block.MetaResourceLocation;
import com.riintouge.strata.util.StateUtil;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;

public class UnlistedPropertyHostRock implements IUnlistedProperty< MetaResourceLocation >
{
    public static UnlistedPropertyHostRock PROPERTY = new UnlistedPropertyHostRock();
    // TODO: Default to null and handle appropriately
    public static MetaResourceLocation DEFAULT = new MetaResourceLocation( new ResourceLocation( "minecraft" , "stone" ) , 0 );

    // IUnlistedProperty overrides

    @Override
    public String getName()
    {
        return "UnlistedPropertyHostRock";
    }

    @Override
    public boolean isValid( MetaResourceLocation value )
    {
        return true;
    }

    @Override
    public Class< MetaResourceLocation > getType()
    {
        return MetaResourceLocation.class;
    }

    @Override
    public String valueToString( MetaResourceLocation value )
    {
        return value.toString();
    }

    // Statics

    public static MetaResourceLocation findHost( IBlockAccess worldIn , BlockPos pos )
    {
        MetaResourceLocation bestHost = DEFAULT;

        for( EnumFacing facing : EnumFacing.VALUES )
        {
            // Prefer true hosts over ores
            BlockPos adjPos = pos.offset( facing );
            IBlockState adjState = worldIn.getBlockState( adjPos );
            Block adjBlock = adjState.getBlock();
            ResourceLocation adjRegistryName = adjBlock.getRegistryName();
            int adjMeta = adjBlock.getMetaFromState( adjState );
            // TODO: Prioritize matching materials so clay ores try to take clay hosts
            MetaResourceLocation possibleHost = new MetaResourceLocation( adjRegistryName , adjMeta );
            if( !possibleHost.equals( DEFAULT ) && GenericHostRegistry.INSTANCE.find( adjRegistryName , adjMeta ) != null )
                return possibleHost;

            if( !( adjState instanceof IExtendedBlockState ) )
                continue;

            MetaResourceLocation adjHost = StateUtil.getValue( adjState , worldIn , adjPos , UnlistedPropertyHostRock.PROPERTY , DEFAULT );
            if( !adjHost.equals( DEFAULT ) )
                bestHost = adjHost;
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
