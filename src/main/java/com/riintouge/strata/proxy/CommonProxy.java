package com.riintouge.strata.proxy;

import com.riintouge.strata.block.Blocks;
import com.riintouge.strata.block.geo.GeoTileSetRegistry;
import com.riintouge.strata.block.ore.OreRegistry;
import com.riintouge.strata.Config;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class CommonProxy
{
    public void preInit( FMLPreInitializationEvent event )
    {
        System.out.println( "CommonProxy::preInit()" );

        Config.extractMissingConfigFiles();

        MinecraftForge.EVENT_BUS.register( Blocks.class );
        MinecraftForge.EVENT_BUS.register( GeoTileSetRegistry.class );
        MinecraftForge.EVENT_BUS.register( OreRegistry.class );
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
