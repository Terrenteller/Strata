package com.riintouge.strata.proxy.event;

import com.riintouge.strata.block.DynamicOreHostManager;
import com.riintouge.strata.block.GenericStoneModelLoader;
import com.riintouge.strata.block.ore.GenericOreBlockModelLoader;
import com.riintouge.strata.item.OreItemModelLoader;
import com.riintouge.strata.item.OreItemTextureManager;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

// TODO: Add some tracing helpers to replace the println's here and elsewhere
public class ClientPreInitEventHandlers
{
    @SubscribeEvent( priority = EventPriority.LOWEST )
    public static void onEvent( ModelRegistryEvent event )
    {
        System.out.println( "ClientPreInitEventHandlers::onEvent( ModelRegistryEvent )" );

        // TODO: Move into respective registries
        ModelLoaderRegistry.registerLoader( new GenericStoneModelLoader() );
        ModelLoaderRegistry.registerLoader( new OreItemModelLoader() );
        ModelLoaderRegistry.registerLoader( new GenericOreBlockModelLoader() );
    }

    @SubscribeEvent( priority = EventPriority.LOWEST )
    public static void onEvent( TextureStitchEvent.Pre event )
    {
        System.out.println( "ClientPreInitEventHandlers::onEvent( TextureStitchEvent.Pre )" );

        DynamicOreHostManager.INSTANCE.regenerate( event.getMap() );
        OreItemTextureManager.INSTANCE.regenerate( event.getMap() );
    }
}
