package com.riintouge.strata.block.geo;

import com.riintouge.strata.StrataConfig;
import com.riintouge.strata.misc.MetaResourceLocation;
import com.riintouge.strata.block.ParticleHelper;
import com.riintouge.strata.block.SpecialBlockPropertyFlags;
import com.riintouge.strata.block.host.IHostInfo;
import com.riintouge.strata.gui.StrataCreativeTabs;
import com.riintouge.strata.item.IDropFormula;
import com.riintouge.strata.item.StaticDropFormula;
import com.riintouge.strata.item.geo.GeoItemFragment;
import com.riintouge.strata.sound.AmbientSoundHelper;
import com.riintouge.strata.sound.SoundEventTuple;
import com.riintouge.strata.util.FlagUtil;
import com.riintouge.strata.util.Util;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFalling;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityWitherSkull;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatBase;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.*;
import net.minecraftforge.common.EnumPlantType;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.FakePlayerFactory;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;
import java.util.function.Supplier;

public class GeoBlock extends BlockFalling
{
    public static final ThreadLocal< Boolean > CAN_SUSTAIN_PLANT_EVENT_CHECK = ThreadLocal.withInitial( () -> false );
    public static final ThreadLocal< HarvestReason > HARVEST_REASON = ThreadLocal.withInitial( () -> HarvestReason.UNDEFINED );

    protected IGeoTileInfo tileInfo;

    public enum HarvestReason
    {
        UNDEFINED,
        MELT,
        SUBLIMATE
    }

    public GeoBlock( IGeoTileInfo tileInfo )
    {
        super( tileInfo.material() );
        this.tileInfo = tileInfo;

        ResourceLocation registryName = tileInfo.registryName();
        setHardness( tileInfo.hardness() );
        setHarvestLevel( tileInfo.harvestTool() , tileInfo.harvestLevel() );
        setRegistryName( registryName );
        setResistance( tileInfo.explosionResistance() );
        setSoundType( tileInfo.soundType() );
        setTickRandomly( getTickRandomly() || tileInfo.ticksRandomly() );
        setUnlocalizedName( registryName.toString() );

        if( tileInfo.tileType().isPrimary || tileInfo.tileType() == TileType.COBBLE )
            setCreativeTab( StrataCreativeTabs.BLOCK_TAB );
        else
            setCreativeTab( StrataCreativeTabs.BUILDING_BLOCK_TAB );

        Float slipperiness = tileInfo.slipperiness();
        if( slipperiness != null )
            setDefaultSlipperiness( slipperiness );
    }

    public boolean canFall()
    {
        return tileInfo.tileType() == TileType.SAND
            || tileInfo.tileType() == TileType.GRAVEL
            || FlagUtil.check( tileInfo.specialBlockPropertyFlags() , SpecialBlockPropertyFlags.AFFECTED_BY_GRAVITY );
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
        SoundEventTuple ambientSound = tileInfo.ambientSound();
        if( ambientSound != null )
            AmbientSoundHelper.playForRandomDisplayTick( worldIn , pos , rand , ambientSound );

        if( canFall() )
            super.randomDisplayTick( stateIn , worldIn , pos , rand );
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
    public boolean canEntityDestroy( IBlockState state , IBlockAccess world , BlockPos pos , Entity entity )
    {
        if( FlagUtil.check( tileInfo.specialBlockPropertyFlags() , SpecialBlockPropertyFlags.DRAGON_IMMUNE )
            && entity instanceof EntityDragon )
        {
            return false;
        }
        else if( FlagUtil.check( tileInfo.specialBlockPropertyFlags() , SpecialBlockPropertyFlags.WITHER_IMMUNE )
            && ( entity instanceof EntityWither || entity instanceof EntityWitherSkull ) )
        {
            return false;
        }

        return super.canEntityDestroy( state , world , pos , entity );
    }

    @Deprecated
    @Override
    protected boolean canSilkHarvest()
    {
        if( tileInfo.tileType().isPrimary && FlagUtil.check( tileInfo.specialBlockPropertyFlags() , SpecialBlockPropertyFlags.NO_SILK_TOUCH ) )
            return false;

        return super.canSilkHarvest();
    }

    @Override
    public boolean canSustainPlant( IBlockState state , IBlockAccess world , BlockPos pos , EnumFacing direction , IPlantable plantable )
    {
        // Worldgen may swap an otherwise valid block with us (such as hardened clay with limestone in a mesa).
        // We cannot accurately determine validity here without knowing worldgen implementation details.
        // As such, allow anything so existing plants don't drop their items in the world.
        // Prefer strange placement over litter. Event handlers must enforce actual restrictions.
        if( !CAN_SUSTAIN_PLANT_EVENT_CHECK.get() )
            return true;
        else if( !tileInfo.tileType().isPrimary )
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
        return new BlockStateContainer( this , PropertyOrientation.PROPERTY );
    }

    @Override
    public int getExpDrop( IBlockState state , IBlockAccess world , BlockPos pos , int fortune )
    {
        IDropFormula expDropFormula = tileInfo.experienceDropFormula();
        if( expDropFormula == null )
            return 0;

        if( !tileInfo.tileType().isPrimary || state.getValue( PropertyOrientation.PROPERTY ) != PropertyOrientation.NATURAL )
            return 0;

        // We don't have the actual tool at this point but we do have creativity
        ItemStack fakeHarvestTool = new ItemStack( Items.POTATO );
        fakeHarvestTool.addEnchantment( Enchantments.FORTUNE , fortune );
        return Math.max( 0 , expDropFormula.getAmount( RANDOM , fakeHarvestTool , pos ) );
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
        Item drop = null;

        if( tileInfo.tileType() == TileType.CLAY || StrataConfig.dropNonStandardFragments )
        {
            if( tileInfo.hasFragment() )
            {
                drop = Item.REGISTRY.getObject( GeoItemFragment.fragmentRegistryName( tileInfo ) );
            }
            else if( tileInfo.tileType() == TileType.COBBLE )
            {
                IGeoTileInfo stoneInfo = GeoTileSetRegistry.INSTANCE.findTileInfo( tileInfo.tileSetName() , TileType.STONE );
                drop = stoneInfo != null ? Item.REGISTRY.getObject( GeoItemFragment.fragmentRegistryName( stoneInfo ) ) : null;
            }
        }

        if( drop == null && tileInfo.tileType() == TileType.STONE )
            drop = Item.REGISTRY.getObject( TileType.COBBLE.registryName( tileInfo.tileSetName() ) );

        return drop != null ? drop : super.getItemDropped( state , rand , fortune );
    }

    @Override
    public int getLightOpacity( IBlockState state , IBlockAccess world , BlockPos pos )
    {
        Integer lightOpacity = tileInfo.lightOpacity();
        return lightOpacity != null ? lightOpacity : super.getLightOpacity( state , world , pos );
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
        return ( state.getValue( PropertyOrientation.PROPERTY ) ).meta;
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
            .withProperty( PropertyOrientation.PROPERTY , PropertyOrientation.placedAgainst( facing , placer.getHorizontalFacing() ) );
    }

    @Deprecated
    @Override
    public IBlockState getStateFromMeta( int meta )
    {
        return this.getDefaultState()
            .withProperty( PropertyOrientation.PROPERTY , PropertyOrientation.VALUES[ meta ] );
    }

    @Override
    public EnumFacing[] getValidRotations( World world , BlockPos pos )
    {
        return EnumFacing.VALUES;
    }

    public void harvestBlock( World worldIn , EntityPlayer player , BlockPos pos , IBlockState state , @Nullable TileEntity te , ItemStack stack )
    {
        MetaResourceLocation replacementBlockResourceLocation = null;

        // meltsAt() and sublimatesAt() determine whether meltsInto() and sublimatesInto() are meaningful,
        // respectively. However, it doesn't make sense to require *Into() to not be null
        // assuming it will only be used when *At() is valid. If the block does not melt or sublimate,
        // *Into() returning null is the right thing to do. Therefore, we end up with a slight disconnect
        // between what we require in text, require in code, and what the code actually does with missing values.
        // tl;dr TileData requires both parameters but the interface does not enforce it.
        switch( HARVEST_REASON.get() )
        {
            case SUBLIMATE:
                replacementBlockResourceLocation = tileInfo.sublimatesInto();
                if( replacementBlockResourceLocation == null )
                    replacementBlockResourceLocation = new MetaResourceLocation( "minecraft:air" );
                if( worldIn instanceof WorldServer )
                {
                    ( (WorldServer)worldIn ).spawnParticle(
                        EnumParticleTypes.SMOKE_LARGE,
                        pos.getX() + 0.5,
                        pos.getY() + 0.25,
                        pos.getZ() + 0.5,
                        8,
                        0.5,
                        0.25,
                        0.5,
                        0.0 );
                }
                break;
            case MELT:
                replacementBlockResourceLocation = tileInfo.meltsInto();
                if( replacementBlockResourceLocation == null )
                    replacementBlockResourceLocation = new MetaResourceLocation( "minecraft:air" );
                break;
            default:
                replacementBlockResourceLocation = tileInfo.breaksInto();
        }

        if( !tileInfo.tileType().isPrimary
            || replacementBlockResourceLocation == null
            || ( canSilkHarvest( worldIn , pos , state , player ) && EnchantmentHelper.getEnchantmentLevel( Enchantments.SILK_TOUCH , stack ) > 0 ) )
        {
            // Consider overriding getDrops() to drop all the fragments in a single stack.
            // Unless it is more visually pleasing to possibly see multiple item stacks?
            // Would we want to ensure fragments drop in a more dispersed way?
            // What about a toggle to ease the burden on stack-combining anti-lag mods?
            super.harvestBlock( worldIn , player , pos , state , te , stack );
            return;
        }

        StatBase blockStats = StatList.getBlockStats( this );
        if( blockStats != null )
            player.addStat( blockStats );
        player.addExhaustion( 0.005f ); // Taken from Block.harvestBlock()

        Block replacementBlock = Block.REGISTRY.getObject( replacementBlockResourceLocation.resourceLocation );
        IBlockState replacementBlockState = replacementBlock.getStateFromMeta( replacementBlockResourceLocation.meta );
        Material replacementMaterial = replacementBlock.getMaterial( replacementBlockState );

        // Ice requires a solid block or liquid below it to turn into water. No explanation is given.
        if( !( replacementMaterial == Material.WATER && worldIn.provider.doesWaterVaporize() ) )
            worldIn.setBlockState( pos , replacementBlockState );
    }

    @Override
    public boolean isFireSource( World world , BlockPos pos , EnumFacing side )
    {
        return FlagUtil.check( tileInfo.specialBlockPropertyFlags() , SpecialBlockPropertyFlags.FIRE_SOURCE )
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
    public void onFallenUpon( World worldIn , BlockPos pos , Entity entityIn , float fallDistance )
    {
        onFallenUponCommon( this , worldIn , pos , entityIn , fallDistance );

        super.onFallenUpon( worldIn , pos , entityIn , fallDistance );
    }

    @Override
    public int quantityDropped( Random random )
    {
        // quantityDroppedWithBonus() normally calls us but we invert that to de-duplicate logic
        return quantityDroppedWithBonus( 0 , random );
    }

    @Override
    public int quantityDroppedWithBonus( int fortune , Random random )
    {
        ResourceLocation fragmentRegistryName = null;

        if( tileInfo.tileType() == TileType.CLAY || StrataConfig.dropNonStandardFragments )
        {
            if( tileInfo.hasFragment() )
            {
                fragmentRegistryName = GeoItemFragment.fragmentRegistryName( tileInfo );
            }
            else if( tileInfo.tileType() == TileType.COBBLE )
            {
                IGeoTileInfo stoneInfo = GeoTileSetRegistry.INSTANCE.findTileInfo( tileInfo.tileSetName() , TileType.STONE );
                fragmentRegistryName = stoneInfo != null ? GeoItemFragment.fragmentRegistryName( stoneInfo ) : null;
            }
        }

        if( Item.REGISTRY.getObject( fragmentRegistryName ) == null )
            return 1;

        IDropFormula fragmentDropFormula = tileInfo.fragmentDropFormula();
        if( fragmentDropFormula == null )
            return StaticDropFormula.STANDARD_FRAGMENT_COUNT;

        // We don't have the actual tool at this point but we do have creativity
        ItemStack fakeHarvestTool = new ItemStack( Items.POTATO );
        fakeHarvestTool.addEnchantment( Enchantments.FORTUNE , fortune );
        int dropAmount = fragmentDropFormula.getAmount( random , fakeHarvestTool , null );

        return Util.clamp( 0 , dropAmount , StaticDropFormula.STANDARD_FRAGMENT_COUNT );
    }

    @Override
    public void randomTick( World worldIn , BlockPos pos , IBlockState state , Random random )
    {
        // Do not call super.randomTick() because it calls updateTick()

        if( worldIn.isRemote )
            return;

        HarvestReason harvestReason = checkRandomHarvest( tileInfo , this , worldIn , pos , state );
        if( harvestReason == HarvestReason.UNDEFINED )
            return;

        FakePlayer fakePlayer = FakePlayerFactory.getMinecraft( (WorldServer)worldIn );
        ItemStack fakeHarvestTool = new ItemStack( Items.POTATO );

        try
        {
            HARVEST_REASON.set( harvestReason );
            harvestBlock( worldIn , fakePlayer , pos , state , null , fakeHarvestTool );
        }
        finally
        {
            HARVEST_REASON.remove();
        }
    }

    @Override
    public boolean rotateBlock( World world , BlockPos pos , EnumFacing axis )
    {
        IBlockState state = world.getBlockState( pos );
        PropertyOrientation rotatedOrientation = state.getValue( PropertyOrientation.PROPERTY ).rotate( axis );
        if( rotatedOrientation == null )
            return false;

        world.setBlockState( pos , state.withProperty( PropertyOrientation.PROPERTY , rotatedOrientation ) );
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

    @Deprecated
    @Override
    public IBlockState withMirror( IBlockState state , Mirror mirrorIn )
    {
        return state.withProperty( PropertyOrientation.PROPERTY , state.getValue( PropertyOrientation.PROPERTY ).mirror( mirrorIn ) );
    }

    @Deprecated
    @Override
    public IBlockState withRotation( IBlockState state , Rotation rot )
    {
        return state.withProperty( PropertyOrientation.PROPERTY , state.getValue( PropertyOrientation.PROPERTY ).rotate( rot ) );
    }

    // Statics

    public static HarvestReason checkRandomHarvest( IHostInfo hostInfo , Block block , World worldIn , BlockPos pos , IBlockState state )
    {
        Integer sublimatesAt = hostInfo.sublimatesAt();
        Integer meltsAt = hostInfo.meltsAt();
        if( sublimatesAt == null && meltsAt == null )
            return HarvestReason.UNDEFINED;

        // Ensure blocks that emit enough light to melt themselves can still do so
        // even when surrounded by opaque or partially translucent blocks
        int brightestNeighbour = block.getLightValue( state , worldIn , pos ) - 1;
        for( EnumFacing facing : EnumFacing.VALUES )
            brightestNeighbour = Math.max( brightestNeighbour , worldIn.getLightFor( EnumSkyBlock.BLOCK , pos.offset( facing ) ) );

        return sublimatesAt != null && brightestNeighbour >= sublimatesAt
            ? HarvestReason.SUBLIMATE
            : meltsAt != null && brightestNeighbour >= meltsAt
                ? HarvestReason.MELT
                : HarvestReason.UNDEFINED;
    }

    public static void onFallenUponCommon( Block block , World worldIn , BlockPos pos , Entity entityIn , float fallDistance )
    {
        if( StrataConfig.additionalBlockSounds && entityIn instanceof EntityLivingBase && fallDistance > 3.0 )
        {
            SoundType soundType = block.getSoundType( worldIn.getBlockState( pos ) , worldIn , pos , entityIn );
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
    }
}
