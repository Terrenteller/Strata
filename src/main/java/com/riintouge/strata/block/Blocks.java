package com.riintouge.strata.block;

import com.riintouge.strata.Strata;
import com.riintouge.strata.block.loader.TileDataLoader;
import com.riintouge.strata.block.ore.OreBlockTileEntity;
import com.riintouge.strata.resource.Config;
import net.minecraft.block.Block;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

import java.io.IOException;

public class Blocks
{
    @SubscribeEvent
    public static void onEvent( RegistryEvent.Register< Block > event ) throws IOException
    {
        System.out.println( "Blocks::registerBlocks()" );

        TileDataLoader tileDataLoader = new TileDataLoader();
        for( String path : Config.INSTANCE.allIn( Strata.modid + "/tiledata" , true ) )
            tileDataLoader.loadFile( path );

        GameRegistry.registerTileEntity(
            OreBlockTileEntity.class,
            new ResourceLocation( "strata:ore_tile_entity" ) );
    }
}
