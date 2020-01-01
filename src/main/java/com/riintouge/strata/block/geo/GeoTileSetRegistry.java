package com.riintouge.strata.block.geo;

import com.riintouge.strata.block.IForgeRegistrable;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.item.Item;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.HashMap;
import java.util.Map;

public class GeoTileSetRegistry
{
    public static final GeoTileSetRegistry INSTANCE = new GeoTileSetRegistry();

    private Map< String , IForgeRegistrable > tileSets = new HashMap<>();

    private GeoTileSetRegistry()
    {
        ModelLoaderRegistry.registerLoader( new GeoBlockModelLoader() );
    }

    public void register( IForgeRegistrable tileSet , String tileSetName )
    {
        tileSets.put( tileSetName , tileSet );
    }

    public boolean contains( String tileSetName )
    {
        return tileSets.getOrDefault( tileSetName , null ) != null;
    }

    // Statics

    @SubscribeEvent( priority = EventPriority.LOWEST )
    public static void registerBlocks( RegistryEvent.Register< Block > event )
    {
        System.out.println( "GeoTileSetRegistry::registerBlocks()" );

        IForgeRegistry< Block > blockRegistry = event.getRegistry();
        for( IForgeRegistrable tileSet : INSTANCE.tileSets.values() )
            tileSet.registerBlocks( blockRegistry );
    }

    @SubscribeEvent( priority = EventPriority.LOWEST )
    public static void registerItems( RegistryEvent.Register< Item > event )
    {
        System.out.println( "GeoTileSetRegistry::registerItems()" );

        IForgeRegistry< Item > itemRegistry = event.getRegistry();
        for( IForgeRegistrable tileSet : INSTANCE.tileSets.values() )
            tileSet.registerItems( itemRegistry );
    }

    @SubscribeEvent( priority = EventPriority.LOWEST )
    public static void registerModels( ModelRegistryEvent event )
    {
        System.out.println( "GeoTileSetRegistry::registerModels()" );

        for( IForgeRegistrable tileSet : INSTANCE.tileSets.values() )
            tileSet.registerModels( event );
    }

    @SubscribeEvent( priority = EventPriority.LOWEST )
    public static void stitchTextures( TextureStitchEvent.Pre event )
    {
        System.out.println( "GeoTileSetRegistry::stitchTextures()" );

        TextureMap textureMap = event.getMap();
        for( IForgeRegistrable tileSet : INSTANCE.tileSets.values() )
            tileSet.stitchTextures( textureMap );
    }
}
