package com.riintouge.strata.block.geo;

import com.riintouge.strata.block.ParticleHelper;
import com.riintouge.strata.gui.StrataCreativeTabs;
import com.riintouge.strata.util.FlagUtil;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;
import java.util.function.Supplier;

public class GeoBlockSlab extends BlockSlab
{
    protected static final int TOP_META_BIT = 0x8;
    protected static final PropertyEnum< GeoBlockSlab.Variant > VARIANT = PropertyEnum.create( "variant" , GeoBlockSlab.Variant.class );

    protected IGeoTileInfo tileInfo;
    protected final GeoBlockSlab singleSlab;

    // Mandatory to fulfill the abstract class but useless for our purposes
    public enum Variant implements IStringSerializable
    {
        DEFAULT;

        public String getName()
        {
            return "default";
        }
    }

    public GeoBlockSlab( IGeoTileInfo tileInfo )
    {
        this( tileInfo , null );
    }

    protected GeoBlockSlab( IGeoTileInfo tileInfo , GeoBlockSlab singleSlab )
    {
        super( Material.ROCK , MapColor.STONE );
        this.tileInfo = tileInfo;
        this.singleSlab = singleSlab != null ? singleSlab : this;
        // This is what Forge does for BlockSlab in Block.registerBlocks()
        this.useNeighborBrightness = true;

        ResourceLocation registryName = tileInfo.registryName();
        setRegistryName( registryName );
        setUnlocalizedName( registryName.toString() );
        setCreativeTab( StrataCreativeTabs.BUILDING_BLOCK_TAB );

        IBlockState blockState = this.blockState.getBaseState();
        if( !isDouble() )
            blockState = blockState.withProperty( HALF , BlockSlab.EnumBlockHalf.BOTTOM );
        setDefaultState( blockState.withProperty( VARIANT , GeoBlockSlab.Variant.DEFAULT ) );

        setHarvestLevel( tileInfo.harvestTool() , tileInfo.harvestLevel() );
        setSoundType( tileInfo.soundType() );
        setHardness( tileInfo.hardness() );
        setResistance( tileInfo.explosionResistance() );
        Float slipperiness = tileInfo.slipperiness();
        if( slipperiness != null )
            setDefaultSlipperiness( slipperiness );
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
    @SideOnly( Side.CLIENT )
    public boolean addDestroyEffects( World world , BlockPos pos , ParticleManager manager )
    {
        Supplier< TextureAtlasSprite > textureGetter = () -> tileInfo.modelTextureMap().getTexture( EnumFacing.VALUES[ RANDOM.nextInt( 6 ) ] );
        ParticleHelper.addDestroyEffects( world , pos , manager , textureGetter );

        return true;
    }

    @Override
    @SideOnly( Side.CLIENT )
    public boolean addHitEffects( IBlockState state , World worldObj , RayTraceResult target , ParticleManager manager )
    {
        TextureAtlasSprite texture = tileInfo.modelTextureMap().getTexture( target.sideHit );
        ParticleHelper.createHitParticle( state , worldObj , target , manager , RANDOM , texture );

        return true;
    }

    @Override
    @SideOnly( Side.CLIENT )
    public void addInformation( ItemStack stack , @Nullable World player , List< String > tooltip , ITooltipFlag advanced )
    {
        super.addInformation( stack , player , tooltip , advanced );

        List< String > tooltipLines = tileInfo.localizedTooltip();
        if( tooltipLines != null )
            tooltip.addAll( tooltipLines );
    }

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

    @Deprecated
    @Override
    public int getLightValue( IBlockState state )
    {
        return tileInfo.lightLevel();
    }

    @Override
    public int getLightValue( IBlockState state , IBlockAccess world , BlockPos pos )
    {
        return tileInfo.lightLevel();
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
            : blockState.withProperty( HALF , FlagUtil.check( meta , TOP_META_BIT ) ? BlockSlab.EnumBlockHalf.TOP : BlockSlab.EnumBlockHalf.BOTTOM );
    }
}
