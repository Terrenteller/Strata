package com.riintouge.strata.proxy;

import com.riintouge.strata.Strata;
import com.riintouge.strata.block.SampleBlockModelLoader;
import com.riintouge.strata.block.geo.BakedModelCache;
import com.riintouge.strata.block.geo.GeoBlockModelLoader;
import com.riintouge.strata.block.geo.GeoItemFragmentModelLoader;
import com.riintouge.strata.block.geo.GeoItemFragmentTextureManager;
import com.riintouge.strata.block.ore.*;
import com.riintouge.strata.misc.BakedModelStoreProxy;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly( Side.CLIENT )
public class ClientProxy extends CommonProxy
{
    @Override
    public void preInit( FMLPreInitializationEvent event )
    {
        super.preInit( event );
        Strata.LOGGER.trace( "ClientProxy::preInit()" );

        MinecraftForge.EVENT_BUS.register( GeoItemFragmentTextureManager.class );
        MinecraftForge.EVENT_BUS.register( OreParticleTextureManager.class );
        MinecraftForge.EVENT_BUS.register( OreItemTextureManager.class );
        MinecraftForge.EVENT_BUS.register( BakedModelCache.class );

        ModelLoaderRegistry.registerLoader( new GeoBlockModelLoader() );
        ModelLoaderRegistry.registerLoader( new GeoItemFragmentModelLoader() );
        ModelLoaderRegistry.registerLoader( new OreBlockModelLoader() );
        ModelLoaderRegistry.registerLoader( new OreItemModelLoader() );
        ModelLoaderRegistry.registerLoader( new SampleBlockModelLoader() );
    }

    @Override
    public void init( FMLInitializationEvent event )
    {
        super.init( event );
        Strata.LOGGER.trace( "ClientProxy::init()" );

        BakedModelStoreProxy.inject();
    }

    @Override
    public void postInit( FMLPostInitializationEvent event )
    {
        super.postInit( event );
    }
}
