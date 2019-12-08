package com.riintouge.strata;

import com.riintouge.strata.block.DynamicOreHostManager;
import com.riintouge.strata.block.GenericStoneModelLoader;
import com.riintouge.strata.block.GenericStoneTileSet;
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

public class GenericStoneRegistry
{
    public static final GenericStoneRegistry INSTANCE = new GenericStoneRegistry();

    private Map< String , GenericStoneTileSet > stoneTileSetMap = new HashMap<>();

    private GenericStoneRegistry()
    {
    }

    public void register( GenericStoneTileSet tileSet )
    {
        stoneTileSetMap.put( tileSet.tileSetInfo.stoneName() , tileSet );

        DynamicOreHostManager.INSTANCE.registerHost(
            tileSet.tileSetInfo.stoneName(),
            tileSet.tileSetInfo.baseTextureLocation() );
    }

    public GenericStoneTileSet find( String stoneName )
    {
        return stoneTileSetMap.getOrDefault( stoneName , null );
    }

    public boolean contains( String stoneName )
    {
        return stoneTileSetMap.getOrDefault( stoneName , null ) != null;
    }

    @SubscribeEvent( priority = EventPriority.LOWEST )
    public static void registerBlocks( RegistryEvent.Register< Block > event )
    {
        System.out.println( "GenericStoneRegistry::registerBlocks()" );

        IForgeRegistry< Block > blockRegistry = event.getRegistry();
        for( GenericStoneTileSet tileSet : INSTANCE.stoneTileSetMap.values() )
            tileSet.registerBlocks( blockRegistry );
    }

    @SubscribeEvent( priority = EventPriority.LOWEST )
    public static void registerItems( RegistryEvent.Register< Item > event )
    {
        System.out.println( "GenericStoneRegistry::registerItems()" );

        IForgeRegistry< Item > itemRegistry = event.getRegistry();
        for( GenericStoneTileSet tileSet : INSTANCE.stoneTileSetMap.values() )
            tileSet.registerItems( itemRegistry );
    }

    @SubscribeEvent( priority = EventPriority.LOWEST )
    public static void registerModels( ModelRegistryEvent event )
    {
        System.out.println( "GenericStoneRegistry::registerModels()" );

        ModelLoaderRegistry.registerLoader( new GenericStoneModelLoader() );

        for( GenericStoneTileSet tileSet : INSTANCE.stoneTileSetMap.values() )
            tileSet.registerModels( event );
    }

    @SubscribeEvent( priority = EventPriority.LOWEST )
    public static void stitchTextures( TextureStitchEvent.Pre event )
    {
        System.out.println( "GenericStoneRegistry::stitchTextures()" );

        TextureMap textureMap = event.getMap();
        for( GenericStoneTileSet tileSet : INSTANCE.stoneTileSetMap.values() )
            tileSet.stitchTextures( textureMap );
    }
}
