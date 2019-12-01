package com.riintouge.strata.block.brick;

import com.riintouge.strata.property.IMetaPropertyProvider;
import com.riintouge.strata.block.base.BlockBase;
import com.riintouge.strata.property.PropertyMediumStone;
import com.riintouge.strata.property.IPropertyEnumProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;

public class MediumBrickBlock extends BlockBase implements IMetaPropertyProvider
{
    public static final MediumBrickBlock INSTANCE = new MediumBrickBlock();
    public static final String RegistryName = "strata:medium_brick";
    public static final String UnlocalizedName = "strata:medium_brick";

    public MediumBrickBlock()
    {
        super( Material.ROCK );

        setDefaultState( blockState.getBaseState().withProperty( PropertyMediumStone.PROPERTY , PropertyMediumStone.DEFAULT ) );
        setHardness( 3f );
        setHarvestLevel( "pickaxe" , 0 );
        setRegistryName( RegistryName );
        setResistance( 5f );
        setUnlocalizedName( UnlocalizedName );
    }

    // Block overrides

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
