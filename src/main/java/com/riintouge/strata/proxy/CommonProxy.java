package com.riintouge.strata.proxy;

import com.riintouge.strata.EventHandlers;
import com.riintouge.strata.Strata;
import com.riintouge.strata.StrataConfig;
import com.riintouge.strata.block.Blocks;
import com.riintouge.strata.block.FurnaceRecipeReplicator;
import com.riintouge.strata.block.RecipeReplicator;
import com.riintouge.strata.block.geo.GeoTileSetRegistry;
import com.riintouge.strata.block.geo.HostRegistry;
import com.riintouge.strata.block.ore.OreRegistry;
import com.riintouge.strata.network.NetworkEventHandlers;
import com.riintouge.strata.network.StrataNetworkManager;
import com.riintouge.strata.resource.ConfigDir;
import com.riintouge.strata.resource.DocsDir;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class CommonProxy
{
    public void preInit( FMLPreInitializationEvent event )
    {
        Strata.LOGGER.trace( "CommonProxy::preInit()" );

        StrataConfig.INSTANCE.getConfig(); // Ignore return for initialization
        ConfigDir.INSTANCE.extractMissingResourceFiles();
        DocsDir.INSTANCE.extractMissingResourceFiles();

        StrataNetworkManager.init();

        MinecraftForge.EVENT_BUS.register( StrataConfig.class );
        MinecraftForge.EVENT_BUS.register( Blocks.class );
        MinecraftForge.EVENT_BUS.register( EventHandlers.class );
        MinecraftForge.EVENT_BUS.register( NetworkEventHandlers.class );
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
