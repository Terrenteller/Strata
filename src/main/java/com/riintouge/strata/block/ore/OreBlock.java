package com.riintouge.strata.block.ore;

import com.riintouge.strata.*;
import com.riintouge.strata.block.*;
import com.riintouge.strata.block.geo.HostRegistry;
import com.riintouge.strata.block.geo.IGenericBlockProperties;
import com.riintouge.strata.util.StateUtil;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.property.IExtendedBlockState;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Random;

public class OreBlock extends Block
{
    // Ore blocks and their item blocks must have a slightly modified name
    // so the ore items can have the name of the ore without decoration.
    public static final String RegistryNameSuffix = "_ore";

    protected IOreInfo oreInfo;

    public OreBlock( IOreInfo oreInfo )
    {
        super( oreInfo.material() );
        this.oreInfo = oreInfo;

        // TODO: Do NOT set the registry name here because it cannot then be set by derivations or new instances!
        setRegistryName( Strata.modid + ":" + oreInfo.oreName() + RegistryNameSuffix );
        if( oreInfo.proxyBlock() != null )
            setUnlocalizedName( oreInfo.proxyBlock().getUnlocalizedName() );
        else
            setUnlocalizedName( Strata.modid + ":" + oreInfo.oreName() );

        setHarvestLevel( oreInfo.harvestTool() , oreInfo.harvestLevel() );
        setSoundType( oreInfo.soundType() );
        setHardness( oreInfo.hardness() );
        setResistance( oreInfo.explosionResistance() );

        setCreativeTab( Strata.ORE_BLOCK_TAB );
    }

    protected MetaResourceLocation getHost( World world , BlockPos pos )
    {
        TileEntity entity = world.getTileEntity( pos );
        return entity instanceof OreBlockTileEntity
            ? ( (OreBlockTileEntity)entity ).getCachedHost()
            : UnlistedPropertyHostRock.DEFAULT;
    }

    // Block overrides

    @Override
    public boolean canHarvestBlock( IBlockAccess world , BlockPos pos , EntityPlayer player )
    {
        IBlockState state = getExtendedState( world.getBlockState( pos ) , world , pos );
        return canHarvestBlock( state , player , world , pos );
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
    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer.Builder( this )
            .add( UnlistedPropertyHostRock.PROPERTY )
            .build();
    }

    @Override
    public TileEntity createTileEntity( World world , IBlockState state )
    {
        return new OreBlockTileEntity();
    }

    // This is passed to getHarvestLevel and getHarvestTool by ForgeHooks.canHarvestBlock
    /*
    @Deprecated
    @Override
    public IBlockState getActualState( IBlockState state , IBlockAccess worldIn , BlockPos pos )
    {
        // HACK HACK HACK
        // Returning getExtendedState would likely work fine if the try/catch in the
        // WorldType.DEBUG_ALL_BLOCK_STATES check in BlockRendererDispatcher.renderBlock didn't run when it shouldn't.
        // It appears the check is incorrectly inverted. If we modify the state here, BlockModelShapes.getModelForState
        // returns null and results in a missing model model without warnings or errors.
        // Why do unlisted properties affect this? Would an IStateMapper help?
        StackTraceElement[] stackTraceElements = new Throwable().getStackTrace();
        return stackTraceElements[ 3 ].toString().contains( "ForgeHooks" )
            ? getExtendedState( state , worldIn , pos )
            : state;
    }
    */

    @Deprecated
    @Override
    public float getBlockHardness( IBlockState blockState , World worldIn , BlockPos pos )
    {
        MetaResourceLocation hostResource = getHost( worldIn , pos );
        IGenericBlockProperties hostProperties = HostRegistry.INSTANCE.find( hostResource );
        return hostProperties != null ? hostProperties.hardness() + 1.5f : oreInfo.hardness();
    }

    @Override
    public void getDrops( NonNullList< ItemStack > drops , IBlockAccess world , BlockPos pos , IBlockState state , int fortune )
    {
        MetaResourceLocation host = StateUtil.getValue( state , world , pos , UnlistedPropertyHostRock.PROPERTY , UnlistedPropertyHostRock.DEFAULT );
        Block hostBlock = Block.REGISTRY.getObject( host.resourceLocation );
        // FIXME: getStateFromMeta is deprecated. What are we meant to use?
        hostBlock.getDrops( drops , world , pos , hostBlock.getStateFromMeta( host.meta ) , fortune );

        IOreTileSet oreTileSet = OreRegistry.INSTANCE.find( oreInfo.oreName() );
        IOreInfo oreInfo = oreTileSet.getInfo();
        if( oreInfo.proxyBlock() != null )
        {
            oreInfo.proxyBlock().getDrops( drops , world , pos , state , fortune );
        }
        else
        {
            int fortuneBonus = fortune > 0 ? RANDOM.nextInt( fortune + 1 ) : 0;
            drops.add( new ItemStack( oreTileSet.getItem() , 1 + fortuneBonus ) );
        }
    }

    @Override
    public int getExpDrop( IBlockState state , IBlockAccess world , BlockPos pos , int fortune )
    {
        return oreInfo.proxyBlock() != null
            ? oreInfo.proxyBlock().getExpDrop( state , world , pos , fortune )
            : 0;
    }

    @Override
    public IBlockState getExtendedState( IBlockState state , IBlockAccess world , BlockPos pos )
    {
        IExtendedBlockState extendedState = (IExtendedBlockState)state;
        TileEntity entity = world.getTileEntity( pos );
        if( entity instanceof OreBlockTileEntity )
        {
            MetaResourceLocation cachedHost = ( (OreBlockTileEntity)entity ).getCachedHost();
            extendedState = extendedState.withProperty( UnlistedPropertyHostRock.PROPERTY , cachedHost );
        }

        return extendedState;
    }

    @Override
    public int getHarvestLevel( IBlockState state )
    {
        MetaResourceLocation hostResource = StateUtil.getValue( state , UnlistedPropertyHostRock.PROPERTY , UnlistedPropertyHostRock.DEFAULT );
        IGenericBlockProperties hostProperties = HostRegistry.INSTANCE.find( hostResource );
        return hostProperties != null ? hostProperties.harvestLevel() : super.getHarvestLevel( state );
    }

    @Nullable
    @Override
    public String getHarvestTool( IBlockState state )
    {
        MetaResourceLocation hostResource = StateUtil.getValue( state , UnlistedPropertyHostRock.PROPERTY , UnlistedPropertyHostRock.DEFAULT );
        IGenericBlockProperties hostProperties = HostRegistry.INSTANCE.find( hostResource );
        return hostProperties != null ? hostProperties.harvestTool() : super.getHarvestTool( state );
    }

    @Override
    public Item getItemDropped( IBlockState state , Random rand , int fortune )
    {
        return net.minecraft.init.Items.AIR;
    }

    @Deprecated
    @Override
    public Material getMaterial( IBlockState state )
    {
        MetaResourceLocation hostResource = StateUtil.getValue( state , UnlistedPropertyHostRock.PROPERTY , UnlistedPropertyHostRock.DEFAULT );
        IGenericBlockProperties hostProperties = HostRegistry.INSTANCE.find( hostResource );
        return hostProperties != null ? hostProperties.material() : super.getMaterial( state );
    }

    @Deprecated
    @Override
    public float getPlayerRelativeBlockHardness( IBlockState state , EntityPlayer player , World worldIn , BlockPos pos )
    {
        return blockStrength( getExtendedState( state , worldIn , pos ) , player , worldIn , pos );
    }

    @Override
    public SoundType getSoundType( IBlockState state , World world , BlockPos pos , @Nullable Entity entity )
    {
        MetaResourceLocation hostResource = getHost( world , pos );
        IGenericBlockProperties hostProperties = HostRegistry.INSTANCE.find( hostResource );
        return hostProperties != null ? hostProperties.soundType() : oreInfo.soundType();
    }

    @Override
    public void harvestBlock( World world , EntityPlayer player , BlockPos pos , IBlockState state , @Nullable TileEntity entity , ItemStack tool )
    {
        super.harvestBlock( world , player , pos , state , entity , tool );

        world.setBlockToAir( pos );
    }

    @Override
    public boolean hasTileEntity( IBlockState state )
    {
        return true;
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
    public void updateTick( World world , BlockPos pos , IBlockState state , Random rand )
    {
        super.updateTick( world , pos , state , rand );

        // Only the server should poll
        if( !world.isRemote )
            ( (OreBlockTileEntity)world.getTileEntity( pos ) ).pollHost();
    }

    // Statics

    // canHarvestBlock and blockStrength originated from ForgeHooks.java (Forge 1.12.2-14.23.4.2705).
    // Originally, canHarvestBlock re-acquired a block state from getActualState instead of using whatever
    // getPlayerRelativeBlockHardness passed to blockStrength. Unfortunately, returning getExtendedState from an
    // overridden getActualState caused BlockRendererDispatcher.renderBlock to draw a missing model model due to a
    // cache miss in BlockModelShapes.getModelForState. Making minor tweaks in copied code seemed less of a hack
    // than basing the returned state on the caller of getActualState. This should be re-examined and cleaned up
    // in a future version. getActualState is deprecated now and removed in 1.13.
    // Modifications to these two methods are kept to a minimum for diff purposes.

    private static boolean canHarvestBlock( @Nonnull IBlockState state, @Nonnull EntityPlayer player, @Nonnull IBlockAccess world, @Nonnull BlockPos pos)
    {
        if (state.getMaterial().isToolNotRequired())
        {
            System.out.println( "Tool not required" );
            return true;
        }

        Block block = state.getBlock();
        ItemStack stack = player.getHeldItemMainhand();
        String tool = block.getHarvestTool(state);
        if (stack.isEmpty() || tool == null)
        {
            return player.canHarvestBlock(state);
        }

        int toolLevel = stack.getItem().getHarvestLevel(stack, tool, player, state);
        if (toolLevel < 0)
        {
            return player.canHarvestBlock(state);
        }

        return toolLevel >= block.getHarvestLevel(state);
    }

    private static float blockStrength(@Nonnull IBlockState state, @Nonnull EntityPlayer player, @Nonnull World world, @Nonnull BlockPos pos)
    {
        float hardness = state.getBlockHardness(world, pos);
        if (hardness < 0.0F)
        {
            return 0.0F;
        }

        if (!canHarvestBlock(state, player, world, pos))
        {
            return player.getDigSpeed(state, pos) / hardness / 100F;
        }
        else
        {
            return player.getDigSpeed(state, pos) / hardness / 30F;
        }
    }
}
