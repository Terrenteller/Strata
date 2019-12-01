package com.riintouge.strata.block.cobble;

import com.riintouge.strata.block.base.BlockBase;
import com.riintouge.strata.property.IMetaPropertyProvider;
import com.riintouge.strata.property.PropertyVeryStrongStone;
import com.riintouge.strata.property.IPropertyEnumProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;

public class VeryStrongCobbleBlock extends BlockBase implements IMetaPropertyProvider
{
    public static final VeryStrongCobbleBlock INSTANCE = new VeryStrongCobbleBlock();
    public static final String RegistryName = "strata:very_strong_cobble";
    public static final String UnlocalizedName = "strata:very_strong_cobble";

    public VeryStrongCobbleBlock()
    {
        super( Material.ROCK );

        setDefaultState( blockState.getBaseState().withProperty( PropertyVeryStrongStone.PROPERTY , PropertyVeryStrongStone.DEFAULT ) );
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
        return getDefaultState().withProperty( PropertyVeryStrongStone.PROPERTY , PropertyVeryStrongStone.types()[ meta ] );
    }

    // IMetaPropertyProvider overrides

    @Override
    public IPropertyEnumProvider getMetaPropertyProvider()
    {
        return PropertyVeryStrongStone.INSTANCE;
    }
}
