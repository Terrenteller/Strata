package com.riintouge.strata.block.cobble;

import com.riintouge.strata.property.IMetaPropertyProvider;
import com.riintouge.strata.block.base.BlockBase;
import com.riintouge.strata.property.PropertyStrongStone;
import com.riintouge.strata.property.IPropertyEnumProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;

public class StrongCobbleBlock extends BlockBase implements IMetaPropertyProvider
{
    public static final StrongCobbleBlock INSTANCE = new StrongCobbleBlock();
    public static final String RegistryName = "strata:strong_cobble";
    public static final String UnlocalizedName = "strata:strong_cobble";

    public StrongCobbleBlock()
    {
        super( Material.ROCK );

        setDefaultState( blockState.getBaseState().withProperty( PropertyStrongStone.PROPERTY , PropertyStrongStone.DEFAULT ) );
        setHardness( 3f );
        setHarvestLevel( "pickaxe" , 1 );
        setRegistryName( RegistryName );
        setResistance( 5f );
        setUnlocalizedName( UnlocalizedName );
    }

    // Block overrides

    @Override
    public IBlockState getStateFromMeta( int meta )
    {
        return getDefaultState().withProperty( PropertyStrongStone.PROPERTY , PropertyStrongStone.types()[ meta ] );
    }

    // IMetaPropertyProvider overrides

    @Override
    public IPropertyEnumProvider getMetaPropertyProvider()
    {
        return PropertyStrongStone.INSTANCE;
    }
}
