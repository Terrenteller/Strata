package com.riintouge.strata.block.ore;

import com.riintouge.strata.Strata;
import com.riintouge.strata.StrataConfig;
import com.riintouge.strata.block.GenericCubeTextureMap;
import com.riintouge.strata.block.MetaResourceLocation;
import com.riintouge.strata.block.geo.HostRegistry;
import com.riintouge.strata.block.geo.IHostInfo;
import com.riintouge.strata.util.StateUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFalling;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.EnumPushReaction;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.particle.ParticleDigging;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.lang3.NotImplementedException;

import javax.annotation.Nullable;
import java.util.Random;
import java.util.Stack;

public class OreBlock extends BlockFalling
{
    // Ore blocks and their item blocks must have a slightly different registry name
    // so the ore items can have the name of the ore without decoration.
    public static final String RegistryNameSuffix = "_ore";
    protected static ThreadLocal< ItemStack > harvestTool = new ThreadLocal<>();

    protected IOreInfo oreInfo;
    protected ThreadLocal< Integer > particleColor = new ThreadLocal<>();

    public OreBlock( IOreInfo oreInfo )
    {
        super( oreInfo.material() );
        this.oreInfo = oreInfo;

        setRegistryName( Strata.modid + ":" + oreInfo.oreName() + RegistryNameSuffix );
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

    protected int calculateBonusExpr( String bonusExpr , int fortune )
    {
        if( bonusExpr != null )
        {
            Stack< Double > bonusStack = new Stack<>();
            try
            {
                for( String bonusToken : bonusExpr.split( " " ) )
                {
                    switch( bonusToken )
                    {
                        case "f":
                            bonusStack.push( (double)fortune );
                            break;
                        case "+":
                            bonusStack.push( bonusStack.pop() + bonusStack.pop() );
                            break;
                        case "-":
                        {
                            double right = bonusStack.pop();
                            bonusStack.push( bonusStack.pop() - right );
                            break;
                        }
                        case "*":
                            bonusStack.push( bonusStack.pop() * bonusStack.pop() );
                            break;
                        case "/":
                        {
                            double right = bonusStack.pop();
                            bonusStack.push( bonusStack.pop() / right );
                            break;
                        }
                        default:
                        {
                            bonusStack.push( Double.parseDouble( bonusToken ) );
                        }
                    }
                }
            }
            catch( Exception e )
            {
                // TODO: warn
                return 0;
            }

            // +1 to offset exclusion
            return RANDOM.nextInt( (int)Math.round( bonusStack.peek() ) + 1 );
        }

        return 0;
    }

    public IExtendedBlockState getCompleteExtendedState( OreBlockTileEntity entity , IBlockState state , IBlockAccess world , BlockPos pos )
    {
        IExtendedBlockState extendedState = getDefaultExtendedState( state );
        return entity != null
            ? extendedState.withProperty( UnlistedPropertyHostRock.PROPERTY , entity.getHostRock() )
            : extendedState;
    }

    public IExtendedBlockState getDefaultExtendedState( IBlockState state )
    {
        return ( (IExtendedBlockState)state )
            .withProperty( UnlistedPropertyHostRock.PROPERTY , UnlistedPropertyHostRock.DEFAULT );
    }

    public MetaResourceLocation getHost( IBlockAccess world , BlockPos pos )
    {
        OreBlockTileEntity tileEntity = getTileEntity( world , pos );
        return tileEntity != null ? tileEntity.getHostRock() : UnlistedPropertyHostRock.DEFAULT;
    }

    public IHostInfo getHostInfo( IBlockState actualState )
    {
        MetaResourceLocation hostResource = StateUtil.getValue( actualState , UnlistedPropertyHostRock.PROPERTY , null );
        return hostResource != null ? HostRegistry.INSTANCE.find( hostResource ) : null;
    }

    public IOreInfo getOreInfo()
    {
        return oreInfo;
    }

    public OreBlockTileEntity getTileEntity( IBlockAccess world , BlockPos pos )
    {
        // This may be called before the tile entity is created
        TileEntity tileEntity = world.getTileEntity( pos );
        return tileEntity instanceof OreBlockTileEntity ? (OreBlockTileEntity) tileEntity : null;
    }

    public boolean isToolActuallyEffective( ItemStack tool , Material hostMaterial , Material oreMaterial )
    {
        // A null ItemStack means getDrops() was called directly, such as by explosion
        if( tool == null )
            return true;

        // A tool can be effective without meeting harvest level requirements
        return tool.getItem() instanceof ItemPickaxe
            ? hostMaterial == Material.ROCK || oreMaterial == Material.ROCK
            : hostMaterial != Material.ROCK;
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
        if( StrataConfig.usePrecomputedOreParticles && OreParticleTextureManager.INSTANCE.isInitialized() )
            return addPrecomputedDestroyEffects( world , pos , manager );

        try
        {
            IBlockState state = world.getBlockState( pos ).getActualState( world , pos );
            int blockId = Block.getIdFromBlock( state.getBlock() );

            MetaResourceLocation host = StateUtil.getValue( state , UnlistedPropertyHostRock.PROPERTY , null );
            IHostInfo hostInfo = HostRegistry.INSTANCE.find( host );
            GenericCubeTextureMap hostTextureMap = hostInfo.modelTextureMap();
            IBakedModel hostModel = HostRegistry.INSTANCE.getBakedModel( host );
            TextureAtlasSprite hostParticleTexture = hostModel.getParticleTexture();

            String oreName = oreInfo.oreName();
            IOreInfo oreInfo = OreRegistry.INSTANCE.find( oreName ).getInfo();
            GenericCubeTextureMap oreTextureMap = oreInfo.modelTextureMap();
            IBakedModel oreModel = OreRegistry.INSTANCE.getBakedModel( oreName );
            TextureAtlasSprite oreParticleTexture = oreModel.getParticleTexture();

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

                            TextureAtlasSprite texture = hostTextureMap != null
                                ? hostTextureMap.getTexture( EnumFacing.VALUES[ RANDOM.nextInt( 6 ) ] )
                                : hostParticleTexture;

                            particleDigging.setBlockPos( pos ).setParticleTexture( texture );
                            manager.addEffect( particleDigging );
                        }

                        // ...and ore particles in-between
                        if( x != 3 && y != 3 && z != 3 )
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

                            TextureAtlasSprite texture = oreTextureMap != null
                                    ? oreTextureMap.getTexture( EnumFacing.VALUES[ RANDOM.nextInt( 6 ) ] )
                                    : oreParticleTexture;

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
            String hostResourceDomain = host.resourceLocation.getResourceDomain();
            String hostResourceLocation = host.resourceLocation.getResourcePath();

            for( EnumFacing facing : EnumFacing.values() )
            {
                baseTextures[ facing.ordinal() ] = OreParticleTextureManager.INSTANCE.findTexture(
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

                        ParticleDigging particleDigging = (ParticleDigging) new ParticleDigging.Factory().createParticle(
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

    @Override
    @SideOnly( Side.CLIENT )
    public boolean addHitEffects( IBlockState state , World worldObj , RayTraceResult target , ParticleManager manager )
    {
        if( StrataConfig.usePrecomputedOreParticles && OreParticleTextureManager.INSTANCE.isInitialized() )
            return addPrecomputedHitEffects( state , worldObj , target , manager );

        try
        {
            BlockPos blockPos = target.getBlockPos();
            IBlockState actualState = worldObj.getBlockState( blockPos ).getActualState( worldObj , blockPos );
            int blockId = Block.getIdFromBlock( actualState.getBlock() );

            // This logic sampled from ParticleManager.addBlockHitEffects()
            double x = (double)blockPos.getX();
            double y = (double)blockPos.getY();
            double z = (double)blockPos.getZ();
            AxisAlignedBB AABB = actualState.getBoundingBox( worldObj , blockPos );
            double d0 = x + RANDOM.nextDouble() * ( AABB.maxX - AABB.minX - 0.2d ) + 0.1d + AABB.minX;
            double d1 = y + RANDOM.nextDouble() * ( AABB.maxY - AABB.minY - 0.2d ) + 0.1d + AABB.minY;
            double d2 = z + RANDOM.nextDouble() * ( AABB.maxZ - AABB.minZ - 0.2d ) + 0.1d + AABB.minZ;

            switch( target.sideHit.getIndex() )
            {
                case 0: // DOWN
                    d1 = y + AABB.minY - 0.1d;
                    break;
                case 1: // UP
                    d1 = y + AABB.maxY + 0.1d;
                    break;
                case 2: // NORTH
                    d2 = z + AABB.minZ - 0.1d;
                    break;
                case 3: // SOUTH
                    d2 = z + AABB.maxZ + 0.1d;
                    break;
                case 4: // WEST
                    d0 = x + AABB.minX - 0.1d;
                    break;
                case 5: // EAST
                    d0 = x + AABB.maxX + 0.1d;
                    break;
                default:
                    return false;
            }

            ParticleDigging particleDigging = (ParticleDigging)new ParticleDigging.Factory().createParticle(
                0, // unused
                worldObj,
                d0,
                d1,
                d2,
                0.0d,
                0.0d,
                0.0d,
                blockId );

            TextureAtlasSprite texture = null;
            if( RANDOM.nextBoolean() )
            {
                MetaResourceLocation host = StateUtil.getValue( actualState , UnlistedPropertyHostRock.PROPERTY , null );
                IHostInfo hostInfo = HostRegistry.INSTANCE.find( host );
                GenericCubeTextureMap hostTextureMap = hostInfo.modelTextureMap();
                texture = hostTextureMap != null
                    ? hostTextureMap.getTexture( target.sideHit )
                    : HostRegistry.INSTANCE.getBakedModel( host ).getParticleTexture();
            }
            else
            {
                String oreName = oreInfo.oreName();
                IOreInfo oreInfo = OreRegistry.INSTANCE.find( oreName ).getInfo();
                GenericCubeTextureMap oreTextureMap = oreInfo.modelTextureMap();
                texture = oreTextureMap != null
                    ? oreTextureMap.getTexture( target.sideHit )
                    : OreRegistry.INSTANCE.getBakedModel( oreName ).getParticleTexture();
            }

            particleDigging
                .setBlockPos( blockPos )
                .multiplyVelocity( 0.2f )
                .multipleParticleScaleBy( 0.6f )
                .setParticleTexture( texture );

            manager.addEffect( particleDigging );
            return true;
        }
        catch( Exception e )
        {
            // TODO: warn
        }

        return false;
    }

    @SideOnly( Side.CLIENT )
    protected boolean addPrecomputedHitEffects( IBlockState state , World worldObj , RayTraceResult target , ParticleManager manager )
    {
        try
        {
            BlockPos blockPos = target.getBlockPos();
            IBlockState actualState = worldObj.getBlockState( blockPos ).getActualState( worldObj , blockPos );
            int blockId = Block.getIdFromBlock( actualState.getBlock() );

            String oreName = oreInfo.oreName();
            MetaResourceLocation host = StateUtil.getValue( actualState , UnlistedPropertyHostRock.PROPERTY , null );
            String hostResourceDomain = host.resourceLocation.getResourceDomain();
            String hostResourceLocation = host.resourceLocation.getResourcePath();

            // This logic sampled from ParticleManager.addBlockHitEffects()
            double x = (double)blockPos.getX();
            double y = (double)blockPos.getY();
            double z = (double)blockPos.getZ();
            AxisAlignedBB AABB = actualState.getBoundingBox( worldObj , blockPos );
            double d0 = x + RANDOM.nextDouble() * ( AABB.maxX - AABB.minX - 0.2d ) + 0.1d + AABB.minX;
            double d1 = y + RANDOM.nextDouble() * ( AABB.maxY - AABB.minY - 0.2d ) + 0.1d + AABB.minY;
            double d2 = z + RANDOM.nextDouble() * ( AABB.maxZ - AABB.minZ - 0.2d ) + 0.1d + AABB.minZ;

            switch( target.sideHit.getIndex() )
            {
                case 0: // DOWN
                    d1 = y + AABB.minY - 0.1d;
                    break;
                case 1: // UP
                    d1 = y + AABB.maxY + 0.1d;
                    break;
                case 2: // NORTH
                    d2 = z + AABB.minZ - 0.1d;
                    break;
                case 3: // SOUTH
                    d2 = z + AABB.maxZ + 0.1d;
                    break;
                case 4: // WEST
                    d0 = x + AABB.minX - 0.1d;
                    break;
                case 5: // EAST
                    d0 = x + AABB.maxX + 0.1d;
                    break;
                default:
                    return false;
            }

            ParticleDigging particleDigging = (ParticleDigging)new ParticleDigging.Factory().createParticle(
                0, // unused
                worldObj,
                d0,
                d1,
                d2,
                0.0d,
                0.0d,
                0.0d,
                blockId );

            TextureAtlasSprite texture = OreParticleTextureManager.INSTANCE.findTexture(
                oreName,
                hostResourceDomain,
                hostResourceLocation,
                host.meta,
                target.sideHit );

            particleDigging
                .setBlockPos( blockPos )
                .multiplyVelocity( 0.2f )
                .multipleParticleScaleBy( 0.6f )
                .setParticleTexture( texture );

            manager.addEffect( particleDigging );
            return true;
        }
        catch( Exception e )
        {
            // TODO: warn
        }

        return false;
    }

    @Override
    protected boolean canSilkHarvest()
    {
        throw new NotImplementedException( "Use the state-sensitive overload instead!" );
    }

    @Override
    public boolean canSilkHarvest( World world , BlockPos pos , IBlockState state , EntityPlayer player )
    {
        IOreTileSet oreTileSet = OreRegistry.INSTANCE.find( oreInfo.oreName() );
        IOreInfo oreInfo = oreTileSet.getInfo();
        IBlockState proxyBlockState = oreInfo.proxyBlockState();

        // The consequences of silk touch only applies to proxy blocks since we should never drop ourself as an item
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

        IOreTileSet oreTileSet = OreRegistry.INSTANCE.find( oreInfo.oreName() );
        IOreInfo oreInfo = oreTileSet.getInfo();
        IBlockState proxyBlockState = oreInfo.proxyBlockState();
        if( proxyBlockState != null )
        {
            if( proxyBlockState.getBlock().canSustainPlant( proxyBlockState , world , pos , direction , plantable ) )
                return true;
        }

        return super.canSustainPlant( state , world , pos , direction , plantable );
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

    @Deprecated
    @Override
    public IBlockState getActualState( IBlockState state , IBlockAccess worldIn , BlockPos pos )
    {
        // BlockRendererDispatcher.renderBlock() calls BlockModelShapes.getModelForState()
        // which queries an IdentityHashMap populated by valid states of the block. Because this block has no metadata
        // properties, only unlisted, there is only one valid state: the unmodified default.

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
        IHostInfo hostProperties = HostRegistry.INSTANCE.find( getHost( worldIn , pos ) );
        return hostProperties != null ? hostProperties.hardness() + 1.5f : oreInfo.hardness();
    }

    @Override
    public void getDrops( NonNullList< ItemStack > drops , IBlockAccess world , BlockPos pos , IBlockState state , int fortune )
    {
        MetaResourceLocation host = StateUtil.getValue( state , world , pos , UnlistedPropertyHostRock.PROPERTY , UnlistedPropertyHostRock.DEFAULT );
        IHostInfo hostInfo = HostRegistry.INSTANCE.find( host );
        if( !isToolActuallyEffective( harvestTool.get() , hostInfo.material() , oreInfo.material() ) )
            return;

        ItemStack harvestToolOrEmpty = harvestTool.get() != null ? harvestTool.get() : ItemStack.EMPTY;
        Block hostBlock = host != null ? Block.REGISTRY.getObject( host.resourceLocation ) : null;
        IOreTileSet oreTileSet = OreRegistry.INSTANCE.find( oreInfo.oreName() );
        IOreInfo oreInfo = oreTileSet.getInfo();
        IBlockState proxyBlockState = oreInfo.proxyBlockState();
        Block proxyBlock = proxyBlockState != null ? proxyBlockState.getBlock() : null;

        if( EnchantmentHelper.getEnchantmentLevel( Enchantments.SILK_TOUCH , harvestToolOrEmpty ) > 0 )
        {
            drops.add( new ItemStack( hostBlock ) );
            if( proxyBlock != null )
                drops.add( new ItemStack( proxyBlock , 1 , proxyBlock.damageDropped( proxyBlockState ) ) );
        }
        else
        {
            hostBlock.getDrops( drops , world , pos , hostBlock.getStateFromMeta( host.meta ) , fortune );
            if( proxyBlock != null )
                proxyBlock.getDrops( drops , world , pos , proxyBlockState , fortune );
        }

        if( proxyBlock == null )
        {
            int bonus = calculateBonusExpr( oreInfo.bonusDropExpr() , fortune );
            int dropCount = Math.max( 0 , oreInfo.baseDropAmount() + bonus );
            if( dropCount > 0 )
            {
                Item oreItem = oreTileSet.getItem();
                drops.add( new ItemStack( oreItem , Math.min( oreItem.getItemStackLimit() , dropCount ) ) );
            }
        }
    }

    @Override
    public int getExpDrop( IBlockState state , IBlockAccess world , BlockPos pos , int fortune )
    {
        IBlockState proxyBlockState = oreInfo.proxyBlockState();
        return proxyBlockState != null
            ? proxyBlockState.getBlock().getExpDrop( proxyBlockState , world , pos , fortune )
            : oreInfo.baseExp() + calculateBonusExpr( oreInfo.bonusExpExpr() , fortune );
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
        IHostInfo hostProperties = hostResource != null ? HostRegistry.INSTANCE.find( hostResource ) : null;
        return hostProperties != null ? hostProperties.harvestLevel() : oreInfo.harvestLevel();
    }

    @Nullable
    @Override
    public String getHarvestTool( IBlockState state )
    {
        // state is expected to have unlisted properties from ForgeHooks.canHarvestBlock()
        MetaResourceLocation hostResource = StateUtil.getValue( state , UnlistedPropertyHostRock.PROPERTY , null );
        IHostInfo hostProperties = hostResource != null ? HostRegistry.INSTANCE.find( hostResource ) : null;
        return hostProperties != null ? hostProperties.harvestTool() : oreInfo.harvestTool();
    }

    @Override
    public Item getItemDropped( IBlockState state , Random rand , int fortune )
    {
        return Items.AIR;
    }

    @Override
    public String getLocalizedName()
    {
        return oreInfo.localizedName();
    }

    @Deprecated
    @Override
    public Material getMaterial( IBlockState state )
    {
        // state as cached by the world does not have unlisted property data
        MetaResourceLocation hostResource = StateUtil.getValue( state , UnlistedPropertyHostRock.PROPERTY , null );
        if( hostResource == null )
            return super.getMaterial( state );

        // However, state from other callers might. Be aware of the consequences.
        IHostInfo hostProperties = HostRegistry.INSTANCE.find( hostResource );
        return hostProperties != null ? hostProperties.material() : super.getMaterial( state );
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
        // 2. getSilkTouchDrop() is protected on the proxy block in this scope
        // 3. Nothing should be calling this method anyway
        throw new NotImplementedException( "Not implemented for multiple reasons. Consult the source for details." );
    }

    @Override
    public SoundType getSoundType( IBlockState state , World world , BlockPos pos , @Nullable Entity entity )
    {
        IHostInfo hostProperties = HostRegistry.INSTANCE.find( getHost( world , pos ) );
        return hostProperties != null ?
            ( RANDOM.nextBoolean() ? hostProperties.soundType() : oreInfo.soundType() )
            : oreInfo.soundType();
    }

    @Override
    public void harvestBlock( World worldIn , EntityPlayer player , BlockPos pos , IBlockState state , @Nullable TileEntity te , ItemStack stack )
    {
        // Most of this method is unfortunately copied from Block.harvestBlock().
        // Fun Fact: In 1.12.2, chopping any kind of wood log shows up in the statistics page as oak because the
        // unlocalized name is the same for all wooden log blocks. The items are localized correctly, of course.
        player.addStat( StatList.getBlockStats( this ) );
        player.addExhaustion( 0.005F );

        try
        {
            // Silk touch involves a separate code path in Block.harvestBlock()
            // which does not support dropping multiple items. Capture the tool here
            // and bypass that logic for getDrops() to figure everything out.
            harvestTool.set( stack );
            harvesters.set( player );
            int fortuneLevel = EnchantmentHelper.getEnchantmentLevel( Enchantments.FORTUNE , stack );
            dropBlockAsItem( worldIn , pos , state , fortuneLevel );
        }
        finally
        {
            harvesters.set( null );
            harvestTool.remove();
        }

        worldIn.setBlockToAir( pos );
    }

    @Override
    public boolean hasTileEntity( IBlockState state )
    {
        return true;
    }

    @Override
    public boolean isToolEffective( String type , IBlockState state )
    {
        // Assume the best case scenario because state is unlikely to have unlisted property data.
        // Strata will apply a penalty to PlayerEvent.BreakSpeed where appropriate.
        return true;
    }

    @Override
    public void neighborChanged( IBlockState state , World worldIn , BlockPos pos , Block blockIn , BlockPos fromPos )
    {
        // Don't call super. Wait for updateTick().
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
        // BlockFlowerPot says getDrops, but that does not appear to be called.
        return willHarvest || super.removedByPlayer( state , world , pos , player , willHarvest );
    }

    @Override
    public void updateTick( World worldIn , BlockPos pos , IBlockState state , Random rand )
    {
        // Do not call super.updateTick() so the ore block will not fall. The idea is the ore provides
        // the "structure" to remain suspended. BlockFalling.checkFallable() is private for no good reason.
        // Why BlockFalling then? It provides enough useful infrastructure.
        // TODO: Revisit this should sandy ores be added.

        if( !worldIn.isRemote )
        {
            OreBlockTileEntity tileEntity = getTileEntity( worldIn , pos );
            if( tileEntity != null )
                tileEntity.searchForAdjacentHostRock();
        }
    }
}
