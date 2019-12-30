package com.riintouge.strata.block;

import com.riintouge.strata.block.geo.GenericStoneModelLoader;
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

public class GenericTileSetRegistry
{
    public static final GenericTileSetRegistry INSTANCE = new GenericTileSetRegistry();

    private Map< String , IForgeRegistrable > tileSets = new HashMap<>();

    private GenericTileSetRegistry()
    {
        ModelLoaderRegistry.registerLoader( new GenericStoneModelLoader() );
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
        System.out.println( "GenericTileSetRegistry::registerBlocks()" );

        IForgeRegistry< Block > blockRegistry = event.getRegistry();
        for( IForgeRegistrable tileSet : INSTANCE.tileSets.values() )
            tileSet.registerBlocks( blockRegistry );
    }

    @SubscribeEvent( priority = EventPriority.LOWEST )
    public static void registerItems( RegistryEvent.Register< Item > event )
    {
        System.out.println( "GenericTileSetRegistry::registerItems()" );

        IForgeRegistry< Item > itemRegistry = event.getRegistry();
        for( IForgeRegistrable tileSet : INSTANCE.tileSets.values() )
            tileSet.registerItems( itemRegistry );
    }

    @SubscribeEvent( priority = EventPriority.LOWEST )
    public static void registerModels( ModelRegistryEvent event )
    {
        System.out.println( "GenericTileSetRegistry::registerModels()" );

        for( IForgeRegistrable tileSet : INSTANCE.tileSets.values() )
            tileSet.registerModels( event );
    }

    @SubscribeEvent( priority = EventPriority.LOWEST )
    public static void stitchTextures( TextureStitchEvent.Pre event )
    {
        System.out.println( "GenericTileSetRegistry::stitchTextures()" );

        TextureMap textureMap = event.getMap();
        for( IForgeRegistrable tileSet : INSTANCE.tileSets.values() )
            tileSet.stitchTextures( textureMap );
    }
}
