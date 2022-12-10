package com.riintouge.strata.block.ore;

import com.riintouge.strata.Strata;
import com.riintouge.strata.StrataConfig;
import com.riintouge.strata.block.ParticleHelper;
import com.riintouge.strata.item.WeightedDropCollections;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.EnumPushReaction;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.MaterialLogic;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatBase;
import net.minecraft.stats.StatList;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Random;

public class OreSampleBlock extends OreBaseBlock
{
    // Ore sample blocks and their item blocks have a slightly different registry name
    // so the ore items can have the name of the ore without decoration
    public static final String REGISTRY_NAME_SUFFIX = "_sample";
    protected static final double AABB_MIN_X = 3.0 / 16.0;
    protected static final double AABB_MAX_X = 13.0 / 16.0;
    protected static final double AABB_MIN_Z = 3.0 / 16.0;
    protected static final double AABB_MAX_Z = 13.0 / 16.0;
    protected static final AxisAlignedBB AABB = new AxisAlignedBB( AABB_MIN_X , 0 , AABB_MIN_Z , AABB_MAX_X , 4.0 / 16.0 , AABB_MAX_Z );
    // We unfortunately have a very small degree of movement to prevent any part of the model floating mysteriously
    // over a ledge. This would be improved if we could programmatically get the extents of the model.
    protected static final double MAX_OFFSET_FROM_CENTER = 2.0 / 16.0;

    public OreSampleBlock( IOreInfo oreInfo )
    {
        super( oreInfo , new OreSampleMaterial() );

        setRegistryName( Strata.modid + ":" + oreInfo.oreName() + REGISTRY_NAME_SUFFIX );
        IBlockState proxyBlockState = oreInfo.proxyBlockState();
        Block proxyBlock = proxyBlockState != null ? proxyBlockState.getBlock() : null;
        if( proxyBlock != null )
            setUnlocalizedName( proxyBlock.getUnlocalizedName() );
        else
            setUnlocalizedName( Strata.modid + ":" + oreInfo.oreName() );

        setSoundType( SoundType.GROUND );
        setHardness( 0 ); // If we can right click to pick it up instantly we should be able to break it instantly
        setResistance( 0.2f ); // Taken from Geolosys

        setCreativeTab( Strata.ORE_SAMPLE_TAB );
    }

    public boolean canBlockStay( World world , BlockPos pos )
    {
        IBlockState beneathState = world.getBlockState( pos.down() );
        return beneathState.isTopSolid() || beneathState.getBlockFaceShape( world , pos , EnumFacing.UP ) == BlockFaceShape.SOLID;
    }

    // Block overrides

    @Override
    @SideOnly( Side.CLIENT )
    public boolean addDestroyEffects( World world , BlockPos pos , ParticleManager manager )
    {
        ParticleHelper.addDestroyEffects( world , pos , manager , null );

        return true;
    }

    @Override
    public boolean canPlaceBlockAt( World worldIn , BlockPos pos )
    {
        return super.canPlaceBlockAt( worldIn , pos ) && canBlockStay( worldIn , pos );
    }

    @Override
    public boolean canPlaceTorchOnTop( IBlockState state , IBlockAccess world , BlockPos pos )
    {
        return false;
    }

    @Override
    public boolean canRenderInLayer( IBlockState state , BlockRenderLayer layer )
    {
        // FIXME: The break progress overlay has weird transparency on the default model.
        // Only the overlay faces need to render in the translucent layer. Splitting the model into two
        // would also allow the break overlay to be kept off the satellite pebbles in the default model.
        // Note that DAMAGE_MODEL_FACE_COUNT_HACK may be necessary to keep things looking good.
        // However, as long as the block hardness is zero and breaks instantly, all of this is a moot point.
        // The overlay will never be seen.
        return layer == BlockRenderLayer.TRANSLUCENT;
    }

    @Override
    protected boolean canSilkHarvest()
    {
        return false;
    }

    @Override
    public boolean canSilkHarvest( World world , BlockPos pos , IBlockState state , EntityPlayer player )
    {
        return false;
    }

    @Override
    public boolean canSustainPlant( IBlockState state , IBlockAccess world , BlockPos pos , EnumFacing direction , IPlantable plantable )
    {
        return false;
    }

    @Override
    public void dropBlockAsItemWithChance( World worldIn , BlockPos pos , IBlockState state , float chance , int fortune )
    {
        // We don't get particles or sounds when this method is called directly
        // such as by piston, explosion, or flowing liquid
        super.dropBlockAsItemWithChance( worldIn , pos , state , chance , fortune );
    }

    @Override
    public BlockFaceShape getBlockFaceShape( IBlockAccess worldIn , IBlockState state , BlockPos pos , EnumFacing face )
    {
        return BlockFaceShape.UNDEFINED;
    }

    @Override
    public AxisAlignedBB getBoundingBox( IBlockState state , IBlockAccess source , BlockPos pos )
    {
        return AABB;
    }

    @Override
    public void getDrops( NonNullList< ItemStack > drops , IBlockAccess world , BlockPos pos , IBlockState state , int fortune )
    {
        IBlockState proxyBlockState = oreInfo.proxyBlockState();
        Block proxyBlock = proxyBlockState != null ? proxyBlockState.getBlock() : null;
        ItemStack itemToDrop = null;

        if( proxyBlock != null )
        {
            NonNullList< ItemStack > proxyDrops = NonNullList.create();

            // We have no control over the proxy block, so try ten times with Fortune X to get something
            for( int index = 0 ; index < 10 ; index++ )
            {
                proxyBlock.getDrops( proxyDrops , world , pos , proxyBlockState , 10 );

                if( !proxyDrops.isEmpty() )
                {
                    itemToDrop = proxyDrops.get( RANDOM.nextInt( proxyDrops.size() ) );
                    itemToDrop.setCount( 1 );
                    break;
                }
            }
        }
        else
        {
            WeightedDropCollections weightedDropCollections = oreInfo.weightedDropGroups();
            itemToDrop = weightedDropCollections != null ? weightedDropCollections.getSingleRandomDrop( RANDOM ) : null;
        }

        if( itemToDrop == null || itemToDrop.isEmpty() )
            itemToDrop = oreInfo.equivalentItemStack();

        if( itemToDrop == null || itemToDrop.isEmpty() )
        {
            IOreTileSet oreTileSet = OreRegistry.INSTANCE.find( oreInfo.oreName() );
            if( oreTileSet != null )
                drops.add( new ItemStack( oreTileSet.getItem() ) );
        }
        else
            drops.add( itemToDrop );
    }

    @Override
    public ItemStack getItem( World worldIn , BlockPos pos , IBlockState state )
    {
        IOreTileSet oreTileSet = OreRegistry.INSTANCE.find( oreInfo.oreName() );
        return oreTileSet != null ? new ItemStack( oreTileSet.getItem() ) : ItemStack.EMPTY;
    }

    @Override
    public int getLightValue( IBlockState state , IBlockAccess world , BlockPos pos )
    {
        return (int)Math.ceil( super.getLightValue( state , world , pos ) / 4.0f );
    }

    public MapColor getMapColor( IBlockState state , IBlockAccess worldIn , BlockPos pos )
    {
        // TODO: Does it make sense for samples to show up on a map meaningfully if the ore blocks don't?
        return MapColor.AIR;
    }

    @Override
    public EnumPushReaction getMobilityFlag( IBlockState state )
    {
        // Redundant with our custom material, but explicit
        return EnumPushReaction.DESTROY;
    }

    @Override
    public Vec3d getOffset( IBlockState state , IBlockAccess worldIn , BlockPos pos )
    {
        if( !StrataConfig.restrictSampleXYVariation )
            return super.getOffset( state , worldIn , pos );

        long i = MathHelper.getCoordinateRandom( pos.getX() , 0 , pos.getZ() );
        double x = ( ( i >> 16 & 15L ) / 15.0f ) * MAX_OFFSET_FROM_CENTER;
        double z = ( ( i >> 24 & 15L ) / 15.0f ) * MAX_OFFSET_FROM_CENTER;

        return new Vec3d( x , 0.0d , z );
    }

    @Override
    public EnumOffsetType getOffsetType()
    {
        return EnumOffsetType.XZ;
    }

    @Override
    public ItemStack getPickBlock( IBlockState state , RayTraceResult target , World world , BlockPos pos , EntityPlayer player )
    {
        IOreTileSet oreTileSet = OreRegistry.INSTANCE.find( oreInfo.oreName() );
        return oreTileSet != null ? new ItemStack( oreTileSet.getSampleItemBlock() ) : ItemStack.EMPTY;
    }

    @Override
    public boolean isFullBlock( IBlockState state )
    {
        return false;
    }

    @Override
    public boolean isFullCube( IBlockState state )
    {
        return false;
    }

    @Override
    public boolean isOpaqueCube( IBlockState state )
    {
        return false;
    }

    @Override
    public boolean isSideSolid( IBlockState base_state , IBlockAccess world , BlockPos pos , EnumFacing side )
    {
        return false;
    }

    @Override
    public boolean isTopSolid( IBlockState state )
    {
        return false;
    }

    @Override
    public void neighborChanged( IBlockState state , World worldIn , BlockPos pos , Block blockIn , BlockPos fromPos )
    {
        if( !canBlockStay( worldIn , pos ) )
        {
            dropBlockAsItem( worldIn , pos , state , 0 );
            worldIn.setBlockToAir( pos );
        }
    }

    @Override
    public boolean onBlockActivated( World worldIn , BlockPos pos , IBlockState state , EntityPlayer playerIn , EnumHand hand , EnumFacing facing , float hitX , float hitY , float hitZ )
    {
        if( playerIn.isSneaking() )
            return false;

        if( !playerIn.isCreative() )
        {
            if( !worldIn.isRemote )
            {
                NonNullList< ItemStack > drops = NonNullList.create();
                getDrops( drops , worldIn , pos , state , 0 );

                if( drops.size() > 0 )
                {
                    // Because we always play the pick-up/equip sound because we can't run this on the client,
                    // we spawn the drop at the player if unable to put it directly in the player's inventory.
                    // That way, the sound played makes a little more sense.
                    ItemStack itemStack = drops.get( drops.size() - 1 );
                    if( !playerIn.addItemStackToInventory( itemStack ) )
                        spawnAsEntity( worldIn , playerIn.getPosition() , itemStack );
                }
            }

            // Unfortunately, we have to assume something dropped.
            // We can't run the drop code on the client because the item dropped may not be consistent.
            worldIn.playSound(
                playerIn.posX,
                playerIn.posY,
                playerIn.posZ,
                SoundEvents.ITEM_ARMOR_EQUIP_GENERIC,
                playerIn.getSoundCategory(),
                1.0f,
                1.0f,
                false );

            StatBase blockStats = StatList.getBlockStats( this );
            if( blockStats != null )
                playerIn.addStat( blockStats );
            playerIn.addExhaustion( 0.005f ); // Taken from Block.harvestBlock()
        }

        if( !worldIn.isRemote )
            worldIn.destroyBlock( pos , false );

        return true;
    }

    @Override
    public void onFallenUpon( World worldIn , BlockPos pos , Entity entityIn , float fallDistance )
    {
        if( entityIn instanceof EntityLivingBase && fallDistance > 3.0 )
        {
            boolean isPlayer = entityIn instanceof EntityPlayer;
            if( isPlayer && ( (EntityPlayer)entityIn ).isCreative() )
            {
                worldIn.playSound(
                    entityIn.posX,
                    entityIn.posY,
                    entityIn.posZ,
                    SoundType.GROUND.getFallSound(),
                    SoundCategory.BLOCKS,
                    ( SoundType.GROUND.getVolume() + 1.0f ) / 2.0f,
                    SoundType.GROUND.getPitch() * 0.8f,
                    false );

                return;
            }

            if( !worldIn.isRemote && ( isPlayer || net.minecraftforge.event.ForgeEventFactory.getMobGriefingEvent( worldIn , entityIn ) ) )
                worldIn.destroyBlock( pos , true );
        }

        super.onFallenUpon( worldIn , pos , entityIn , fallDistance );
    }

    @Override
    public int quantityDropped( Random random )
    {
        return 1;
    }

    @Override
    @SideOnly( Side.CLIENT )
    public void randomDisplayTick( IBlockState stateIn , World worldIn , BlockPos pos , Random rand )
    {
        // Do not call super to create dust particle
    }

    @Override
    public void updateTick( World worldIn , BlockPos pos , IBlockState state , Random rand )
    {
        // Do not call super to fall
    }

    // Statics

    private static class OreSampleMaterial extends MaterialLogic
    {
        public OreSampleMaterial()
        {
            super( MapColor.STONE );

            setNoPushMobility(); // AKA EnumPushReaction.DESTROY
        }
    }
}
