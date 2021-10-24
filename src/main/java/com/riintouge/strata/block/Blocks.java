package com.riintouge.strata.block;

import com.riintouge.strata.Strata;
import com.riintouge.strata.block.loader.TileDataLoader;
import com.riintouge.strata.block.ore.OreBlockTileEntity;
import com.riintouge.strata.resource.ConfigDir;
import net.minecraft.block.Block;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

import java.io.IOException;

public class Blocks
{
    @SubscribeEvent( priority = EventPriority.LOWEST )
    public static void registerBlocks( RegistryEvent.Register< Block > event ) throws IOException
    {
        Strata.LOGGER.trace( "Blocks::registerBlocks()" );

        TileDataLoader tileDataLoader = new TileDataLoader();
        String tileDataDir = String.format( "%s/tiledata/%s" , Strata.modid , Strata.modid );
        for( String path : ConfigDir.INSTANCE.allIn( tileDataDir , true ) )
            tileDataLoader.loadFile( path );

        for( ModContainer mod : Loader.instance().getIndexedModList().values() )
        {
            String modID = mod.getModId();
            if( !modID.equals( Strata.modid ) )
            {
                tileDataDir = String.format( "%s/tiledata/%s" , Strata.modid , modID );
                for( String path : ConfigDir.INSTANCE.allIn( tileDataDir , true ) )
                    tileDataLoader.loadFile( path );
            }
        }

        GameRegistry.registerTileEntity(
            OreBlockTileEntity.class,
            new ResourceLocation( "strata:ore_tile_entity" ) );
    }
}
