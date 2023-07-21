package com.riintouge.strata.proxy;

import com.riintouge.strata.EventHandlers;
import com.riintouge.strata.Strata;
import com.riintouge.strata.StrataConfig;
import com.riintouge.strata.block.Blocks;
import com.riintouge.strata.entity.EntityThrowableGeoItemFragment;
import com.riintouge.strata.recipe.BrewingRecipeReplicator;
import com.riintouge.strata.recipe.FurnaceRecipeReplicator;
import com.riintouge.strata.recipe.CraftingRecipeReplicator;
import com.riintouge.strata.block.geo.GeoTileSetRegistry;
import com.riintouge.strata.block.host.HostRegistry;
import com.riintouge.strata.block.ore.OreRegistry;
import com.riintouge.strata.network.NetworkManager;
import com.riintouge.strata.resource.DocsDir;
import com.riintouge.strata.resource.JarResourceHelper;
import com.riintouge.strata.sound.SoundEventRegistry;
import com.riintouge.strata.util.DebugUtil;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.EntityRegistry;

import java.io.IOException;
import java.util.zip.ZipException;

public class CommonProxy
{
    private static int nextEntityID = 0;

    public void preInit( FMLPreInitializationEvent event )
    {
        Strata.LOGGER.trace( "CommonProxy::preInit()" );

        try
        {
            JarResourceHelper strataResources = new JarResourceHelper( Strata.class );
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

        registerEntity( EntityThrowableGeoItemFragment.class , 64 , 10 , true );
    }

    public void init( FMLInitializationEvent event )
    {
        Strata.LOGGER.trace( "CommonProxy::init()" );

        FurnaceRecipeReplicator.replicateAndRegister();
        CraftingRecipeReplicator.replicateAndRegister();
    }

    public void postInit( FMLPostInitializationEvent event )
    {
        // Nothing to do
    }

    // Statics

    private static < T extends Entity > void registerEntity(
        Class< T > entityClass,
        int trackingRange,
        int updateFrequency,
        boolean sendsVelocityUpdates )
    {
        ResourceLocation entityResource = Strata.resource( entityClass.getSimpleName() );
        EntityRegistry.registerModEntity(
            entityResource,
            entityClass,
            entityResource.toString().replace( ":" , "." ),
            nextEntityID++,
            Strata.INSTANCE,
            trackingRange,
            updateFrequency,
            sendsVelocityUpdates );
    }
}
