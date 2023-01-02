package com.riintouge.strata.block.ore;

import com.riintouge.strata.Strata;
import com.riintouge.strata.block.MetaResourceLocation;
import com.riintouge.strata.block.ParticleHelper;
import com.riintouge.strata.block.ProtoBlockTextureMap;
import com.riintouge.strata.block.SpecialBlockPropertyFlags;
import com.riintouge.strata.block.geo.BakedModelCache;
import com.riintouge.strata.block.geo.HostRegistry;
import com.riintouge.strata.block.geo.IHostInfo;
import com.riintouge.strata.item.IDropFormula;
import com.riintouge.strata.item.WeightedDropCollections;
import com.riintouge.strata.network.NetworkManager;
import com.riintouge.strata.network.OreBlockLandingEffectMessage;
import com.riintouge.strata.network.OreBlockRunningEffectMessage;
import com.riintouge.strata.sound.AmbientSoundHelper;
import com.riintouge.strata.util.StateUtil;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.EnumPushReaction;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleDigging;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatBase;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.lang3.NotImplementedException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Random;

public class OreBlock extends OreBaseBlock
{
    // Ore blocks and their item blocks have a slightly different registry name
    // so the ore items can have the name of the ore without decoration
    public static final String REGISTRY_NAME_SUFFIX = "_ore";

    protected ThreadLocal< ItemStack > harvestTool = new ThreadLocal<>();
    protected ThreadLocal< Integer > particleColor = new ThreadLocal<>();

    public OreBlock( IOreInfo oreInfo )
    {
        super( oreInfo , oreInfo.material() );

        setRegistryName( Strata.modid + ":" + oreInfo.oreName() + REGISTRY_NAME_SUFFIX );
        IBlockState proxyBlockState = oreInfo.proxyBlockState();
        Block proxyBlock = proxyBlockState != null ? proxyBlockState.getBlock() : null;
        if( proxyBlock != null )
            setUnlocalizedName( proxyBlock.getUnlocalizedName() );
        else
            setUnlocalizedName( Strata.modid + ":" + oreInfo.oreName() );

        setHarvestLevel( oreInfo.harvestTool() , oreInfo.harvestLevel() );
        setSoundType( oreInfo.soundType() );
        setHardness( oreInfo.hardness() );
        setResistance( oreInfo.explosionResistance() );

        setCreativeTab( Strata.ORE_BLOCK_TAB );
    }

    public void addLandingEffects( WorldClient world , BlockPos blockPos , double xPos , double yPos , double zPos , int numberOfParticles )
    {
        if( !world.isRemote )
            return;

        ParticleManager particleManager = Minecraft.getMinecraft().effectRenderer;
        if( particleManager == null )
            return;

        IBlockState actualState = world.getBlockState( blockPos ).getActualState( world , blockPos );
        MetaResourceLocation host = StateUtil.getValue( actualState , UnlistedPropertyHostRock.PROPERTY , null );
        IHostInfo hostInfo = host != null ? HostRegistry.INSTANCE.find( host ) : null;
        TextureAtlasSprite combinedTexture = null;
        TextureAtlasSprite hostTexture = null;

        if( OreParticleTextureManager.INSTANCE.isActive() && hostInfo != null && hostInfo.modelTextureMap() != null )
        {
            combinedTexture = OreParticleTextureManager.INSTANCE.findTextureOrNull(
                oreInfo.oreName(),
                host.resourceLocation.getResourceDomain(),
                host.resourceLocation.getResourcePath(),
                host.meta,
                EnumFacing.UP );
        }

        if( combinedTexture == null && host != null )
            hostTexture = hostInfo.modelTextureMap().getTexture( EnumFacing.UP );

        // We could register our particle factory in ClientProxy and call ParticleManager.spawnEffectParticle()
        // instead, but that method is designed more for SPacketParticles which we can't use because it calls
        // BlockModelShapes.getTexture() which lacks world and coordinate information. It also keeps
        // ParticleOreBlockDust.Factory out of a code path which doesn't need to know about it.
        ParticleOreBlockDust.Factory particleFactory = new ParticleOreBlockDust.Factory();

        // Don't limit the number of ore particles unless we must show the host particles separately
        int numberOfOreParticles = combinedTexture == null && hostTexture != null
            ? (int)Math.floor( numberOfParticles / 3.0 )
            : numberOfParticles;

        for( int index = 0 ; index < numberOfParticles ; index++ )
        {
            // 0.15 comes from EntityLivingBase
            Particle particle = particleFactory.createParticle(
                actualState,
                world,
                xPos,
                yPos,
                zPos,
                RANDOM.nextGaussian() * 0.15,
                RANDOM.nextGaussian() * 0.15,
                RANDOM.nextGaussian() * 0.15 );

            if( particle != null )
            {
                if( combinedTexture != null )
                    particle.setParticleTexture( combinedTexture );
                else if( index >= numberOfOreParticles )
                    particle.setParticleTexture( hostTexture );

                particleManager.addEffect( particle );
            }
        }
    }

    @SideOnly( Side.CLIENT )
    protected boolean addPrecomputedDestroyEffects( World world , BlockPos pos , ParticleManager manager )
    {
        try
        {
            IBlockState state = world.getBlockState( pos ).getActualState( world , pos );
            int blockId = Block.getIdFromBlock( state.getBlock() );

            TextureAtlasSprite baseTextures[] = new TextureAtlasSprite[ EnumFacing.values().length ];
            String oreName = oreInfo.oreName();
            MetaResourceLocation host = StateUtil.getValue( state , UnlistedPropertyHostRock.PROPERTY , null );
            if( host == null )
                return false;

            IHostInfo hostInfo = HostRegistry.INSTANCE.find( host );
            if( hostInfo == null || hostInfo.modelTextureMap() == null )
                return false;

            String hostResourceDomain = host.resourceLocation.getResourceDomain();
            String hostResourceLocation = host.resourceLocation.getResourcePath();

            for( EnumFacing facing : EnumFacing.values() )
            {
                baseTextures[ facing.ordinal() ] = OreParticleTextureManager.INSTANCE.findTextureOrMissing(
                    oreName,
                    hostResourceDomain,
                    hostResourceLocation,
                    host.meta,
                    facing );
            }

            // This loop sampled from ParticleManager.addBlockDestroyEffects()
            for( int x = 0 ; x < 4 ; ++x )
            {
                double d0 = ( (double)x + 0.5d ) / 4.0d;

                for( int y = 0 ; y < 4 ; ++y )
                {
                    double d1 = ( (double)y + 0.5d ) / 4.0d;

                    for( int z = 0 ; z < 4 ; ++z )
                    {
                        double d2 = ( (double)z + 0.5d ) / 4.0d;

                        ParticleDigging particleDigging = (ParticleDigging)new ParticleDigging.Factory().createParticle(
                            0, // unused
                            world,
                            (double)pos.getX() + d0,
                            (double)pos.getY() + d1,
                            (double)pos.getZ() + d2,
                            d0 - 0.5d,
                            d1 - 0.5d,
                            d2 - 0.5d,
                            blockId );

                        TextureAtlasSprite texture = baseTextures[ EnumFacing.VALUES[ RANDOM.nextInt( 6 ) ].ordinal() ];
                        particleDigging.setBlockPos( pos ).setParticleTexture( texture );
                        manager.addEffect( particleDigging );
                    }
                }
            }

            return true;
        }
        catch( Exception e )
        {
            // TODO: warn
        }

        return false;
    }

    public void addRunningEffects(
        WorldClient world,
        BlockPos blockPos,
        double xPos,
        double yPos,
        double zPos,
        double xSpeed,
        double ySpeed,
        double zSpeed )
    {
        if( !world.isRemote )
            return;

        ParticleManager particleManager = Minecraft.getMinecraft().effectRenderer;
        if( particleManager == null )
            return;

        IBlockState actualState = world.getBlockState( blockPos ).getActualState( world , blockPos );
        MetaResourceLocation host = StateUtil.getValue( actualState , UnlistedPropertyHostRock.PROPERTY , null );
        IHostInfo hostInfo = host != null ? HostRegistry.INSTANCE.find( host ) : null;
        TextureAtlasSprite combinedTexture = null;
        TextureAtlasSprite hostTexture = null;

        if( OreParticleTextureManager.INSTANCE.isActive() && hostInfo != null && hostInfo.modelTextureMap() != null )
        {
            combinedTexture = OreParticleTextureManager.INSTANCE.findTextureOrNull(
                oreInfo.oreName(),
                host.resourceLocation.getResourceDomain(),
                host.resourceLocation.getResourcePath(),
                host.meta,
                EnumFacing.UP );
        }

        if( combinedTexture == null && host != null && RANDOM.nextFloat() < ( 1.0f / 3.0f ) )
            hostTexture = hostInfo.modelTextureMap().getTexture( EnumFacing.UP );

        // We could register our particle factory in ClientProxy and call ParticleManager.spawnEffectParticle()
        // instead, but that method is designed more for SPacketParticles which we can't use because it calls
        // BlockModelShapes.getTexture() which lacks world and coordinate information. It also keeps
        // ParticleOreDigging.Factory out of a code path which doesn't need to know about it.
        ParticleOreDigging.Factory particleFactory = new ParticleOreDigging.Factory();
        Particle particle = particleFactory.createParticle( actualState , world , xPos , yPos , zPos , xSpeed , ySpeed , zSpeed );
        if( particle != null )
        {
            if( combinedTexture != null )
                particle.setParticleTexture( combinedTexture );
            else if( hostTexture != null )
                particle.setParticleTexture( hostTexture );

            particleManager.addEffect( particle );
        }
    }

    @Nonnull
    public IExtendedBlockState getCompleteExtendedState( OreBlockTileEntity entity , IBlockState state , IBlockAccess world , BlockPos pos )
    {
        IExtendedBlockState extendedState = getDefaultExtendedState( state );
        return entity != null
            ? extendedState.withProperty( UnlistedPropertyHostRock.PROPERTY , entity.getHostRock() )
            : extendedState;
    }

    @Nonnull
    public IExtendedBlockState getDefaultExtendedState( IBlockState state )
    {
        return ( (IExtendedBlockState)state )
            .withProperty( UnlistedPropertyHostRock.PROPERTY , UnlistedPropertyHostRock.DEFAULT );
    }

    @Nonnull
    public MetaResourceLocation getHost( IBlockAccess world , BlockPos pos )
    {
        OreBlockTileEntity tileEntity = getTileEntity( world , pos );
        return tileEntity != null ? tileEntity.getHostRock() : UnlistedPropertyHostRock.DEFAULT;
    }

    @Nullable
    public IHostInfo getHostInfo( IBlockState actualState )
    {
        MetaResourceLocation hostResource = StateUtil.getValue( actualState , UnlistedPropertyHostRock.PROPERTY , null );
        return hostResource != null ? HostRegistry.INSTANCE.find( hostResource ) : null;
    }

    @Nullable
    public OreBlockTileEntity getTileEntity( IBlockAccess world , BlockPos pos )
    {
        // This may be called before the tile entity is created
        TileEntity tileEntity = world.getTileEntity( pos );
        return tileEntity instanceof OreBlockTileEntity ? (OreBlockTileEntity)tileEntity : null;
    }

    // BlockFalling overrides

    @Override
    @SideOnly( Side.CLIENT )
    public int getDustColor( IBlockState state )
    {
        // We have to rely on randomDisplayTick() to set the particle color because state is clean
        return particleColor.get() != null ? particleColor.get() : super.getDustColor( state );
    }

    @Override
    @SideOnly( Side.CLIENT )
    public void randomDisplayTick( IBlockState stateIn , World worldIn , BlockPos pos , Random rand )
    {
        // Hosts are not expected to have an ambient sound.
        // We can do without the overhead of a lookup here given how often this method is called.
        if( oreInfo.ambientSound() != null )
            AmbientSoundHelper.playForRandomDisplayTick( worldIn , pos , rand , oreInfo.ambientSound() );

        // Cheap sanity check
        if( !canFallThrough( worldIn.getBlockState( pos.down() ) ) )
            return;

        IHostInfo hostInfo = HostRegistry.INSTANCE.find( getHost( worldIn , pos ) );
        // It would be fancy to check if the host block is BlockFalling, but all Strata rocks are BlockFalling
        if( hostInfo == null || hostInfo.material() != Material.SAND )
            return;

        particleColor.set( hostInfo.particleFallingColor() );
        super.randomDisplayTick( stateIn , worldIn , pos , rand );
    }

    // Block overrides

    @Override
    @SideOnly( Side.CLIENT )
    public boolean addDestroyEffects( World world , BlockPos pos , ParticleManager manager )
    {
        if( OreParticleTextureManager.INSTANCE.isActive() && addPrecomputedDestroyEffects( world , pos , manager ) )
            return true;

        try
        {
            IBlockState state = world.getBlockState( pos ).getActualState( world , pos );
            int blockId = Block.getIdFromBlock( state.getBlock() );

            MetaResourceLocation host = StateUtil.getValue( state , UnlistedPropertyHostRock.PROPERTY , null );
            IHostInfo hostInfo = HostRegistry.INSTANCE.find( host );
            ProtoBlockTextureMap hostTextureMap = hostInfo != null ? hostInfo.modelTextureMap() : null;

            String oreName = oreInfo.oreName();
            IOreTileSet oreTileSet = OreRegistry.INSTANCE.find( oreName );
            ProtoBlockTextureMap oreTextureMap = oreTileSet != null ? oreTileSet.getInfo().modelTextureMap() : null;

            if( hostTextureMap == null && host != null )
            {
                Block hostBlock = Block.getBlockFromName( host.resourceLocation.toString() );
                if( hostBlock != null )
                    hostBlock.addDestroyEffects( world , pos , manager );
            }

            // This loop sampled from ParticleManager.addBlockDestroyEffects()
            for( int x = 0 ; x < 4 ; ++x )
            {
                double d0 = ( (double)x + 0.5d ) / 4.0d;

                for( int y = 0 ; y < 4 ; ++y )
                {
                    double d1 = ( (double)y + 0.5d ) / 4.0d;

                    for( int z = 0 ; z < 4 ; ++z )
                    {
                        double d2 = ( (double)z + 0.5d ) / 4.0d;

                        // Spawn host particles like normal...
                        if( hostTextureMap != null )
                        {
                            ParticleDigging particleDigging = (ParticleDigging)new ParticleDigging.Factory().createParticle(
                                0, // unused
                                world,
                                (double)pos.getX() + d0,
                                (double)pos.getY() + d1,
                                (double)pos.getZ() + d2,
                                d0 - 0.5d,
                                d1 - 0.5d,
                                d2 - 0.5d,
                                blockId );

                            EnumFacing facing = EnumFacing.VALUES[ RANDOM.nextInt( 6 ) ];
                            TextureAtlasSprite texture = hostTextureMap.getTexture( facing );
                            particleDigging.setBlockPos( pos ).setParticleTexture( texture );
                            manager.addEffect( particleDigging );
                        }

                        // ...and ore particles in-between
                        if( oreTextureMap != null && x != 3 && y != 3 && z != 3 )
                        {
                            ParticleDigging particleDigging = (ParticleDigging)new ParticleDigging.Factory().createParticle(
                                0, // unused
                                world,
                                (double)pos.getX() + ( d0 + 0.25d ),
                                (double)pos.getY() + ( d1 + 0.25d ),
                                (double)pos.getZ() + ( d2 + 0.25d ),
                                d0 - 0.5d,
                                d1 - 0.5d,
                                d2 - 0.5d,
                                blockId );

                            EnumFacing facing = EnumFacing.VALUES[ RANDOM.nextInt( 6 ) ];
                            TextureAtlasSprite texture = oreTextureMap.getTexture( facing );
                            particleDigging.setBlockPos( pos ).setParticleTexture( texture );
                            manager.addEffect( particleDigging );
                        }
                    }
                }
            }

            return true;
        }
        catch( Exception e )
        {
            // TODO: warn
        }

        return false;
    }

    @Override
    @SideOnly( Side.CLIENT )
    public boolean addHitEffects( IBlockState state , World worldObj , RayTraceResult target , ParticleManager manager )
    {
        BlockPos blockPos = target.getBlockPos();
        IBlockState actualState = worldObj.getBlockState( blockPos ).getActualState( worldObj , blockPos );
        TextureAtlasSprite texture = null;

        if( OreParticleTextureManager.INSTANCE.isActive() )
        {
            MetaResourceLocation host = StateUtil.getValue( actualState , UnlistedPropertyHostRock.PROPERTY , null );
            if( host == null )
                return false;

            IHostInfo hostInfo = HostRegistry.INSTANCE.find( host );
            if( hostInfo != null && hostInfo.modelTextureMap() != null )
            {
                texture = OreParticleTextureManager.INSTANCE.findTextureOrNull(
                    oreInfo.oreName(),
                    host.resourceLocation.getResourceDomain(),
                    host.resourceLocation.getResourcePath(),
                    host.meta,
                    target.sideHit );
            }
        }

        if( texture == null )
        {
            if( RANDOM.nextBoolean() )
            {
                MetaResourceLocation host = StateUtil.getValue( actualState , UnlistedPropertyHostRock.PROPERTY , null );
                IHostInfo hostInfo = HostRegistry.INSTANCE.find( host );
                ProtoBlockTextureMap hostTextureMap = hostInfo != null ? hostInfo.modelTextureMap() : null;

                texture = hostTextureMap != null
                    ? hostTextureMap.getTexture( target.sideHit )
                    : BakedModelCache.INSTANCE.getBakedModel( host ).getParticleTexture();
            }
            else
            {
                String oreName = oreInfo.oreName();
                IOreTileSet oreTileSet = OreRegistry.INSTANCE.find( oreName );
                ProtoBlockTextureMap oreTextureMap = oreTileSet != null ? oreTileSet.getInfo().modelTextureMap() : null;

                texture = oreTextureMap != null
                    ? oreTextureMap.getTexture( target.sideHit )
                    : BakedModelCache.INSTANCE.getBakedOreModel( oreName ).getParticleTexture();
            }
        }

        if( texture != null )
        {
            ParticleHelper.createHitParticle( actualState , worldObj , target , manager , RANDOM , texture );
            return true;
        }

        return false;
    }

    @Override
    public boolean addLandingEffects(
        IBlockState state,
        WorldServer worldObj,
        BlockPos blockPosition,
        IBlockState iblockstate, // EntityLivingBase passes the same value to both state and iblockstate
        EntityLivingBase entity,
        int numberOfParticles )
    {
        // The base method says it's server-side but is not annotated as such
        if( !worldObj.isRemote )
        {
            NetworkManager.INSTANCE.NetworkWrapper.sendToAllAround(
                new OreBlockLandingEffectMessage(
                    (float)entity.posX,
                    (float)entity.posY,
                    (float)entity.posZ,
                    numberOfParticles ),
                new NetworkRegistry.TargetPoint(
                    entity.dimension,
                    entity.posX,
                    entity.posY,
                    entity.posZ,
                    1024.0 ) );
        }

        return true;
    }

    @Override
    public boolean addRunningEffects( IBlockState state , World world , BlockPos pos , Entity entity )
    {
        if( world.isRemote )
        {
            // These values taken from Entity.createRunningParticles()
            NetworkManager.INSTANCE.NetworkWrapper.sendToAllAround(
                new OreBlockRunningEffectMessage(
                    (float)( entity.posX + ( RANDOM.nextFloat() - 0.5d ) * (double)entity.width ),
                    (float)( entity.getEntityBoundingBox().minY + 0.1d ),
                    (float)( entity.posZ + ( RANDOM.nextFloat() - 0.5d ) * (double)entity.width ),
                    (float)( -entity.motionX * 4.0d ),
                    1.5f,
                    (float)( -entity.motionZ * 4.0d ) ),
                new NetworkRegistry.TargetPoint(
                    entity.dimension,
                    entity.posX,
                    entity.posY,
                    entity.posZ,
                    1024.0 ) );
        }

        return true;
    }

    @Override
    public boolean canDropFromExplosion( Explosion explosionIn )
    {
        // This block should never drop.
        // However, this method may determine if an explosion ultimately calls Block.getDrops().
        return true;
    }

    @Override
    public boolean canEntityDestroy( IBlockState state , IBlockAccess world , BlockPos pos , Entity entity )
    {
        // The assumption is that entities can destroy the block and the inability to destroy is the exception
        if( !super.canEntityDestroy( state , world , pos , entity ) )
            return false;

        MetaResourceLocation hostResourceLocation = getHost( world , pos );
        Block host = Block.REGISTRY.getObject( hostResourceLocation.resourceLocation );
        IBlockState hostBlockState = host.getStateFromMeta( hostResourceLocation.meta );
        return host == Blocks.AIR || host.canEntityDestroy( hostBlockState , world , pos , entity );
    }

    @Override
    protected boolean canSilkHarvest()
    {
        throw new NotImplementedException( "Use the state-sensitive overload instead!" );
    }

    @Override
    public boolean canSilkHarvest( World world , BlockPos pos , IBlockState state , EntityPlayer player )
    {
        // Silk touch only applies to proxy blocks since we should never drop ourself as an item
        IBlockState proxyBlockState = oreInfo.proxyBlockState();
        return proxyBlockState != null && proxyBlockState.getBlock().canSilkHarvest( world , pos , proxyBlockState , player );
    }

    @Override
    public boolean canRenderInLayer( IBlockState state , BlockRenderLayer layer )
    {
        return layer == BlockRenderLayer.SOLID || layer == BlockRenderLayer.TRANSLUCENT;
    }

    @Override
    public boolean canSustainPlant( IBlockState state , IBlockAccess world , BlockPos pos , EnumFacing direction , IPlantable plantable )
    {
        MetaResourceLocation hostResourceLocation = getHost( world , pos );
        Block host = Block.REGISTRY.getObject( hostResourceLocation.resourceLocation );
        IBlockState hostState = host.getStateFromMeta( hostResourceLocation.meta );
        if( host.canSustainPlant( hostState , world , pos , direction , plantable ) )
            return true;

        IBlockState proxyBlockState = oreInfo.proxyBlockState();
        return proxyBlockState != null
            ? proxyBlockState.getBlock().canSustainPlant( proxyBlockState , world , pos , direction , plantable )
            : super.canSustainPlant( state , world , pos , direction , plantable );
    }

    @Override
    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer.Builder( this )
            .add( UnlistedPropertyHostRock.PROPERTY )
            .build();
    }

    @Override
    public TileEntity createTileEntity( World world , IBlockState state )
    {
        return new OreBlockTileEntity( state );
    }

    @Override
    public void dropXpOnBlockBreak( World worldIn , BlockPos pos , int amount )
    {
        IBlockState proxyBlockState = oreInfo.proxyBlockState();
        if( proxyBlockState != null )
            proxyBlockState.getBlock().dropXpOnBlockBreak( worldIn , pos , amount );
        else
            super.dropXpOnBlockBreak( worldIn , pos , amount );
    }

    @Deprecated
    @Override
    public IBlockState getActualState( IBlockState state , IBlockAccess worldIn , BlockPos pos )
    {
        // BlockRendererDispatcher.renderBlock() calls BlockModelShapes.getModelForState()
        // which queries an IdentityHashMap populated by valid states of the block.
        // Because this block has no metadata properties, only unlisted,
        // there is only one valid state: the unmodified default.

        // Option 1: Accept reality and deal with the consequences
        //return super.getActualState( state , worldIn , pos );

        // Option 2: Derive from IExtendedBlockState and have it create and manage a set of valid block states.
        // Return whichever corresponds to the unlisted property values. Alternatively, create individual blocks.

        // Option 3: Examine the stack to determine our behaviour (JDK 9+ has StackWalker)
        //StackTraceElement[] stackTraceElements = new Throwable().getStackTrace();
        //String caller = stackTraceElements[ 2 ].toString();
        //return caller.contains( "renderBlock" ) || caller.contains( "addBlockDestroyEffects" )
        //    ? super.getActualState( state , worldIn , pos )
        //    : getExtendedState( state , worldIn , pos );

        // Option 4: Proxy BlockModelShapes.bakedModelStore to fallback on the state mapper
        return getExtendedState( state , worldIn , pos );
    }

    @Deprecated
    @Override
    public float getBlockHardness( IBlockState blockState , World worldIn , BlockPos pos )
    {
        float oreHardness = oreInfo.hardness();
        IBlockState proxyBlockState = oreInfo.proxyBlockState();
        if( proxyBlockState != null )
            oreHardness = proxyBlockState.getBlock().getBlockHardness( proxyBlockState , worldIn , pos );

        IHostInfo hostInfo = HostRegistry.INSTANCE.find( getHost( worldIn , pos ) );
        return hostInfo != null
            ? ( hostInfo.hardness() + oreHardness ) / 2.0f
            : oreHardness;
    }

    @Override
    public void getDrops( NonNullList< ItemStack > drops , IBlockAccess world , BlockPos pos , IBlockState state , int fortune )
    {
        MetaResourceLocation host = StateUtil.getValue( state , world , pos , UnlistedPropertyHostRock.PROPERTY , UnlistedPropertyHostRock.DEFAULT );
        IHostInfo hostInfo = HostRegistry.INSTANCE.find( host );
        if( hostInfo == null )
            return;

        // harvestBlock() replaces this block with the host and calls harvestBlock() on it
        // to give the host more control over what happens when it is broken.
        // Don't drop the drops of the host when the host will drop its drops itself.
        boolean dropHost = harvesters.get() == null;
        ItemStack harvestToolOrEmpty = harvestTool.get() != null ? harvestTool.get() : ItemStack.EMPTY;
        Block hostBlock = Block.REGISTRY.getObject( host.resourceLocation );
        IBlockState proxyBlockState = oreInfo.proxyBlockState();
        Block proxyBlock = proxyBlockState != null ? proxyBlockState.getBlock() : null;

        if( EnchantmentHelper.getEnchantmentLevel( Enchantments.SILK_TOUCH , harvestToolOrEmpty ) > 0 )
        {
            if( dropHost )
                drops.add( new ItemStack( hostBlock ) );

            if( proxyBlock != null )
                drops.add( new ItemStack( proxyBlock , 1 , proxyBlock.damageDropped( proxyBlockState ) ) );
        }
        else
        {
            if( dropHost )
                hostBlock.getDrops( drops , world , pos , hostBlock.getStateFromMeta( host.meta ) , fortune );

            if( proxyBlock != null )
                proxyBlock.getDrops( drops , world , pos , proxyBlockState , fortune );
        }

        if( proxyBlock == null )
        {
            WeightedDropCollections weightedDropCollections = oreInfo.weightedDropGroups();
            if( weightedDropCollections != null )
            {
                drops.addAll( weightedDropCollections.collectRandomDrops( RANDOM , harvestToolOrEmpty , pos ) );
            }
            else
            {
                IOreTileSet oreTileSet = OreRegistry.INSTANCE.find( oreInfo.oreName() );
                if( oreTileSet != null )
                    drops.add( new ItemStack( oreTileSet.getItem() ) );
            }
        }
    }

    @Override
    public int getExpDrop( IBlockState state , IBlockAccess world , BlockPos pos , int fortune )
    {
        IBlockState proxyBlockState = oreInfo.proxyBlockState();
        if( proxyBlockState != null )
            return proxyBlockState.getBlock().getExpDrop( proxyBlockState , world , pos , fortune );

        IDropFormula expDropFormula = oreInfo.expDropFormula();
        if( expDropFormula == null )
            return 0;

        // We don't have the actual tool at this point but we do have creativity
        ItemStack fakeHarvestTool = new ItemStack( Items.POTATO );
        fakeHarvestTool.addEnchantment( Enchantments.FORTUNE , fortune );
        return Math.max( 0 , expDropFormula.getAmount( RANDOM , fakeHarvestTool , pos ) );
    }

    @Deprecated
    @Override
    public float getExplosionResistance( Entity exploder )
    {
        IBlockState proxyBlockState = oreInfo.proxyBlockState();
        if( proxyBlockState != null )
            return proxyBlockState.getBlock().getExplosionResistance( exploder );

        // Explosion resistance math is weird. We can't simply return what IOreInfo provides.
        return super.getExplosionResistance( exploder );
    }

    @Override
    public float getExplosionResistance( World world , BlockPos pos , @Nullable Entity exploder , Explosion explosion )
    {
        // Wither explosions call canEntityDestroy() but do not respect its intentions
        if( !canEntityDestroy( world.getBlockState( pos ) , world , pos , exploder ) )
            return 6000000.0f; // Same as bedrock

        IBlockState proxyBlockState = oreInfo.proxyBlockState();
        if( proxyBlockState != null )
            return proxyBlockState.getBlock().getExplosionResistance( world , pos , exploder , explosion );

        // Explosion resistance math is weird. We can't simply return what IOreInfo provides.
        return super.getExplosionResistance( world , pos , exploder , explosion );
    }

    @Override
    public IBlockState getExtendedState( IBlockState state , IBlockAccess world , BlockPos pos )
    {
        return getCompleteExtendedState( getTileEntity( world , pos ) , state , world ,  pos );
    }

    @Override
    public int getHarvestLevel( IBlockState state )
    {
        // state is expected to have unlisted properties from ForgeHooks.canHarvestBlock()
        MetaResourceLocation hostResource = StateUtil.getValue( state , UnlistedPropertyHostRock.PROPERTY , null );
        IHostInfo hostInfo = hostResource != null ? HostRegistry.INSTANCE.find( hostResource ) : null;
        if( hostInfo != null )
        {
            // The harvest level of the ore doesn't matter if a tool is not required for the host.
            // For example, a tool is not required to dig rocks out of garden soil, but it helps.
            // Alternatively, let the harvest level be that of the host given the same reasoning.
            return hostInfo.material().isToolNotRequired() ? 0 : hostInfo.harvestLevel();
        }

        IBlockState proxyBlockState = oreInfo.proxyBlockState();
        return proxyBlockState != null
            ? proxyBlockState.getBlock().getHarvestLevel( proxyBlockState )
            : oreInfo.harvestLevel();
    }

    @Nullable
    @Override
    public String getHarvestTool( IBlockState state )
    {
        // state is expected to have unlisted properties from ForgeHooks.canHarvestBlock()
        MetaResourceLocation hostResource = StateUtil.getValue( state , UnlistedPropertyHostRock.PROPERTY , null );
        IHostInfo hostInfo = hostResource != null ? HostRegistry.INSTANCE.find( hostResource ) : null;
        if( hostInfo != null )
            return hostInfo.harvestTool();

        IBlockState proxyBlockState = oreInfo.proxyBlockState();
        return proxyBlockState != null
            ? proxyBlockState.getBlock().getHarvestTool( proxyBlockState )
            : oreInfo.harvestTool();
    }

    @Override
    public Item getItemDropped( IBlockState state , Random rand , int fortune )
    {
        return Items.AIR;
    }

    @Override
    public int getLightOpacity( IBlockState state , IBlockAccess world , BlockPos pos )
    {
        MetaResourceLocation hostResource = getHost( world , pos );
        Block hostBlock = ForgeRegistries.BLOCKS.getValue( hostResource.resourceLocation );

        return hostBlock != null
            ? hostBlock.getStateFromMeta( hostResource.meta ).getLightOpacity( world , pos )
            : super.getLightOpacity( state , world , pos );
    }

    @Override
    public int getLightValue( IBlockState state , IBlockAccess world , BlockPos pos )
    {
        int hostLightValue = 0;
        IHostInfo hostInfo = HostRegistry.INSTANCE.find( getHost( world , pos ) );
        if( hostInfo != null )
            hostLightValue = hostInfo.lightLevel();

        return Math.max( hostLightValue , super.getLightValue( state , world , pos ) );
    }

    @Deprecated
    @Override
    public Material getMaterial( IBlockState state )
    {
        // state as cached by the world does not have unlisted property data.
        // However, state from other callers might.
        MetaResourceLocation hostResource = StateUtil.getValue( state , UnlistedPropertyHostRock.PROPERTY , null );
        if( hostResource == null )
            return super.getMaterial( state );

        IHostInfo hostInfo = HostRegistry.INSTANCE.find( hostResource );
        return hostInfo != null ? hostInfo.material() : super.getMaterial( state );
    }

    @Override
    public EnumPushReaction getMobilityFlag( IBlockState state )
    {
        // BUG ABUSE: The check for a tile entity should probably come first in BlockPistonBase.canPush(),
        // but since it doesn't, we can use PUSH_ONLY to return true. The tile entity can be destroyed
        // without consequence because we juggle unlisted properties from getActualState() back to createTileEntity().
        // Although we could examine the stack in hasTileEntity() and return false if pistons are involved,
        // that method is called too often from elsewhere to risk a performance hit at this time.
        // Pushing ores is already a huge success.
        return EnumPushReaction.PUSH_ONLY;
    }

    @Override
    protected ItemStack getSilkTouchDrop( IBlockState state )
    {
        // 1. This block drops multiple items
        // 2. getSilkTouchDrop() is protected on the proxy block in this scope (so we can't call the proxy because Java)
        // 3. Nothing should be calling this method anyway
        throw new NotImplementedException( "Not implemented for multiple reasons. Consult the source for details." );
    }

    @Override
    public float getSlipperiness( IBlockState state , IBlockAccess world , BlockPos pos , @Nullable Entity entity )
    {
        IHostInfo hostProperties = HostRegistry.INSTANCE.find( getHost( world , pos ) );
        return hostProperties != null && hostProperties.slipperiness() != null
            ? hostProperties.slipperiness()
            : super.getSlipperiness( state , world , pos , entity );
    }

    @Deprecated
    public SoundType getSoundType()
    {
        IBlockState proxyBlockState = oreInfo.proxyBlockState();
        return proxyBlockState != null
            ? proxyBlockState.getBlock().getSoundType()
            : oreInfo.soundType();
    }

    @Override
    public SoundType getSoundType( IBlockState state , World world , BlockPos pos , @Nullable Entity entity )
    {
        if( RANDOM.nextBoolean() )
        {
            IHostInfo hostInfo = HostRegistry.INSTANCE.find( getHost( world , pos ) );
            if( hostInfo != null )
            {
                MetaResourceLocation host = StateUtil.getValue( state , world , pos , UnlistedPropertyHostRock.PROPERTY , UnlistedPropertyHostRock.DEFAULT );
                Block hostBlock = Block.REGISTRY.getObject( host.resourceLocation );
                return hostBlock.getSoundType( state , world , pos , null );
            }
        }

        IBlockState proxyBlockState = oreInfo.proxyBlockState();
        return proxyBlockState != null
            ? proxyBlockState.getBlock().getSoundType( oreInfo.proxyBlockState() , world , pos , entity )
            : oreInfo.soundType();
    }

    @Override
    public void harvestBlock( World worldIn , EntityPlayer player , BlockPos pos , IBlockState state , @Nullable TileEntity te , ItemStack stack )
    {
        // Most of this method is unfortunately copied from Block.harvestBlock().
        // Fun Fact: In 1.12.2, chopping any kind of wood log shows up in the statistics page as oak because the
        // unlocalized name is the same for all wooden log blocks. The items are localized correctly, of course.
        StatBase blockStats = StatList.getBlockStats( this );
        if( blockStats != null )
            player.addStat( blockStats );
        player.addExhaustion( 0.005f ); // Taken from Block.harvestBlock()

        try
        {
            // Silk touch involves a separate code path in Block.harvestBlock()
            // which does not support dropping multiple items. Capture the tool here
            // and bypass that logic for getDrops() to figure everything out.
            harvestTool.set( stack );
            harvesters.set( player );
            int fortuneLevel = EnchantmentHelper.getEnchantmentLevel( Enchantments.FORTUNE , stack );
            dropBlockAsItem( worldIn , pos , state , fortuneLevel );

            MetaResourceLocation host = StateUtil.getValue( state , worldIn , pos , UnlistedPropertyHostRock.PROPERTY , UnlistedPropertyHostRock.DEFAULT );
            IHostInfo hostInfo = HostRegistry.INSTANCE.find( host );
            if( hostInfo == null )
                return;

            Block hostBlock = Block.REGISTRY.getObject( host.resourceLocation );
            IBlockState hostBlockState = hostBlock.getStateFromMeta( host.meta );
            // Flags 1 and 2 would normally be used to cause a block update and send it to clients.
            // Instead, we use flags 4 and 16 to suppress additional notifications.
            // The host will not exist in the world long enough for anything to react to it.
            worldIn.setBlockState( pos , hostBlockState , 16 | 4 );
            hostBlock.harvestBlock( worldIn , player , pos , hostBlockState , te , stack );

            // Set us to air if harvesting the host did not change the block again
            if( hostBlockState.equals( worldIn.getBlockState( pos ) ) )
                worldIn.setBlockToAir( pos );
        }
        finally
        {
            harvesters.set( null );
            harvestTool.remove();
        }
    }

    @Override
    public boolean hasTileEntity( IBlockState state )
    {
        return true;
    }

    @Override
    public boolean isFireSource( World world , BlockPos pos , EnumFacing side )
    {
        IBlockState proxyBlockState = oreInfo.proxyBlockState();
        if( proxyBlockState != null && proxyBlockState.getBlock().isFireSource( world , pos , side ) )
            return true;

        MetaResourceLocation hostResourceLocation = getHost( world , pos );
        Block host = Block.REGISTRY.getObject( hostResourceLocation.resourceLocation );
        if( host != Blocks.AIR && host.isFireSource( world , pos , side ) )
            return true;

        return ( oreInfo.specialBlockPropertyFlags() & SpecialBlockPropertyFlags.FIRE_SOURCE ) > 0
            || super.isFireSource( world , pos , side );
    }

    @Override
    public boolean isToolEffective( String type , IBlockState state )
    {
        // Assume the best case scenario because state is unlikely to have unlisted property data.
        // Let PlayerEvent.BreakSpeed figure it out.
        return true;
    }

    @Override
    public void neighborChanged( IBlockState state , World worldIn , BlockPos pos , Block blockIn , BlockPos fromPos )
    {
        // Don't call super to schedule an update. Wait for updateTick().

        if( worldIn.isRemote )
            return;

        OreBlockTileEntity tileEntity = getTileEntity( worldIn , pos );
        if( tileEntity.getHostRock() != UnlistedPropertyHostRock.DEFAULT )
            return;

        IBlockState changedState = worldIn.getBlockState( fromPos );
        if( changedState.getBlock() == Blocks.AIR )
            return;

        if( changedState.getBlock() instanceof OreBlock )
            worldIn.scheduleBlockUpdate( pos , state.getBlock() , 20 , 10 );
    }

    @Override
    public void onBlockAdded( World worldIn , BlockPos pos , IBlockState state )
    {
        // Don't call super. Wait for updateTick().
    }

    @Override
    public boolean removedByPlayer( IBlockState state , World world , BlockPos pos , EntityPlayer player , boolean willHarvest )
    {
        // See BlockFlowerPot for details about this logic.
        // tldr: If it will harvest, delay deletion of the block until after harvestBlock.
        // BlockFlowerPot says getDrops(), but that does not appear to be called.
        return willHarvest || super.removedByPlayer( state , world , pos , player , willHarvest );
    }

    @Override
    public void updateTick( World worldIn , BlockPos pos , IBlockState state , Random rand )
    {
        // Do not call super.updateTick() so the ore block will not fall. The idea is the ore provides
        // the "structure" to remain suspended. BlockFalling.checkFallable() is private for no good reason.
        // TODO: Revisit this should sandy ores be added.

        if( !worldIn.isRemote )
        {
            OreBlockTileEntity tileEntity = getTileEntity( worldIn , pos );
            if( tileEntity != null )
                tileEntity.searchForAdjacentHostRock();
        }
    }
}
