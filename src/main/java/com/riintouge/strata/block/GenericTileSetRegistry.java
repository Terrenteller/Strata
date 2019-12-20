package com.riintouge.strata.block;

import com.riintouge.strata.block.geo.GenericStoneModelLoader;
import com.riintouge.strata.block.geo.tileset.IGenericTileSet;
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

    private Map< String , IGenericTileSet > tileSetMap = new HashMap<>();

    private GenericTileSetRegistry()
    {
    }

    public void register( IGenericTileSet tileSet )
    {
        tileSetMap.put( tileSet.tileSetInfo().registryName().getResourcePath() , tileSet );
    }

    public IGenericTileSet find( String tileSetName )
    {
        return tileSetMap.getOrDefault( tileSetName , null );
    }

    @SuppressWarnings( "unchecked" )
    public < T > T find( String tileSetName , Class< T > clazz )
    {
        try
        {
            return clazz.cast( tileSetMap.getOrDefault( tileSetName , null ) );
        }
        catch( ClassCastException e )
        {
            return null;
        }
    }

    public boolean contains( String tileSetName )
    {
        return tileSetMap.getOrDefault( tileSetName , null ) != null;
    }

    public < T > boolean containsType( String tileSetName , Class< T > clazz )
    {
        return find( tileSetName , clazz ) != null;
    }

    // Statics

    @SubscribeEvent( priority = EventPriority.LOWEST )
    public static void registerBlocks( RegistryEvent.Register< Block > event )
    {
        System.out.println( "GenericTileSetRegistry::registerBlocks()" );

        IForgeRegistry< Block > blockRegistry = event.getRegistry();
        for( IGenericTileSet tileSet : INSTANCE.tileSetMap.values() )
            tileSet.registerBlocks( blockRegistry );
    }

    @SubscribeEvent( priority = EventPriority.LOWEST )
    public static void registerItems( RegistryEvent.Register< Item > event )
    {
        System.out.println( "GenericTileSetRegistry::registerItems()" );

        IForgeRegistry< Item > itemRegistry = event.getRegistry();
        for( IGenericTileSet tileSet : INSTANCE.tileSetMap.values() )
            tileSet.registerItems( itemRegistry );
    }

    @SubscribeEvent( priority = EventPriority.LOWEST )
    public static void registerModels( ModelRegistryEvent event )
    {
        System.out.println( "GenericTileSetRegistry::registerModels()" );

        ModelLoaderRegistry.registerLoader( new GenericStoneModelLoader() );

        for( IGenericTileSet tileSet : INSTANCE.tileSetMap.values() )
            tileSet.registerModels( event );
    }

    @SubscribeEvent( priority = EventPriority.LOWEST )
    public static void stitchTextures( TextureStitchEvent.Pre event )
    {
        System.out.println( "GenericTileSetRegistry::stitchTextures()" );

        TextureMap textureMap = event.getMap();
        for( IGenericTileSet tileSet : INSTANCE.tileSetMap.values() )
            tileSet.stitchTextures( textureMap );
    }
}
