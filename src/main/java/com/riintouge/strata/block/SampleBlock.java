package com.riintouge.strata.block;

import com.riintouge.strata.StrataConfig;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFalling;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.EnumPushReaction;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.MaterialLogic;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

public abstract class SampleBlock extends BlockFalling
{
    // Sample blocks and their item blocks have a slightly different registry name
    // so fragments can have the undecorated name
    public static final String REGISTRY_NAME_SUFFIX = "_sample";
    protected static final double AABB_MIN_X = 3.0 / 16.0;
    protected static final double AABB_MAX_X = 13.0 / 16.0;
    protected static final double AABB_MIN_Z = 3.0 / 16.0;
    protected static final double AABB_MAX_Z = 13.0 / 16.0;
    protected static final AxisAlignedBB AABB = new AxisAlignedBB( AABB_MIN_X , 0 , AABB_MIN_Z , AABB_MAX_X , 4.0 / 16.0 , AABB_MAX_Z );
    // Unfortunately, we have a very small degree of movement to prevent any part of the model floating mysteriously
    // over a ledge. This would be improved if we could programmatically get the extents of the model.
    protected static final double MAX_OFFSET_FROM_CENTER = 2.0 / 16.0;

    protected IBlockState originalBlockState;

    public SampleBlock( IBlockState originalBlockState )
    {
        super( new SampleMaterial() );
        this.originalBlockState = originalBlockState;

        setHardness( 0 ); // If we can right click to pick it up instantly we should be able to break it instantly
        setResistance( 0.2f ); // Taken from Geolosys
    }

    protected boolean canBlockStay( World world , BlockPos pos )
    {
        IBlockState beneathState = world.getBlockState( pos.down() );
        return beneathState.isTopSolid() || beneathState.getBlockFaceShape( world , pos , EnumFacing.UP ) == BlockFaceShape.SOLID;
    }

    // Block overrides

    @Override
    @SideOnly( Side.CLIENT )
    public void addInformation( ItemStack stack , @Nullable World player , List< String > tooltip , ITooltipFlag advanced )
    {
        originalBlockState.getBlock().addInformation( stack , player , tooltip , advanced );
    }

    @Override
    @SideOnly( Side.CLIENT )
    public boolean addDestroyEffects( World world , BlockPos pos , ParticleManager manager )
    {
        ParticleHelper.addDestroyEffects( world , pos , manager , null );

        return true;
    }

    @Override
    public boolean canEntityDestroy( IBlockState state , IBlockAccess world , BlockPos pos , Entity entity )
    {
        return originalBlockState.getBlock().canEntityDestroy( originalBlockState , world , pos , entity );
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

    @Deprecated
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

    @Deprecated
    @Override
    public BlockFaceShape getBlockFaceShape( IBlockAccess worldIn , IBlockState state , BlockPos pos , EnumFacing face )
    {
        return BlockFaceShape.UNDEFINED;
    }

    @Deprecated
    @Override
    public AxisAlignedBB getBoundingBox( IBlockState state , IBlockAccess source , BlockPos pos )
    {
        return AABB;
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBox( IBlockState blockState , IBlockAccess worldIn , BlockPos pos )
    {
        return NULL_AABB;
    }

    @Deprecated
    @Override
    public int getLightValue( IBlockState state )
    {
        return originalBlockState.getBlock().getLightValue( originalBlockState );
    }

    @Override
    public int getLightValue( IBlockState state , IBlockAccess world , BlockPos pos )
    {
        return (int)Math.ceil( originalBlockState.getBlock().getLightValue( state , world , pos ) / 4.0f );
    }

    @Override
    public String getLocalizedName()
    {
        return originalBlockState.getBlock().getLocalizedName();
    }

    @Deprecated
    @Override
    public MapColor getMapColor( IBlockState state , IBlockAccess worldIn , BlockPos pos )
    {
        return MapColor.AIR;
    }

    @Deprecated
    @Override
    public EnumPushReaction getMobilityFlag( IBlockState state )
    {
        // Redundant with our custom material, but explicit
        return EnumPushReaction.DESTROY;
    }

    @Deprecated
    @Override
    public Vec3d getOffset( IBlockState state , IBlockAccess worldIn , BlockPos pos )
    {
        if( !StrataConfig.restrictSampleOffset )
            return super.getOffset( state , worldIn , pos );

        long i = MathHelper.getCoordinateRandom( pos.getX() , 0 , pos.getZ() );
        double x = ( ( i >> 16 & 15L ) / 15.0f ) * MAX_OFFSET_FROM_CENTER;
        double z = ( ( i >> 24 & 15L ) / 15.0f ) * MAX_OFFSET_FROM_CENTER;

        return new Vec3d( x , 0.0d , z );
    }

    @Override
    public Block.EnumOffsetType getOffsetType()
    {
        return Block.EnumOffsetType.XZ;
    }

    @Override
    public SoundType getSoundType( IBlockState state , World world , BlockPos pos , @Nullable Entity entity )
    {
        return originalBlockState.getBlock().getSoundType( originalBlockState , world , pos , entity );
    }

    @Override
    public String getUnlocalizedName()
    {
        return originalBlockState.getBlock().getUnlocalizedName();
    }

    @Deprecated
    @Override
    public boolean isFullBlock( IBlockState state )
    {
        return false;
    }

    @Deprecated
    @Override
    public boolean isFullCube( IBlockState state )
    {
        return false;
    }

    @Deprecated
    @Override
    public boolean isOpaqueCube( IBlockState state )
    {
        return false;
    }

    @Override
    public boolean isPassable( IBlockAccess worldIn , BlockPos pos )
    {
        return true;
    }

    @Deprecated
    @Override
    public boolean isSideSolid( IBlockState base_state , IBlockAccess world , BlockPos pos , EnumFacing side )
    {
        return false;
    }

    @Deprecated
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
                    // Grab the item before Player.addItemStackToInventory() decrements the stack size
                    Item item = itemStack.getItem();

                    if( playerIn.addItemStackToInventory( itemStack ) )
                        playerIn.addStat( StatList.getObjectsPickedUpStats( item ) );
                    else
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
            if( StrataConfig.additionalBlockSounds && isPlayer && ( (EntityPlayer)entityIn ).isCreative() )
            {
                SoundType soundType = getSoundType( worldIn.getBlockState( pos ) , worldIn , pos , entityIn );
                worldIn.playSound(
                    entityIn.posX,
                    entityIn.posY,
                    entityIn.posZ,
                    soundType.getFallSound(),
                    SoundCategory.BLOCKS,
                    ( soundType.getVolume() + 1.0f ) / 2.0f,
                    soundType.getPitch() * 0.8f,
                    false );
            }
            else if( !worldIn.isRemote && ( isPlayer || net.minecraftforge.event.ForgeEventFactory.getMobGriefingEvent( worldIn , entityIn ) ) )
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

    private static class SampleMaterial extends MaterialLogic
    {
        public SampleMaterial()
        {
            super( MapColor.STONE );

            setNoPushMobility(); // AKA EnumPushReaction.DESTROY
        }
    }
}
