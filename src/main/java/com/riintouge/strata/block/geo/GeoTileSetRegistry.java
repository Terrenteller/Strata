package com.riintouge.strata.block.geo;

import com.riintouge.strata.Strata;
import com.riintouge.strata.block.IForgeRegistrable;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.registries.IForgeRegistry;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

public final class GeoTileSetRegistry
{
    public static final GeoTileSetRegistry INSTANCE = new GeoTileSetRegistry();

    private final Map< String , GeoTileSet > tileSetMap = new HashMap<>();

    private GeoTileSetRegistry()
    {
        // Nothing to do
    }

    @Nonnull
    public Set< String > tileSetNames()
    {
        return tileSetMap.keySet();
    }

    public void register( GeoTileSet tileSet , String tileSetName ) throws IllegalStateException
    {
        if( find( tileSetName ) != null )
            throw new IllegalStateException( String.format( "Tile set '%s' already registered!" , tileSetName ) );

        tileSetMap.put( tileSetName , tileSet );
    }

    public boolean contains( String tileSetName )
    {
        return tileSetMap.getOrDefault( tileSetName , null ) != null;
    }

    @Nullable
    public IGeoTileSet find( String tileSetName )
    {
        return tileSetMap.getOrDefault( tileSetName , null );
    }

    @Nullable
    public IGeoTileInfo findTileInfo( String tileSetName , TileType tileType )
    {
        GeoTileSet tileSet = tileSetMap.getOrDefault( tileSetName , null );
        return tileSet != null ? tileSet.getInfo( tileType ) : null;
    }

    // Statics

    @SubscribeEvent( priority = EventPriority.LOWEST )
    public static void registerBlocks( RegistryEvent.Register< Block > event )
    {
        Strata.LOGGER.trace( "GeoTileSetRegistry::registerBlocks()" );

        // NOTE: Multipart models used as dependencies do not seem to get loaded by recursive model loading.
        // In the future if necessary, try creating a new block using the multipart model and register it first.
        // A dummy block using the model may be necessary to get the right stuff loaded before it gets used.
        // Stacktraces and a brain dump are available upon request.

        IForgeRegistry< Block > blockRegistry = event.getRegistry();
        for( IForgeRegistrable tileSet : INSTANCE.tileSetMap.values() )
            tileSet.registerBlocks( blockRegistry );
    }

    @SubscribeEvent( priority = EventPriority.LOWEST )
    public static void registerItems( RegistryEvent.Register< Item > event )
    {
        Strata.LOGGER.trace( "GeoTileSetRegistry::registerItems()" );

        IForgeRegistry< Item > itemRegistry = event.getRegistry();
        for( IForgeRegistrable tileSet : INSTANCE.tileSetMap.values() )
            tileSet.registerItems( itemRegistry );
    }

    @SubscribeEvent( priority = EventPriority.LOWEST )
    public static void registerRecipes( RegistryEvent.Register< IRecipe > event )
    {
        Strata.LOGGER.trace( "GeoTileSetRegistry::registerRecipes()" );

        IForgeRegistry< IRecipe > recipeRegistry = event.getRegistry();
        for( IForgeRegistrable tileSet : INSTANCE.tileSetMap.values() )
            tileSet.registerRecipes( recipeRegistry );
    }

    @SubscribeEvent( priority = EventPriority.LOWEST )
    public static void registerModels( ModelRegistryEvent event )
    {
        Strata.LOGGER.trace( "GeoTileSetRegistry::registerModels()" );

        for( IForgeRegistrable tileSet : INSTANCE.tileSetMap.values() )
            tileSet.registerModels( event );
    }

    @SideOnly( Side.CLIENT )
    @SubscribeEvent( priority = EventPriority.LOWEST )
    public static void stitchTextures( TextureStitchEvent.Pre event )
    {
        Strata.LOGGER.trace( "GeoTileSetRegistry::stitchTextures()" );

        TextureMap textureMap = event.getMap();
        for( IForgeRegistrable tileSet : INSTANCE.tileSetMap.values() )
            tileSet.stitchTextures( textureMap );
    }
}
