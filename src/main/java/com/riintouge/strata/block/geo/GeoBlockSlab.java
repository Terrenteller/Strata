package com.riintouge.strata.block.geo;

import com.riintouge.strata.Strata;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Random;

public class GeoBlockSlab extends BlockSlab
{
    private static final int TOP_META_BIT = 0x8;
    private static final PropertyEnum< GeoBlockSlab.Variant > VARIANT = PropertyEnum.create( "variant" , GeoBlockSlab.Variant.class );
    private final GeoBlockSlab singleSlab;

    // Mandatory to fulfill the abstract class but useless for our purposes
    public enum Variant implements IStringSerializable
    {
        DEFAULT;

        public String getName()
        {
            return "default";
        }
    }

    public GeoBlockSlab( IGeoTileInfo info )
    {
        this( info , null );
    }

    protected GeoBlockSlab( IGeoTileInfo info , GeoBlockSlab singleSlab )
    {
        super( Material.ROCK , MapColor.STONE );
        this.singleSlab = singleSlab != null ? singleSlab : this;
        // This is what Forge does for BlockSlab in Block.registerBlocks()
        this.useNeighborBrightness = true;

        ResourceLocation registryName = isDouble()
            ? info.type().slabsType().registryName( info.tileSetName() )
            : info.type().slabType().registryName( info.tileSetName() );
        setRegistryName( registryName );
        setUnlocalizedName( registryName.toString() );
        setCreativeTab( Strata.BUILDING_BLOCK_TAB );

        IBlockState blockState = this.blockState.getBaseState();
        if( !isDouble() )
            blockState = blockState.withProperty( HALF , BlockSlab.EnumBlockHalf.BOTTOM );
        setDefaultState( blockState.withProperty( VARIANT , GeoBlockSlab.Variant.DEFAULT ) );

        setHarvestLevel( info.harvestTool() , info.harvestLevel() );
        setSoundType( info.soundType() );
        setHardness( info.hardness() );
        setResistance( info.explosionResistance() );
    }

    // BlockSlab overrides

    @Override
    public String getUnlocalizedName( int meta )
    {
        return super.getUnlocalizedName();
    }

    @Override
    public boolean isDouble()
    {
        // We cannot modify the return value before it is used by createBlockState()
        // from BlockSlab's constructor. For a double slab use GeoBlockSlabs.
        return false;
    }

    @Override
    public IProperty< ? > getVariantProperty()
    {
        return VARIANT;
    }

    @Override
    public Comparable< ? > getTypeForItem( ItemStack stack )
    {
        return Variant.DEFAULT;
    }

    // Block overrides

    @Override
    protected BlockStateContainer createBlockState()
    {
        return isDouble()
            ? new BlockStateContainer( this , new IProperty[] { VARIANT } )
            : new BlockStateContainer( this , new IProperty[] { HALF , VARIANT } );
    }

    @Override
    public ItemStack getItem( World worldIn , BlockPos pos , IBlockState state )
    {
        return new ItemStack( singleSlab );
    }

    @Override
    public Item getItemDropped( IBlockState state , Random rand , int fortune )
    {
        return Item.getItemFromBlock( singleSlab );
    }

    @Override
    public int getMetaFromState( IBlockState state )
    {
        return !isDouble() && state.getValue( HALF ) == BlockSlab.EnumBlockHalf.TOP ? TOP_META_BIT : 0;
    }

    @Override
    public IBlockState getStateFromMeta( int meta )
    {
        IBlockState blockState = this.getDefaultState().withProperty( VARIANT , Variant.DEFAULT );
        return isDouble()
            ? blockState
            : blockState.withProperty( HALF , ( meta & TOP_META_BIT ) != 0 ? BlockSlab.EnumBlockHalf.TOP : BlockSlab.EnumBlockHalf.BOTTOM );
    }
}