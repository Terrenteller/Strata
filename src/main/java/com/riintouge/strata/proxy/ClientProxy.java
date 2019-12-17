package com.riintouge.strata.proxy;

import com.riintouge.strata.block.ore.DynamicOreHostManager;
import com.riintouge.strata.block.ore.OreItemTextureManager;
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

        MinecraftForge.EVENT_BUS.register( DynamicOreHostManager.class );
        MinecraftForge.EVENT_BUS.register( OreItemTextureManager.class );
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
