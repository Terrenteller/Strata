package com.riintouge.strata.proxy;

import com.riintouge.strata.EventHandlers;
import com.riintouge.strata.Strata;
import com.riintouge.strata.StrataConfig;
import com.riintouge.strata.block.Blocks;
import com.riintouge.strata.recipe.BrewingRecipeReplicator;
import com.riintouge.strata.block.FurnaceRecipeReplicator;
import com.riintouge.strata.block.RecipeReplicator;
import com.riintouge.strata.block.geo.GeoTileSetRegistry;
import com.riintouge.strata.block.geo.HostRegistry;
import com.riintouge.strata.block.ore.OreRegistry;
import com.riintouge.strata.network.NetworkManager;
import com.riintouge.strata.resource.ConfigDir;
import com.riintouge.strata.resource.DocsDir;
import com.riintouge.strata.resource.JarResourceHelper;
import com.riintouge.strata.sound.SoundEventRegistry;
import com.riintouge.strata.util.DebugUtil;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import java.io.IOException;
import java.util.zip.ZipException;

public class CommonProxy
{
    public void preInit( FMLPreInitializationEvent event )
    {
        Strata.LOGGER.trace( "CommonProxy::preInit()" );

        try
        {
            JarResourceHelper strataResources = new JarResourceHelper( Strata.class );
            ( new ConfigDir() ).extractResources( strataResources , false );
            ( new DocsDir() ).extractResources( strataResources , true );
        }
        catch( ZipException e )
        {
            // Assume we're running in debug and our resources are unpacked/symlinked appropriately
        }
        catch( IOException e )
        {
            Strata.LOGGER.error( DebugUtil.prettyPrintThrowable( e , "Caught %s while extracting resources!" ) );
        }

        StrataConfig.INSTANCE.init();
        NetworkManager.INSTANCE.init( event.getSide() );

        MinecraftForge.EVENT_BUS.register( StrataConfig.class );
        MinecraftForge.EVENT_BUS.register( Blocks.class );
        MinecraftForge.EVENT_BUS.register( EventHandlers.class );
        MinecraftForge.EVENT_BUS.register( NetworkManager.class );
        MinecraftForge.EVENT_BUS.register( HostRegistry.class );
        MinecraftForge.EVENT_BUS.register( GeoTileSetRegistry.class );
        MinecraftForge.EVENT_BUS.register( OreRegistry.class );
        MinecraftForge.EVENT_BUS.register( SoundEventRegistry.class );
        MinecraftForge.EVENT_BUS.register( BrewingRecipeReplicator.class );
    }

    public void init( FMLInitializationEvent event )
    {
        Strata.LOGGER.trace( "CommonProxy::init()" );

        FurnaceRecipeReplicator.replicateAndRegister();
        RecipeReplicator.replicateAndRegister();
    }

    public void postInit( FMLPostInitializationEvent event )
    {
        // Nothing to do
    }
}
