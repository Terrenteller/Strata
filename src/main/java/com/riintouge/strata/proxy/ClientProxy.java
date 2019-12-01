package com.riintouge.strata.proxy;

import com.riintouge.strata.proxy.event.ClientPreInitEventHandlers;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class ClientProxy extends CommonProxy
{
    @Override
    public void preInit( FMLPreInitializationEvent event )
    {
        super.preInit( event );
        System.out.println( "ClientProxy::preInit()" );

        // TODO: Can these move to regular init? What if other mods want to add to them?
        // Can mods be set to init after others?
        MinecraftForge.EVENT_BUS.register( ClientPreInitEventHandlers.class );
    }

    @Override
    public void init( FMLInitializationEvent event )
    {
        super.init( event );
        System.out.println( "ClientProxy::init()" );
    }

    @Override
    public void postInit( FMLPostInitializationEvent event )
    {
        super.postInit( event );
        System.out.println( "ClientProxy::postInit()" );
    }
}
