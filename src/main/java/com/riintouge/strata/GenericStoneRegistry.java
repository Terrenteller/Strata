package com.riintouge.strata;

import com.riintouge.strata.block.GenericStoneTileSet;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.item.Item;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.event.RegistryEvent;
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
        stoneTileSetMap.put( tileSet.stoneName , tileSet );
    }

    public GenericStoneTileSet find( String stoneName )
    {
        return stoneTileSetMap.getOrDefault( stoneName , null );
    }

    public void registerBlocks( RegistryEvent.Register< Block > event )
    {
        System.out.println( "GenericStoneRegistry::registerBlocks()" );

        IForgeRegistry< Block > blockRegistry = event.getRegistry();
        for( GenericStoneTileSet tileSet : stoneTileSetMap.values() )
            tileSet.registerBlocks( blockRegistry );
    }

    public void registerItems( RegistryEvent.Register< Item > event )
    {
        System.out.println( "GenericStoneRegistry::registerItems()" );

        IForgeRegistry< Item > itemRegistry = event.getRegistry();
        for( GenericStoneTileSet tileSet : stoneTileSetMap.values() )
            tileSet.registerItems( itemRegistry );
    }

    public void registerModels( ModelRegistryEvent event )
    {
        System.out.println( "GenericStoneRegistry::registerModels()" );

        for( GenericStoneTileSet tileSet : stoneTileSetMap.values() )
            tileSet.registerModels( event );
    }

    public void stitchTextures( TextureStitchEvent.Pre event )
    {
        System.out.println( "GenericStoneRegistry::stitchTextures()" );

        TextureMap textureMap = event.getMap();
        for( GenericStoneTileSet tileSet : stoneTileSetMap.values() )
            tileSet.stitchTextures( textureMap );
    }
}
