package com.riintouge.strata.item;

import com.riintouge.strata.property.IMetaPropertyProvider;
import net.minecraft.block.Block;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class GenericItemBlock< T extends Block & IMetaPropertyProvider > extends ItemBlock
{
    private IMetaPropertyProvider metaPropertyProvider;

    public GenericItemBlock( T hostBlock )
    {
        super( hostBlock );

        metaPropertyProvider = hostBlock;

        NonNullList<ItemStack> subBlocks = NonNullList.create();
        block.getSubBlocks( CreativeTabs.SEARCH , subBlocks );
        setHasSubtypes( subBlocks.size() > 1 );
        setRegistryName( block.getRegistryName().toString() );
        setUnlocalizedName( block.getRegistryName().toString() );
        setCreativeTab( CreativeTabs.MISC );
        addPropertyOverride( new ResourceLocation( "meta" ) , META_GETTER );
    }

    // ItemBlock overrides

    @Override
    public int getMetadata( int damage )
    {
        return damage;
    }

    @Override
    public String getUnlocalizedName( ItemStack stack )
    {
        IBlockState blockState = this.block.getStateFromMeta( stack.getMetadata() );
        PropertyEnum metaProperty = metaPropertyProvider.getMetaPropertyProvider().property();
        IStringSerializable metaEnum = (IStringSerializable)blockState.getValue( metaProperty );

        String fixedBlockUnlocalizedName = this.block.getUnlocalizedName().replaceAll( "tile." , "" );
        return fixedBlockUnlocalizedName + "_" + metaEnum.getName();
    }

    // Statics

    private static final IItemPropertyGetter META_GETTER = new IItemPropertyGetter()
    {
        @Override
        public float apply( ItemStack stack , World worldIn , EntityLivingBase entityIn )
        {
            return stack.getMetadata();
        }
    };
}
