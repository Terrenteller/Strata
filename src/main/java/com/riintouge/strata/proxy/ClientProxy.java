package com.riintouge.strata.proxy;

import com.riintouge.strata.Strata;
import com.riintouge.strata.StrataConfig;
import com.riintouge.strata.block.BakedModelCache;
import com.riintouge.strata.block.SampleBlockModelLoader;
import com.riintouge.strata.block.geo.GeoBlockModelLoader;
import com.riintouge.strata.block.ore.OreBlockModelLoader;
import com.riintouge.strata.block.ore.OreParticleTextureManager;
import com.riintouge.strata.entity.EntityThrowableGeoItemFragment;
import com.riintouge.strata.gui.StrataCreativeTabs;
import com.riintouge.strata.item.geo.GeoItemFragmentModelLoader;
import com.riintouge.strata.item.geo.GeoItemFragmentTextureManager;
import com.riintouge.strata.item.ore.OreItemModelLoader;
import com.riintouge.strata.item.ore.OreItemTextureManager;
import com.riintouge.strata.misc.BakedModelStoreProxy;
import com.riintouge.strata.render.RenderEntityThrowableGeoItemFragment;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
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

        RenderingRegistry.registerEntityRenderingHandler( EntityThrowableGeoItemFragment.class , RenderEntityThrowableGeoItemFragment::new );
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
        Strata.LOGGER.trace( "ClientProxy::postInit()" );

        if( StrataConfig.prioritizeCreativeTabs )
            StrataCreativeTabs.moveStrataTabsToBeforeOtherModTabs();
    }
}
