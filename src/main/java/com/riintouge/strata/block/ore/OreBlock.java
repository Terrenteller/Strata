package com.riintouge.strata.block.ore;

import com.riintouge.strata.Strata;
import com.riintouge.strata.block.MetaResourceLocation;
import com.riintouge.strata.block.geo.HostRegistry;
import com.riintouge.strata.block.geo.IGenericBlockProperties;
import com.riintouge.strata.misc.InitializedThreadLocal;
import com.riintouge.strata.util.StateUtil;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.property.IExtendedBlockState;
import org.apache.commons.lang3.NotImplementedException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

public class OreBlock extends Block
{
    // Ore blocks and their item blocks must have a slightly different registry name
    // so the ore items can have the name of the ore without decoration.
    public static final String RegistryNameSuffix = "_ore";
    protected static InitializedThreadLocal< ItemStack > harvestTool = new InitializedThreadLocal<>( ItemStack.EMPTY );

    protected IOreInfo oreInfo;

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

    protected MetaResourceLocation getHost( IBlockAccess world , BlockPos pos )
    {
        return getTileEntity( world , pos ).getHostRock();
    }

    protected OreBlockTileEntity getTileEntity( IBlockAccess world , BlockPos pos )
    {
        return (OreBlockTileEntity)world.getTileEntity( pos );
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
        return new OreBlockTileEntity();
    }

    // This is passed to getHarvestLevel and getHarvestTool by ForgeHooks.canHarvestBlock
    /*
    @Deprecated
    @Override
    public IBlockState getActualState( IBlockState state , IBlockAccess worldIn , BlockPos pos )
    {
        // HACK HACK HACK
        // Returning getExtendedState would likely work fine if the try/catch in the WorldType.DEBUG_ALL_BLOCK_STATES
        // check in BlockRendererDispatcher.renderBlock() didn't run when it shouldn't. It appears the check is
        // incorrectly inverted. If we modify the state here, BlockModelShapes.getModelForState() returns null and
        // results in a missing model model without warnings or errors. Why do unlisted properties affect this?
        // Would an IStateMapper help?
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
        IGenericBlockProperties hostProperties = HostRegistry.INSTANCE.find( getHost( worldIn , pos ) );
        return hostProperties != null ? hostProperties.hardness() + 1.5f : oreInfo.hardness();
    }

    @Override
    public void getDrops( NonNullList< ItemStack > drops , IBlockAccess world , BlockPos pos , IBlockState state , int fortune )
    {
        MetaResourceLocation host = StateUtil.getValue( state , world , pos , UnlistedPropertyHostRock.PROPERTY , UnlistedPropertyHostRock.DEFAULT );
        Block hostBlock = Block.REGISTRY.getObject( host.resourceLocation );
        IOreTileSet oreTileSet = OreRegistry.INSTANCE.find( oreInfo.oreName() );
        IOreInfo oreInfo = oreTileSet.getInfo();
        IBlockState proxyBlockState = oreInfo.proxyBlockState();
        Block proxyBlock = proxyBlockState != null ? proxyBlockState.getBlock() : null;

        if( EnchantmentHelper.getEnchantmentLevel( Enchantments.SILK_TOUCH , harvestTool.get() ) > 0 )
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
        return getCompleteExtendedState( getTileEntity( world , pos ) , state , world,  pos );
    }

    @Override
    public int getHarvestLevel( IBlockState state )
    {
        // This state has up-to-date, unlisted properties because of the extended state passed
        // to our modified blockStrength() called by getPlayerRelativeBlockHardness()
        MetaResourceLocation hostResource = StateUtil.getValue( state , UnlistedPropertyHostRock.PROPERTY , UnlistedPropertyHostRock.DEFAULT );
        IGenericBlockProperties hostProperties = HostRegistry.INSTANCE.find( hostResource );
        return hostProperties != null ? hostProperties.harvestLevel() : super.getHarvestLevel( state );
    }

    @Nullable
    @Override
    public String getHarvestTool( IBlockState state )
    {
        // This state has up-to-date, unlisted properties because of the extended state passed
        // to our modified blockStrength() called by getPlayerRelativeBlockHardness()
        MetaResourceLocation hostResource = StateUtil.getValue( state , UnlistedPropertyHostRock.PROPERTY , UnlistedPropertyHostRock.DEFAULT );
        IGenericBlockProperties hostProperties = HostRegistry.INSTANCE.find( hostResource );
        return hostProperties != null ? hostProperties.harvestTool() : super.getHarvestTool( state );
    }

    @Override
    public Item getItemDropped( IBlockState state , Random rand , int fortune )
    {
        return Items.AIR;
    }

    @Deprecated
    @Override
    public Material getMaterial( IBlockState state )
    {
        // Because state does not have up-to-date, unlisted property data, we cannot determine the actual material
        return super.getMaterial( state );
    }

    @Deprecated
    @Override
    public float getPlayerRelativeBlockHardness( IBlockState state , EntityPlayer player , World worldIn , BlockPos pos )
    {
        return blockStrength( getExtendedState( state , worldIn , pos ) , player , worldIn , pos );
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
        IGenericBlockProperties hostProperties = HostRegistry.INSTANCE.find( getHost( world , pos ) );
        return hostProperties != null ? hostProperties.soundType() : oreInfo.soundType();
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
            // and bypass that logic for getDrops to figure everything out.
            harvestTool.set( stack );
            harvesters.set( player );
            int fortuneLevel = EnchantmentHelper.getEnchantmentLevel( Enchantments.FORTUNE , stack );
            this.dropBlockAsItem( worldIn , pos , state , fortuneLevel );
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
        super.updateTick( worldIn , pos , state , rand );

        if( !worldIn.isRemote )
            getTileEntity( worldIn , pos ).searchForAdjacentHostRock();
    }

    // Statics

    // canHarvestBlock() and blockStrength() originated from ForgeHooks.java (Forge 1.12.2-14.23.4.2705).
    // Originally, canHarvestBlock() re-acquired a block state from getActualState() instead of using whatever
    // getPlayerRelativeBlockHardness() passed to blockStrength(). Unfortunately, returning getExtendedState() from an
    // overridden getActualState() caused BlockRendererDispatcher.renderBlock() to draw a missing model model due to a
    // cache miss in BlockModelShapes.getModelForState(). Making minor tweaks in copied code seemed less of a hack
    // than basing the returned state on the caller of getActualState(). This should be re-examined and cleaned up
    // in a future version. getActualState() is deprecated now and removed in 1.13.
    // Modifications to these two methods are kept to a minimum for diff purposes.

    private static boolean canHarvestBlock( @Nonnull IBlockState state, @Nonnull EntityPlayer player, @Nonnull IBlockAccess world, @Nonnull BlockPos pos)
    {
        if (state.getMaterial().isToolNotRequired())
        {
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
