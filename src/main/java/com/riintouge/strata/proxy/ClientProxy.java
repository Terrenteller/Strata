package com.riintouge.strata.proxy;

import com.riintouge.strata.block.geo.GeoItemFragmentTextureManager;
import com.riintouge.strata.block.ore.OreBlockTextureManager;
import com.riintouge.strata.block.ore.OreItemTextureManager;
import com.riintouge.strata.misc.BakedModelStoreProxy;
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

        MinecraftForge.EVENT_BUS.register( GeoItemFragmentTextureManager.class );
        MinecraftForge.EVENT_BUS.register( OreBlockTextureManager.class );
        MinecraftForge.EVENT_BUS.register( OreItemTextureManager.class );
    }

    @Override
    public void init( FMLInitializationEvent event )
    {
        super.init( event );
        System.out.println( "ClientProxy::init()" );

        BakedModelStoreProxy.inject();
    }

    @Override
    public void postInit( FMLPostInitializationEvent event )
    {
        super.postInit( event );
        System.out.println( "ClientProxy::postInit()" );
    }
}
