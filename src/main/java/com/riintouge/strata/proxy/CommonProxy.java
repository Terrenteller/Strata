package com.riintouge.strata.proxy;

import com.riintouge.strata.EventHandlers;
import com.riintouge.strata.block.geo.HostRegistry;
import com.riintouge.strata.resource.Docs;
import com.riintouge.strata.block.Blocks;
import com.riintouge.strata.block.FurnaceRecipeReplicator;
import com.riintouge.strata.block.RecipeReplicator;
import com.riintouge.strata.block.geo.GeoTileSetRegistry;
import com.riintouge.strata.block.ore.OreRegistry;
import com.riintouge.strata.resource.Config;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class CommonProxy
{
    public void preInit( FMLPreInitializationEvent event )
    {
        System.out.println( "CommonProxy::preInit()" );

        Config.INSTANCE.extractMissingResourceFiles();
        Docs.INSTANCE.extractMissingResourceFiles();

        MinecraftForge.EVENT_BUS.register( Blocks.class );
        MinecraftForge.EVENT_BUS.register( EventHandlers.class );
        MinecraftForge.EVENT_BUS.register( FurnaceRecipeReplicator.class );
        MinecraftForge.EVENT_BUS.register( HostRegistry.class );
        MinecraftForge.EVENT_BUS.register( GeoTileSetRegistry.class );
        MinecraftForge.EVENT_BUS.register( OreRegistry.class );
        MinecraftForge.EVENT_BUS.register( RecipeReplicator.class );
    }

    public void init( FMLInitializationEvent event )
    {
        // Nothing to do
    }

    public void postInit( FMLPostInitializationEvent event )
    {
        // Nothing to do
    }
}
