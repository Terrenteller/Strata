package com.riintouge.strata.block.geo;

import com.riintouge.strata.Strata;
import com.riintouge.strata.block.ParticleHelper;
import com.riintouge.strata.block.ProtoBlockTextureMap;
import com.riintouge.strata.block.SpecialBlockPropertyFlags;
import com.riintouge.strata.sound.AmbientSoundHelper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFalling;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.entity.projectile.EntityWitherSkull;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Mirror;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.EnumPlantType;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

public class GeoBlock extends BlockFalling
{
    public static ThreadLocal< Boolean > canSustainPlantEventOverride = ThreadLocal.withInitial( () -> false );
    protected static final PropertyEnum< EnumGeoOrientation > ORIENTATION = PropertyEnum.create( "orientation" , EnumGeoOrientation.class );

    protected IGeoTileInfo tileInfo;

    public GeoBlock( IGeoTileInfo tileInfo )
    {
        super( tileInfo.material() );
        this.tileInfo = tileInfo;

        ResourceLocation registryName = tileInfo.registryName();
        setRegistryName( registryName );
        setUnlocalizedName( registryName.toString() );
        if( tileInfo.type().isPrimary || tileInfo.type() == TileType.COBBLE )
            setCreativeTab( Strata.BLOCK_TAB );
        else
            setCreativeTab( Strata.BUILDING_BLOCK_TAB );

        setHarvestLevel( tileInfo.harvestTool() , tileInfo.harvestLevel() );
        setSoundType( tileInfo.soundType() );
        setHardness( tileInfo.hardness() );
        setResistance( tileInfo.explosionResistance() );
        if( tileInfo.slipperiness() != null )
            setDefaultSlipperiness( tileInfo.slipperiness() );
    }

    public boolean canFall()
    {
        return tileInfo.type() == TileType.SAND;
    }

    public IGeoTileInfo getTileInfo()
    {
        return tileInfo;
    }

    // BlockFalling overrides

    @Override
    @SideOnly( Side.CLIENT )
    public int getDustColor( IBlockState state )
    {
        return tileInfo.particleFallingColor();
    }

    @Override
    @SideOnly( Side.CLIENT )
    public void randomDisplayTick( IBlockState stateIn , World worldIn , BlockPos pos , Random rand )
    {
        if( tileInfo.ambientSound() != null )
            AmbientSoundHelper.playForRandomDisplayTick( worldIn , pos , rand , tileInfo.ambientSound() );

        if( canFall() )
            super.randomDisplayTick( stateIn , worldIn , pos , rand );
    }

    // Block overrides

    @Override
    @SideOnly( Side.CLIENT )
    public boolean addDestroyEffects( World world , BlockPos pos , ParticleManager manager )
    {
        ProtoBlockTextureMap hostTextureMap = tileInfo.modelTextureMap();
        ParticleHelper.addDestroyEffects( world , pos , manager , RANDOM , hostTextureMap );

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
    public boolean canEntityDestroy( IBlockState state , IBlockAccess world , BlockPos pos , Entity entity )
    {
        if( ( tileInfo.specialBlockPropertyFlags() & SpecialBlockPropertyFlags.DRAGON_IMMUNE ) > 0
            && entity instanceof EntityDragon )
        {
            return false;
        }
        else if( ( tileInfo.specialBlockPropertyFlags() & SpecialBlockPropertyFlags.WITHER_IMMUNE ) > 0
            && ( entity instanceof EntityWither || entity instanceof EntityWitherSkull ) )
        {
            return false;
        }

        return super.canEntityDestroy( state , world , pos , entity );
    }

    @Override
    public boolean canSustainPlant( IBlockState state , IBlockAccess world , BlockPos pos , EnumFacing direction , IPlantable plantable )
    {
        // Because worldgen may swap an otherwise valid block with us (such as hardened clay with limestone in a mesa),
        // we cannot accurately determine validity here without knowing worldgen implementation details. As such,
        // allow anything so existing plants don't drop their items in the world. Prefer strange placement over litter.
        // Event handlers must enforce actual restrictions.
        if( !canSustainPlantEventOverride.get() )
            return true;
        else if( !tileInfo.type().isPrimary )
            return false;

        // FIXME: What if there is already a plant at pos which is replaceable?
        EnumPlantType plantType = plantable.getPlantType( world , pos );
        if( tileInfo.sustainedPlantTypes().contains( plantType ) )
            return true;

        for( IBlockState otherState : tileInfo.sustainsPlantsSustainedBy() )
            if( otherState.getBlock().canSustainPlant( otherState , world , pos , direction , plantable ) )
                return true;

        return false;
    }

    @Override
    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer( this , ORIENTATION );
    }

    @Override
    public float getExplosionResistance( World world , BlockPos pos , @Nullable Entity exploder , Explosion explosion )
    {
        // Wither explosions call canEntityDestroy() but do not respect its intentions
        if( !canEntityDestroy( world.getBlockState( pos ) , world , pos , exploder ) )
            return 6000000.0f; // Same as bedrock

        // Explosion resistance math is weird. We can't simply return what IGeoTileInfo provides.
        return super.getExplosionResistance( world , pos , exploder , explosion );
    }

    @Override
    public Item getItemDropped( IBlockState state , Random rand , int fortune )
    {
        Item item = null;

        try
        {
            item = tileInfo.type() == TileType.STONE
                ? Item.REGISTRY.getObject( TileType.COBBLE.registryName( tileInfo.tileSetName() ) )
                : Item.REGISTRY.getObject( GeoItemFragment.getResourceLocation( tileInfo ) );
        }
        catch( NullPointerException e )
        {
            // No special drop
        }

        return item != null ? item : super.getItemDropped( state , rand , fortune );
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
        String name = tileInfo.localizedName();
        return name != null ? name : tileInfo.registryName().toString();
    }

    @Override
    public int getMetaFromState( IBlockState state )
    {
        return ( state.getValue( ORIENTATION ) ).meta;
    }

    @Override
    public IBlockState getStateForPlacement(
        World worldIn,
        BlockPos pos,
        EnumFacing facing,
        float hitX,
        float hitY,
        float hitZ,
        int meta,
        EntityLivingBase placer )
    {
        return this.getDefaultState()
            .withProperty( ORIENTATION , EnumGeoOrientation.placedAgainst( facing , placer.getHorizontalFacing() ) );
    }

    @Override
    public IBlockState getStateFromMeta( int meta )
    {
        return this.getDefaultState()
            .withProperty( ORIENTATION , EnumGeoOrientation.VALUES[ meta ] );
    }

    @Override
    public EnumFacing[] getValidRotations( World world , BlockPos pos )
    {
        return EnumFacing.VALUES;
    }

    @Override
    public boolean isFireSource( World world , BlockPos pos , EnumFacing side )
    {
        return ( tileInfo.specialBlockPropertyFlags() & SpecialBlockPropertyFlags.FIRE_SOURCE ) > 0
            || super.isFireSource( world , pos , side );
    }

    @Override
    public void neighborChanged( IBlockState state , World worldIn , BlockPos pos , Block blockIn , BlockPos fromPos )
    {
        if( canFall() )
            super.neighborChanged( state , worldIn , pos , blockIn , fromPos );
    }

    @Override
    public void onBlockAdded( World worldIn , BlockPos pos , IBlockState state )
    {
        if( canFall() )
            super.onBlockAdded( worldIn , pos , state );
    }

    @Override
    public int quantityDropped( Random random )
    {
        return Item.REGISTRY.getObject( GeoItemFragment.getResourceLocation( tileInfo ) ) != null ? 4 : 1;
    }

    @Override
    public boolean rotateBlock( World world , BlockPos pos , EnumFacing axis )
    {
        IBlockState state = world.getBlockState( pos );
        EnumGeoOrientation rotatedOrientation = state.getValue( ORIENTATION ).rotate( axis );
        if( rotatedOrientation == null )
            return false;

        world.setBlockState( pos , state.withProperty( ORIENTATION , rotatedOrientation ) );
        return true;
    }

    @Override
    public int tickRate( World worldIn )
    {
        // 10 is the default from Block. We can't call Block.tickRate() from here because Java.
        return canFall() ? super.tickRate( worldIn ) : 10;
    }

    @Override
    public void updateTick( World worldIn , BlockPos pos , IBlockState state , Random rand )
    {
        if( canFall() )
            super.updateTick( worldIn , pos , state , rand );
    }

    @Override
    public IBlockState withMirror( IBlockState state , Mirror mirrorIn )
    {
        return state.withProperty( ORIENTATION , state.getValue( ORIENTATION ).mirror( mirrorIn ) );
    }

    @Override
    public IBlockState withRotation( IBlockState state , Rotation rot )
    {
        return state.withProperty( ORIENTATION , state.getValue( ORIENTATION ).rotate( rot ) );
    }
}
