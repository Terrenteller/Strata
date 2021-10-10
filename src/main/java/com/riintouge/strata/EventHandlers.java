package com.riintouge.strata;

import com.riintouge.strata.block.geo.GeoBlock;
import com.riintouge.strata.block.geo.IHostInfo;
import com.riintouge.strata.block.ore.IOreInfo;
import com.riintouge.strata.block.ore.OreBlock;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlockSpecial;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@EventBusSubscriber
public class EventHandlers
{
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
        IBlockState state = event.getState();
        Block block = state.getBlock();
        if( !( block instanceof OreBlock ) )
            return;

        World world = event.getEntityPlayer().world;
        if( world == null )
            return;

        OreBlock oreBlock = (OreBlock)block;
        ItemStack tool = event.getEntityPlayer().getHeldItemMainhand();
        state = block.getActualState( state , world , event.getPos() );
        IHostInfo hostInfo = oreBlock.getHostInfo( state );
        IOreInfo oreInfo = oreBlock.getOreInfo();

        // When the host material does not require a tool, methods like Block.getHarvestTool()
        // and Block.getHarvestLevel() are skipped. This complicates requirement checks and speed boosts.
        // Instead of having OreBlock gamble on an answer, it always reports true for tool effectiveness
        // and relies on this event handler to apply a penalty where applicable.
        if( hostInfo != null && !oreBlock.isToolActuallyEffective( tool , hostInfo.material() , oreInfo.material() ) )
        {
            Block hostBlock = Block.getBlockFromName( hostInfo.registryName().toString() );
            float hostDestroySpeed = tool.getDestroySpeed( hostBlock.getStateFromMeta( hostInfo.meta() ) );
            float forgeHookHarvestPenalty = 30f / 100f; // Inverse of the "bonus" in ForgeHooks.blockStrength()
            event.setNewSpeed( hostDestroySpeed * forgeHookHarvestPenalty );
        }
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
}
