package com.riintouge.strata.block.ore;

import com.riintouge.strata.block.base.BlockFallingBase;
import com.riintouge.strata.property.IMetaPropertyProvider;
import com.riintouge.strata.property.PropertyWeakSandOre;
import com.riintouge.strata.property.IPropertyEnumProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;

public class WeakSandOreBlock extends BlockFallingBase implements IMetaPropertyProvider
{
    public static final WeakSandOreBlock INSTANCE = new WeakSandOreBlock();
    public static final String RegistryName = "strata:weak_sand_ore";
    public static final String UnlocalizedName = "strata:weak_sand_ore";

    public WeakSandOreBlock()
    {
        super( Material.SAND );

        setDefaultState( blockState.getBaseState().withProperty( PropertyWeakSandOre.PROPERTY , PropertyWeakSandOre.DEFAULT ) );
        setHardness( 3f );
        setHarvestLevel( "shovel" , 0 );
        setRegistryName( RegistryName );
        setResistance( 5f );
        setUnlocalizedName( UnlocalizedName );
    }

    // Block overrides

    @Override
    public IBlockState getStateFromMeta( int meta )
    {
        return getDefaultState().withProperty( getMetaPropertyProvider().property() , PropertyWeakSandOre.types()[ meta ] );
    }

    // IMetaPropertyProvider overrides

    @Override
    public IPropertyEnumProvider getMetaPropertyProvider()
    {
        return PropertyWeakSandOre.INSTANCE;
    }
}
