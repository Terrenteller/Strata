package com.riintouge.strata.block;

import com.riintouge.strata.block.loader.TileLoader;
import com.riintouge.strata.block.ore.OreBlockTileEntity;
import com.riintouge.strata.Config;
import net.minecraft.block.Block;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

import java.io.*;

public class Blocks
{
    @SubscribeEvent
    public static void onEvent( RegistryEvent.Register< Block > event ) throws IOException
    {
        System.out.println( "Blocks::registerBlocks()" );

        TileLoader tileLoader = new TileLoader();
        for( String path : Config.INSTANCE.allIn( "tiledata" , false ) )
            tileLoader.loadFile( path );

        GameRegistry.registerTileEntity(
            OreBlockTileEntity.class,
            new ResourceLocation( "strata:ore_tile_entity" ) );
    }
}
