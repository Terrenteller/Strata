package com.riintouge.strata.block.stone;

import com.riintouge.strata.block.*;
import com.riintouge.strata.block.base.BlockBase;
import com.riintouge.strata.init.Items;
import com.riintouge.strata.property.IMetaPropertyProvider;
import com.riintouge.strata.property.IPropertyEnumProvider;
import com.riintouge.strata.property.PropertyMediumStone;
import com.riintouge.strata.property.UnlistedPropertyHostRock;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.property.IExtendedBlockState;

import java.util.Random;

public class MediumStoneBlock extends BlockBase implements IMetaPropertyProvider
{
    public static final MediumStoneBlock INSTANCE = new MediumStoneBlock();
    public static final String RegistryName = "strata:medium_stone";
    public static final String UnlocalizedName = "strata:medium_stone";

    static
    {
        // Add mapping from host name to block and meta value
        for( PropertyMediumStone.Type type : PropertyMediumStone.types() )
            DynamicOreHostManager.INSTANCE.registerHostBlock( type.getName() , INSTANCE , type.getValue() );
    }

    public MediumStoneBlock()
    {
        super( Material.ROCK );

        setDefaultState( blockState.getBaseState().withProperty( PropertyMediumStone.PROPERTY , PropertyMediumStone.DEFAULT ) );
        setHardness( 3f );
        setHarvestLevel( "pickaxe" , 1 );
        setRegistryName( RegistryName );
        setResistance( 5f );
        setUnlocalizedName( UnlocalizedName );
    }

    // Block overrides

    @Override
    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer.Builder( this )
            .add( PropertyMediumStone.PROPERTY )
            .add( UnlistedPropertyHostRock.PROPERTY )
            .build();
    }

    @Override
    public IBlockState getExtendedState( IBlockState state , IBlockAccess world , BlockPos pos )
    {
        String hostName = StateUtil.getValue( state , PropertyMediumStone.PROPERTY , PropertyMediumStone.DEFAULT ).toString();
        IExtendedBlockState extendedState = (IExtendedBlockState)state;

        return extendedState.withProperty( UnlistedPropertyHostRock.PROPERTY , hostName );
    }

    @Override
    public Item getItemDropped( IBlockState state , Random rand , int fortune )
    {
        return Items.mediumCobble;
    }

    @Override
    public IBlockState getStateFromMeta( int meta )
    {
        return getDefaultState().withProperty( PropertyMediumStone.PROPERTY , PropertyMediumStone.types()[ meta ] );
    }

    // IMetaPropertyProvider overrides

    @Override
    public IPropertyEnumProvider getMetaPropertyProvider()
    {
        return PropertyMediumStone.INSTANCE;
    }
}
