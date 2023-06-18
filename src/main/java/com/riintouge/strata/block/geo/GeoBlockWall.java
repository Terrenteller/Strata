package com.riintouge.strata.block.geo;

import com.riintouge.strata.StrataConfig;
import com.riintouge.strata.block.ParticleHelper;
import com.riintouge.strata.gui.StrataCreativeTabs;
import com.riintouge.strata.util.ReflectionUtil;
import com.riintouge.strata.util.StateUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFenceGate;
import net.minecraft.block.BlockTorch;
import net.minecraft.block.BlockWall;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.util.List;
import java.util.function.Supplier;

public class GeoBlockWall extends BlockWall
{
    public static final PropertyBool TALL = PropertyBool.create( "tall" );
    protected static final ThreadLocal< Boolean > IS_RECURSING_UP = ThreadLocal.withInitial( () -> false );

    protected IGeoTileInfo tileInfo;
    // The logic for this hack MUST respect the default boolean value.
    // A default of true will not be set until after it is too late.
    private boolean createRealBlockState;

    public GeoBlockWall( IGeoTileInfo tileInfo , Block block )
    {
        super( block );
        this.tileInfo = tileInfo;

        RemoveBlockWallVariantFromBlockState();

        ResourceLocation registryName = tileInfo.registryName();
        setCreativeTab( StrataCreativeTabs.BUILDING_BLOCK_TAB );
        setHarvestLevel( tileInfo.harvestTool() , tileInfo.harvestLevel() );
        setHardness( tileInfo.hardness() );
        setRegistryName( registryName );
        setResistance( tileInfo.explosionResistance() );
        setSoundType( tileInfo.soundType() );
        setUnlocalizedName( registryName.toString() );

        Float slipperiness = tileInfo.slipperiness();
        if( slipperiness != null )
            setDefaultSlipperiness( slipperiness );
    }

    private void RemoveBlockWallVariantFromBlockState()
    {
        createRealBlockState = true;

        try
        {
            // Cannot get field by name due to obfuscation
            Field field = ReflectionUtil.findFieldByType( Block.class , BlockStateContainer.class , false );
            field.setAccessible( true );
            field.set( this , createBlockState() );

            setDefaultState( blockState.getBaseState()
                .withProperty( UP , Boolean.FALSE )
                .withProperty( NORTH , Boolean.FALSE )
                .withProperty( EAST , Boolean.FALSE )
                .withProperty( SOUTH , Boolean.FALSE )
                .withProperty( WEST , Boolean.FALSE )
                .withProperty( TALL , Boolean.FALSE ) );
        }
        catch( Exception e )
        {
            throw new RuntimeException( e );
        }
    }

    // BlockWall overrides

    @Override
    public boolean canBeConnectedTo( IBlockAccess world , BlockPos pos , EnumFacing facing )
    {
        BlockPos otherPos = pos.offset( facing );
        IBlockState otherBlockState = world.getBlockState( otherPos );
        Block otherBlock = otherBlockState.getBlock();

        // Filter out visually displeasing wall connections
        if( otherBlock instanceof GeoBlockWall )
        {
            GeoBlockWall otherWall = (GeoBlockWall)otherBlock;
            if( tileInfo.tileSetName().compareTo( otherWall.tileInfo.tileSetName() ) != 0 )
                return false;

            switch( tileInfo.tileType() )
            {
                case COBBLEWALL:
                case COBBLEWALLMOSSY:
                {
                    switch( otherWall.tileInfo.tileType() )
                    {
                        case COBBLEWALL:
                        case COBBLEWALLMOSSY:
                            return true;
                    }

                    break;
                }
                case STONEWALL:
                    return otherWall.tileInfo.tileType() == TileType.STONEWALL;
                case STONEBRICKWALL:
                case STONEBRICKWALLMOSSY:
                {
                    switch( otherWall.tileInfo.tileType() )
                    {
                        case STONEBRICKWALL:
                        case STONEBRICKWALLMOSSY:
                            return true;
                    }

                    break;
                }
            }

            return false;
        }

        // Opposite facing to get the face which faces towards us
        BlockFaceShape otherBlockFaceShape = otherBlockState.getBlockFaceShape( world , otherPos , facing.getOpposite() );
        // Do not connect to BlockFaceShape.MIDDLE_POLE_THICK so we don't connect to other walls
        return ( otherBlockFaceShape == BlockFaceShape.MIDDLE_POLE && otherBlock instanceof BlockFenceGate )
            || ( !isExcepBlockForAttachWithPiston( otherBlock ) && otherBlockFaceShape == BlockFaceShape.SOLID );
    }

    // Block overrides

    @Override
    @SideOnly( Side.CLIENT )
    public boolean addDestroyEffects( World world , BlockPos pos , ParticleManager manager )
    {
        TextureAtlasSprite textures[] = tileInfo.modelTextureMap().getTextures();
        Supplier< TextureAtlasSprite > textureGetter = () -> textures[ RANDOM.nextInt( 6 ) ];
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
    public boolean canPlaceTorchOnTop( IBlockState state , IBlockAccess world , BlockPos pos )
    {
        return true;
    }

    @Override
    protected BlockStateContainer createBlockState()
    {
        // BlockWall stuffs its VARIANT PropertyEnum into the default block state in its constructor.
        // This property does not make sense for Strata. Play along until we can remove it.
        return createRealBlockState
            ? new BlockStateContainer( this , UP , NORTH , EAST , SOUTH , WEST , TALL )
            : new BlockStateContainer( this , UP , NORTH , EAST , SOUTH , WEST , TALL , VARIANT );
    }

    @Override
    public int damageDropped( IBlockState state )
    {
        return 0;
    }

    @Override
    public IBlockState getActualState( IBlockState state , IBlockAccess worldIn , BlockPos pos )
    {
        boolean north = canBeConnectedTo( worldIn , pos , EnumFacing.NORTH );
        boolean east = canBeConnectedTo( worldIn , pos , EnumFacing.EAST );
        boolean south = canBeConnectedTo( worldIn , pos , EnumFacing.SOUTH );
        boolean west = canBeConnectedTo( worldIn , pos , EnumFacing.WEST );
        // up is true if the wall is not a straight section
        boolean up = !( ( north && !east && south && !west ) || ( !north && east && !south && west ) );
        boolean tall = false;

        if( !StrataConfig.useModernWallStyle )
        {
            up |= !worldIn.isAirBlock( pos.up() )
                || worldIn.getBlockState( pos.offset( EnumFacing.DOWN ) ).getBlock() instanceof BlockWall;
        }
        else if( !( up && IS_RECURSING_UP.get() ) )
        {
            BlockPos abovePos = pos.offset( EnumFacing.UP );
            IBlockState aboveBlockState = worldIn.getBlockState( abovePos );
            Block aboveBlock = aboveBlockState.getBlock();

            switch( aboveBlockState.getBlockFaceShape( worldIn , abovePos , EnumFacing.DOWN ) )
            {
                case BOWL:
                    break;
                case SOLID:
                    tall = true;
                    break;
                case UNDEFINED:
                    up |= aboveBlock instanceof BlockTorch; // Torch bottoms aren't CENTER_SMALL for some reason
                    break;
                case CENTER_BIG:
                    if( aboveBlock instanceof BlockWall )
                    {
                        // Recurse upwards
                        boolean wasRecursingUp = IS_RECURSING_UP.get();
                        if( !wasRecursingUp )
                            IS_RECURSING_UP.set( true );
                        IBlockState aboveBlockActualState = aboveBlock.getActualState( aboveBlockState , worldIn , abovePos );
                        if( !wasRecursingUp )
                            IS_RECURSING_UP.remove();

                        boolean aboveNorth = StateUtil.getValue( aboveBlockActualState , NORTH , false );
                        boolean aboveEast = StateUtil.getValue( aboveBlockActualState , EAST , false );
                        boolean aboveSouth = StateUtil.getValue( aboveBlockActualState , SOUTH , false );
                        boolean aboveWest = StateUtil.getValue( aboveBlockActualState , WEST , false );

                        // If the wall above has a post, we do too. Otherwise, we're a post if connections differ.
                        // Stacked, parallel walls will pass these checks and not have a post.
                        up |= StateUtil.getValue( aboveBlockActualState , UP , false )
                            || ( north != aboveNorth ) || ( east != aboveEast ) || ( south != aboveSouth ) || ( west != aboveWest );

                        // If the wall above has any connections which match ours, all directions are considered "tall".
                        // This dampens the model state combinatorial explosion in the "proto_wall" block state.
                        // Visual oddities will occur with certain arrangements, but the overall improvement
                        // is worth the edge cases. This might can be fixed if/when we can load a multipart model
                        // as a dependency. See the note in GeoTileSetRegistry.registerBlocks() for details.
                        tall |= ( north && aboveNorth ) || ( east && aboveEast ) || ( south && aboveSouth ) || ( west && aboveWest );

                        break;
                    }
                default:
                    up = true;
            }
        }

        return state
            .withProperty( UP , up )
            .withProperty( NORTH , north )
            .withProperty( EAST , east )
            .withProperty( SOUTH , south )
            .withProperty( WEST , west )
            .withProperty( TALL , tall );
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
    public String getLocalizedName()
    {
        return I18n.translateToLocal( this.getUnlocalizedName() + ".name" );
    }

    @Override
    public int getMetaFromState( IBlockState state )
    {
        return 0;
    }

    @Override
    public IBlockState getStateFromMeta( int meta )
    {
        return this.getDefaultState();
    }

    @Override
    public void getSubBlocks( CreativeTabs itemIn , NonNullList< ItemStack > items )
    {
        items.add( new ItemStack( this ) );
    }

    @Override
    public void onFallenUpon( World worldIn , BlockPos pos , Entity entityIn , float fallDistance )
    {
        GeoBlock.onFallenUponCommon( this , worldIn , pos , entityIn , fallDistance );

        super.onFallenUpon( worldIn , pos , entityIn , fallDistance );
    }
}
