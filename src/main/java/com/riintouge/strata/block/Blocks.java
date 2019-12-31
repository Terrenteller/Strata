package com.riintouge.strata.block;

import com.riintouge.strata.block.ore.DynamicOreHostTileEntity;
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

        TileLoader loader = new TileLoader();
        loader.load( "assets/strata/tiledata/geo.txt" );
        loader.load( "assets/strata/tiledata/ore.txt" );
        loader.load( "assets/strata/tiledata/vanilla.txt" );
        // TODO: Read from on-disk config directory

        GameRegistry.registerTileEntity(
            DynamicOreHostTileEntity.class,
            new ResourceLocation( "strata:ore_tile_entity" ) );
    }
}
