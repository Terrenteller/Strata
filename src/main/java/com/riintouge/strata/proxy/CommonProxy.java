package com.riintouge.strata.proxy;

import com.riintouge.strata.init.Blocks;
import com.riintouge.strata.init.Items;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class CommonProxy
{
    public void preInit( FMLPreInitializationEvent event )
    {
        System.out.println( "CommonProxy::preInit()" );

        MinecraftForge.EVENT_BUS.register( Blocks.class );
        MinecraftForge.EVENT_BUS.register( Items.class );
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
