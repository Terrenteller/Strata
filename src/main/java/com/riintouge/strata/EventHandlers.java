package com.riintouge.strata;

import com.riintouge.strata.block.geo.GeoBlock;
import com.riintouge.strata.block.geo.IHostInfo;
import com.riintouge.strata.block.ore.IOreInfo;
import com.riintouge.strata.block.ore.OreBlock;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlockSpecial;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@EventBusSubscriber
public class EventHandlers
{
    public static final float unharvestablePenalty = 30f / 100f; // Derived from ForgeHooks.blockStrength()
    public static final float halfUnharvestablePenalty = unharvestablePenalty + ( ( 1.0f - unharvestablePenalty ) * 0.5f );

    /*
    @SubscribeEvent( priority = EventPriority.LOWEST )
    public static void blockPlace( BlockEvent.PlaceEvent event )
    {
        // TODO: Determine a use case for this method to be implemented, such as block-placing machines from other mods.

        // This causes the block to momentarily appear and play its placement sound.
        // It also causes client/server item count de-syncronization.
        // Is there a better way to prevent non-player actions from circumventing the checks in playerInteractEvent()?
        event.setCanceled( true );
    }
    */

    @SubscribeEvent( priority = EventPriority.LOWEST )
    public static void breakSpeed( PlayerEvent.BreakSpeed event )
    {
        // When the host material does not require a tool, methods like Block.getHarvestTool()
        // and Block.getHarvestLevel() are skipped. This complicates requirement checks and speed boosts.
        // Instead of having OreBlock gamble on an answer, it always reports true for tool effectiveness
        // and relies on this event handler to do what it cannot. No penalty is applied here for hosts.
        // ForgeHooks.blockStrength() already does that.

        EntityPlayer player = event.getEntityPlayer();
        World world = player.world;
        if( world == null )
            return;

        IBlockState oreBlockState = event.getState();
        Block block = oreBlockState.getBlock();
        if( !( block instanceof OreBlock ) )
            return;

        OreBlock oreBlock = (OreBlock)block;
        oreBlockState = block.getActualState( oreBlockState , world , event.getPos() );
        IHostInfo hostInfo = oreBlock.getHostInfo( oreBlockState );
        if( hostInfo == null )
            return;

        ItemStack tool = player.getHeldItemMainhand();
        Block hostBlock = Block.getBlockFromName( hostInfo.registryName().toString() );
        IBlockState hostBlockState = hostBlock.getStateFromMeta( hostInfo.meta() );
        String hostHarvestTool = hostBlock.getHarvestTool( hostBlockState );
        int hostToolLevel = tool.getItem().getHarvestLevel( tool , hostHarvestTool , player , hostBlockState );
        boolean isToolEffectiveOnHost = hostToolLevel != -1;
        boolean canToolHarvestHost = hostToolLevel >= hostInfo.harvestLevel();

        // Strong stone ore in a weak stone host with a weak pickaxe?
        // Just because the tool can't harvest the host doesn't mean it's not effective (and retains its speed).
        if( !isToolEffectiveOnHost )
        {
            // Stone in stone with a shovel? Clay in sand with an axe?
            // It doesn't work in the base case. Remove the speed boost applied in EntityPlayer.getDigSpeed()
            // because OreBlock.isToolEffective() always returns true.
            event.setNewSpeed( event.getNewSpeed() / getEfficientDestroySpeed( player , oreBlockState ) );
        }

        // No need to penalize an unharvestable host here. ForgeHooks.blockStrength() will do that.
        // Don't stop here either. We may have more penalties to apply.

        // The ore often defers to the host. We need the real (or proxy) info here.
        IOreInfo oreInfo = oreBlock.getOreInfo();
        IBlockState proxyBlockState = oreInfo.proxyBlockState();
        if( proxyBlockState != null )
            oreBlockState = proxyBlockState;

        String oreHarvestTool = proxyBlockState != null
            ? proxyBlockState.getBlock().getHarvestTool( proxyBlockState )
            : oreInfo.harvestTool();
        int oreHarvestLevel = proxyBlockState != null
            ? proxyBlockState.getBlock().getHarvestLevel( proxyBlockState )
            : oreInfo.harvestLevel();
        int oreToolLevel = oreHarvestTool != null
            ? tool.getItem().getHarvestLevel( tool , oreHarvestTool , player , oreBlockState )
            : -1;
        // FIXME: If oreHarvestTool is null, we misinterpret that as the tool being effective
        boolean isToolEffectiveOnOre = oreToolLevel != -1;
        boolean canToolHarvestOre = oreToolLevel >= oreHarvestLevel;

        if( canToolHarvestHost )
        {
            if( canToolHarvestOre )
            {
                // Stone in stone with a pickaxe? Clay in sand with a shovel?
                // It just works.
                return;
            }
            else if( oreInfo.material().isToolNotRequired() )
            {
                // Clay in stone with a pickaxe?
                // It doesn't "just work" but it doesn't meet the bar for a penalty.
                return;
            }
            else if( isToolEffectiveOnOre )
            {
                // Strong stone ore in a weak stone host with a weak pickaxe?
                // A weak harvest penalty will do.
                event.setNewSpeed( event.getNewSpeed() * halfUnharvestablePenalty );
                return;
            }
            else
            {
                // Stone in sand with a shovel?
                // Retain the tool efficiency speed boost but apply a harvest penalty.
                event.setNewSpeed( event.getNewSpeed() * halfUnharvestablePenalty );
                return;
            }
        }
        else if( canToolHarvestOre )
        {
            // Stone in sand with a pickaxe?
            // The ineffective host tool speed nerf above is sufficient.
            return;
        }
        else if( oreInfo.material().isToolNotRequired() )
        {
            // Clay in sand with a pickaxe? Glass in glass with anything?
            // The ineffective host tool speed nerf above is sufficient.
            return;
        }

        // Strong stone in sand with a weak pickaxe? Stone in sand with an axe?
        // Whatever the case, you're going to have a bad time.
        event.setNewSpeed( event.getNewSpeed() * unharvestablePenalty );
    }

    // Taken from EntityPlayer.getDigSpeed()
    public static float getEfficientDestroySpeed( EntityPlayer player , IBlockState state )
    {
        ItemStack tool = player.getHeldItemMainhand();
        float destroySpeed = player.inventory.getDestroySpeed( state );
        if( !tool.isEmpty() && destroySpeed > 1.0f )
        {
            int efficiency = EnchantmentHelper.getEfficiencyModifier( player );
            if( efficiency > 0 )
                destroySpeed += (float)( efficiency * efficiency + 1 );
        }

        return destroySpeed;
    }

    @SubscribeEvent( priority = EventPriority.LOWEST )
    public static void playerInteractEvent( PlayerInteractEvent.RightClickBlock event )
    {
        World world = event.getWorld();
        EnumFacing face = event.getFace();
        if( world == null || face == null )
            return;

        ItemStack itemStack = event.getItemStack();
        if( itemStack.isEmpty() )
            return;

        BlockPos pos = event.getPos();
        IBlockState eventBlockState = world.getBlockState( pos );
        Block eventBlock = eventBlockState.getBlock();
        if( !( eventBlock instanceof GeoBlock || eventBlock instanceof OreBlock ) )
            return;

        // Do we need to be concerned about IGrowable?
        IPlantable plantableItem = null;
        Item item = itemStack.getItem();
        if( item instanceof IPlantable )
            plantableItem = (IPlantable)item;

        Block plantableBlock = null;
        Block itemBlock = Block.getBlockFromItem( item );
        if( itemBlock instanceof IPlantable )
            plantableBlock = itemBlock;
        else
        {
            if( plantableItem != null )
            {
                IBlockState plantableBlockState = plantableItem.getPlant( world , pos.offset( face ) );
                Block plantBlock = plantableBlockState.getBlock();
                if( plantBlock instanceof IPlantable )
                    plantableBlock = plantBlock;
            }

            if( plantableBlock == null && item instanceof ItemBlockSpecial )
            {
                itemBlock = ( (ItemBlockSpecial)item ).getBlock();
                if( itemBlock instanceof IPlantable )
                    plantableBlock = itemBlock;
            }
        }

        try
        {
            // Preempt calls to GeoBlock.canSustainPlant() before World.mayPlace()
            // so we may attempt to override plant sustaining logic.
            GeoBlock.canSustainPlantEventOverride.set( true );
            if( plantableBlock != null && !plantableBlock.canPlaceBlockOnSide( world , pos.offset( face ) , face ) )
                throw new UnsupportedOperationException();

            if( plantableItem != null && !eventBlock.canSustainPlant( eventBlockState , world , pos , face , plantableItem ) )
                throw new UnsupportedOperationException();

            return;
        }
        catch( UnsupportedOperationException e )
        {
            // Unfortunately, our borderline hacks only get us so far. Plantables which turn the check back around on us
            // see through our lies. World.getBlockState() returns cached information about GeoBlock rather than that of
            // a block we defer to. Even if we can override the block state now, we can't during regular update ticks.
            // Therefore, any plantable which does actual block comparison, such as BlockMushroom, will fail here.
            // Attempts to extend this convoluted solution to cover these corner cases has not been fruitful.
        }
        catch( Exception e )
        {
            // TODO: warn
        }
        finally
        {
            GeoBlock.canSustainPlantEventOverride.remove();
        }

        event.setUseItem( Event.Result.DENY );
        event.setCancellationResult( EnumActionResult.FAIL );
    }

    @SubscribeEvent( priority = EventPriority.LOWEST )
    public static void projectileImpactEvent( ProjectileImpactEvent event )
    {
        World world = event.getEntity().world;
        if( world == null
            || ( world.isRemote && !StrataConfig.additionalBlockSounds )
            || event.getRayTraceResult().typeOfHit != RayTraceResult.Type.BLOCK )
        {
            return;
        }

        float volumeDivisor = 1.0f;
        if( event instanceof ProjectileImpactEvent.Throwable )
            volumeDivisor = 3.0f;
        else if( !( event instanceof ProjectileImpactEvent.Arrow ) )
            return;

        SoundType soundType;
        BlockPos pos = event.getRayTraceResult().getBlockPos();
        IBlockState blockState = world.getBlockState( pos );
        if( blockState.getBlock() instanceof GeoBlock )
        {
            GeoBlock geoBlock = (GeoBlock)blockState.getBlock();
            soundType = geoBlock.getTileInfo().soundType();
        }
        else if( blockState.getBlock() instanceof OreBlock )
        {
            OreBlock oreBlock = (OreBlock)blockState.getBlock();
            soundType = oreBlock.getSoundType( blockState , world , pos , event.getEntity() );
        }
        else
            soundType = blockState.getBlock().getSoundType( blockState , world , pos , event.getEntity() );

        if( soundType != null )
        {
            world.playSound(
                pos.getX(),
                pos.getY(),
                pos.getZ(),
                soundType.getHitSound(),
                SoundCategory.NEUTRAL,
                soundType.volume / volumeDivisor,
                soundType.pitch,
                false );
        }
    }
}
