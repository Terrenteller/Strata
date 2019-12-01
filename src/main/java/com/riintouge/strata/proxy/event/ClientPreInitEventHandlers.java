package com.riintouge.strata.proxy.event;

import com.riintouge.strata.block.DynamicOreHostManager;
import com.riintouge.strata.block.DynamicOreHostModel;
import com.riintouge.strata.item.OreItemModelLoader;
import com.riintouge.strata.item.OreItemTextureManager;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

// TODO: Add some tracing helpers to replace the println's here and elsewhere
public class ClientPreInitEventHandlers
{
    @SubscribeEvent
    public static void onEvent( ModelRegistryEvent event )
    {
        System.out.println( "ClientPreInitEventHandlers::onEvent( ModelRegistryEvent )" );

        ModelLoaderRegistry.registerLoader( new OreItemModelLoader() );
    }

    @SubscribeEvent( priority = EventPriority.LOWEST )
    public static void onEvent( TextureStitchEvent.Pre event )
    {
        System.out.println( "ClientPreInitEventHandlers::onEvent( TextureStitchEvent.Pre )" );

        DynamicOreHostManager.INSTANCE.regenerate( event.getMap() );
        OreItemTextureManager.INSTANCE.regenerate( event.getMap() );
    }

    @SubscribeEvent
    public static void onEvent( ModelBakeEvent event )
    {
        System.out.println( "ClientPreInitEventHandlers::onEvent( ModelBakeEvent )" );

        for( ModelResourceLocation model : DynamicOreHostManager.INSTANCE.getAllOreBlockModels() )
        {
            IBakedModel existingModel = event.getModelRegistry().getObject( model );
            if( existingModel != null )
            {
                DynamicOreHostModel customModel = new DynamicOreHostModel( existingModel );
                event.getModelRegistry().putObject( model , customModel );
            }
        }
    }
}
