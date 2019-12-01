package com.riintouge.strata.block.ore;

import com.riintouge.strata.block.*;
import com.riintouge.strata.block.base.OreBlockBase;
import com.riintouge.strata.init.Items;
import com.riintouge.strata.property.IMetaPropertyProvider;
import com.riintouge.strata.property.IPropertyEnumProvider;
import com.riintouge.strata.property.PropertyStrongStoneOre;
import com.riintouge.strata.property.UnlistedPropertyHostRock;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.property.IExtendedBlockState;
import org.apache.commons.lang3.tuple.Pair;

public class StrongStoneOreBlock extends OreBlockBase implements IMetaPropertyProvider
{
    public static final StrongStoneOreBlock INSTANCE = new StrongStoneOreBlock();
    public static final String REGISTRY_NAME = "strata:strong_stone_ore";
    public static final String UNLOCALIZED_NAME = REGISTRY_NAME;

    static
    {
        DynamicOreHostManager.INSTANCE.registerOreBlock( REGISTRY_NAME , PropertyStrongStoneOre.PROPERTY );
    }

    public StrongStoneOreBlock()
    {
        super( Material.ROCK );

        setDefaultState( blockState.getBaseState().withProperty( PropertyStrongStoneOre.PROPERTY , PropertyStrongStoneOre.DEFAULT ) );
        setHardness( 3f );
        setHarvestLevel( "pickaxe" , 0 );
        setRegistryName( REGISTRY_NAME );
        setResistance( 5f );
        setUnlocalizedName( UNLOCALIZED_NAME );
    }

    // Block overrides

    @Override
    public void getDrops( NonNullList<ItemStack> drops , IBlockAccess world , BlockPos pos , IBlockState state , int fortune )
    {
        super.getDrops( drops , world , pos , state , fortune );

        IExtendedBlockState extendedState = StateUtil.getCompleteBlockState( state , world , pos );
        if( extendedState == null )
            return;

        String host = StateUtil.getValue( extendedState , UnlistedPropertyHostRock.PROPERTY , UnlistedPropertyHostRock.DEFAULT );
        Pair< Block , Integer > hostBlock = DynamicOreHostManager.INSTANCE.getHostBlock( host );
        if( hostBlock == null )
            return;

        drops.add( new ItemStack( hostBlock.getLeft().getItemDropped( state , RANDOM , fortune ) , 1 , hostBlock.getRight() ) );
        PropertyStrongStoneOre.Type ore = StateUtil.getValue( extendedState , PropertyStrongStoneOre.PROPERTY , PropertyStrongStoneOre.DEFAULT );
        int fortuneBonus = fortune > 0 ? RANDOM.nextInt( fortune + 1 ) : 0;
        drops.add( new ItemStack( Items.strongStoneOreItem , 1 + fortuneBonus , ore.getValue() ) );
    }

    @Override
    public IBlockState getStateFromMeta( int meta )
    {
        return getDefaultState().withProperty( PropertyStrongStoneOre.PROPERTY , PropertyStrongStoneOre.types()[ meta ] );
    }

    // IMetaPropertyProvider overrides

    @Override
    public IPropertyEnumProvider getMetaPropertyProvider()
    {
        return PropertyStrongStoneOre.INSTANCE;
    }
}
