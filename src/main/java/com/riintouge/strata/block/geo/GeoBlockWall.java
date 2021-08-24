package com.riintouge.strata.block.geo;

import com.riintouge.strata.Strata;
import com.riintouge.strata.util.ReflectionUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFenceGate;
import net.minecraft.block.BlockWall;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.IBlockAccess;

import java.lang.reflect.Field;

public class GeoBlockWall extends BlockWall
{
    // The logic for this hack MUST respect the default boolean value.
    // A default of true will not be set until after it is too late.
    private boolean createRealBlockState;
    private IGeoTileInfo info;

    public GeoBlockWall( IGeoTileInfo info , Block block )
    {
        super( block );
        this.info = info;

        RemoveBlockWallVariantFromBlockState();

        ResourceLocation registryName = info.registryName();
        setRegistryName( registryName );
        setUnlocalizedName( registryName.toString() );
        setCreativeTab( Strata.BUILDING_BLOCK_TAB );

        setHarvestLevel( info.harvestTool() , info.harvestLevel() );
        setSoundType( info.soundType() );
        setHardness( info.hardness() );
        setResistance( info.explosionResistance() );
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
            field.setAccessible( false );

            setDefaultState( blockState.getBaseState()
                .withProperty( UP , Boolean.valueOf( false ) )
                .withProperty( NORTH , Boolean.valueOf( false ) )
                .withProperty( EAST , Boolean.valueOf( false ) )
                .withProperty( SOUTH , Boolean.valueOf( false ) )
                .withProperty( WEST , Boolean.valueOf( false ) ) );
        }
        catch( Exception ex )
        {
            throw new RuntimeException( ex );
        }
    }

    // private in BlockWall and required for overriding behaviour
    protected boolean canConnectTo( IBlockAccess world , BlockPos pos , EnumFacing facing )
    {
        // Only connect to relevant types for visual quality
        IBlockState otherBlockState = world.getBlockState( pos );
        Block otherBlock = otherBlockState.getBlock();
        if( otherBlock instanceof GeoBlockWall )
        {
            GeoBlockWall otherWall = (GeoBlockWall)otherBlock;
            if( info.tileSetName().compareTo( otherWall.info.tileSetName() ) != 0 )
                return false;

            switch( info.type() )
            {
                case COBBLEWALL:
                case COBBLEWALLMOSSY:
                {
                    switch( otherWall.info.type() )
                    {
                        case COBBLEWALL:
                        case COBBLEWALLMOSSY:
                            return true;
                    }

                    break;
                }
                case STONEWALL:
                    return otherWall.info.type() == TileType.STONEWALL;
                case STONEBRICKWALL:
                case STONEBRICKWALLMOSSY:
                {
                    switch( otherWall.info.type() )
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

        BlockFaceShape blockFaceShape = otherBlockState.getBlockFaceShape( world , pos , facing );
        // Do not connect to BlockFaceShape.MIDDLE_POLE_THICK so we don't connect to other walls
        return ( blockFaceShape == BlockFaceShape.MIDDLE_POLE && otherBlock instanceof BlockFenceGate )
            || ( !isExcepBlockForAttachWithPiston( otherBlock ) && blockFaceShape == BlockFaceShape.SOLID );
    }

    // private in BlockWall and required for overriding behaviour
    protected boolean canWallConnectTo( IBlockAccess world , BlockPos pos , EnumFacing facing )
    {
        BlockPos otherPos = pos.offset( facing );
        Block otherBlock = world.getBlockState( otherPos ).getBlock();
        // Vanilla ORs instead of ANDs not respecting a mutual connection
        return otherBlock.canBeConnectedTo( world , otherPos , facing.getOpposite() ) && canConnectTo( world , otherPos , facing.getOpposite() );
    }

    // BlockWall overrides

    @Override
    public boolean canBeConnectedTo( IBlockAccess world , BlockPos pos , EnumFacing facing )
    {
        return canConnectTo( world , pos.offset( facing ), facing.getOpposite() );
    }

    // Block overrides

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
            ? new BlockStateContainer( this , UP , NORTH , EAST , WEST , SOUTH )
            : new BlockStateContainer( this , UP , NORTH , EAST , WEST , SOUTH , VARIANT );
    }

    @Override
    public int damageDropped( IBlockState state )
    {
        return 0;
    }

    @Override
    public IBlockState getActualState( IBlockState state , IBlockAccess worldIn , BlockPos pos )
    {
        boolean north = canWallConnectTo( worldIn , pos , EnumFacing.NORTH );
        boolean east = canWallConnectTo( worldIn , pos , EnumFacing.EAST );
        boolean south = canWallConnectTo( worldIn , pos , EnumFacing.SOUTH );
        boolean west = canWallConnectTo( worldIn , pos , EnumFacing.WEST );
        boolean up = !( ( north && !east && south && !west ) || ( !north && east && !south && west ) )
            || !worldIn.isAirBlock( pos.up() )
            || worldIn.getBlockState( pos.offset( EnumFacing.DOWN ) ).getBlock() instanceof BlockWall;

        return state
            .withProperty( UP , Boolean.valueOf( up ) )
            .withProperty( NORTH , Boolean.valueOf( north ) )
            .withProperty( EAST , Boolean.valueOf( east ) )
            .withProperty( SOUTH , Boolean.valueOf( south ) )
            .withProperty( WEST , Boolean.valueOf( west ) );
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
}
