package com.riintouge.strata.block.brick;

import com.riintouge.strata.block.base.BlockBase;
import com.riintouge.strata.property.IMetaPropertyProvider;
import com.riintouge.strata.property.PropertyWeakStone;
import com.riintouge.strata.property.IPropertyEnumProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;

public class WeakBrickBlock extends BlockBase implements IMetaPropertyProvider
{
    public static final WeakBrickBlock INSTANCE = new WeakBrickBlock();
    public static final String RegistryName = "strata:weak_brick";
    public static final String UnlocalizedName = "strata:weak_brick";

    public WeakBrickBlock()
    {
        super( Material.ROCK );

        setDefaultState( blockState.getBaseState().withProperty( PropertyWeakStone.PROPERTY , PropertyWeakStone.DEFAULT ) );
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
        return getDefaultState().withProperty( PropertyWeakStone.PROPERTY , PropertyWeakStone.types()[ meta ] );
    }

    // IMetaPropertyProvider overrides

    @Override
    public IPropertyEnumProvider getMetaPropertyProvider()
    {
        return PropertyWeakStone.INSTANCE;
    }
}
