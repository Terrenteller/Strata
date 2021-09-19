package com.riintouge.strata.block.geo;

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
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class GeoTileSetRegistry
{
    public static final GeoTileSetRegistry INSTANCE = new GeoTileSetRegistry();

    private Map< String , GeoTileSet > tileSets = new HashMap<>();

    private GeoTileSetRegistry()
    {
        // Nothing to do
    }

    @Nonnull
    public Set< String > tileSetNames()
    {
        return tileSets.keySet();
    }

    public void register( GeoTileSet tileSet , String tileSetName )
    {
        tileSets.put( tileSetName , tileSet );
    }

    public boolean contains( String tileSetName )
    {
        return tileSets.getOrDefault( tileSetName , null ) != null;
    }

    @Nullable
    public IGeoTileInfo findTileInfo( String tileSetName , TileType tileType )
    {
        GeoTileSet tileSet = tileSets.getOrDefault( tileSetName , null );
        return tileSet != null ? tileSet.find( tileType ) : null;
    }

    @Nullable
    public IGeoTileInfo findTileInfo( String tileSetName , @Nullable String type )
    {
        if( type != null && !type.isEmpty() )
        {
            try
            {
                return INSTANCE.findTileInfo( tileSetName , Enum.valueOf( TileType.class , type.toUpperCase() ) );
            }
            catch( IllegalArgumentException ex )
            {
                return null;
            }
        }

        // We can't tell primary types apart here
        for( TileType tileType : TileType.values() )
        {
            if( tileType.isPrimary )
            {
                IGeoTileInfo tileInfo = INSTANCE.findTileInfo( tileSetName , tileType );
                if( tileInfo != null )
                    return tileInfo;
            }
        }

        return null;
    }

    // Statics

    @SubscribeEvent( priority = EventPriority.LOWEST )
    public static void registerBlocks( RegistryEvent.Register< Block > event )
    {
        System.out.println( "GeoTileSetRegistry::registerBlocks()" );

        // NOTE: Multipart models used as dependencies do not seem to get loaded by the
        // recursive model loading. In the future if necessary, create a new block using
        // the multipart model and register it here before anything else.

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
    public static void registerRecipes( RegistryEvent.Register< IRecipe > event )
    {
        System.out.println( "GeoTileSetRegistry::registerRecipes()" );

        IForgeRegistry< IRecipe > recipeRegistry = event.getRegistry();
        for( IForgeRegistrable tileSet : INSTANCE.tileSets.values() )
            tileSet.registerRecipes( recipeRegistry );
    }

    @SubscribeEvent( priority = EventPriority.LOWEST )
    public static void registerModels( ModelRegistryEvent event )
    {
        System.out.println( "GeoTileSetRegistry::registerModels()" );

        for( IForgeRegistrable tileSet : INSTANCE.tileSets.values() )
            tileSet.registerModels( event );
    }

    @SideOnly( Side.CLIENT )
    @SubscribeEvent( priority = EventPriority.LOWEST )
    public static void stitchTextures( TextureStitchEvent.Pre event )
    {
        System.out.println( "GeoTileSetRegistry::stitchTextures()" );

        TextureMap textureMap = event.getMap();
        for( IForgeRegistrable tileSet : INSTANCE.tileSets.values() )
            tileSet.stitchTextures( textureMap );
    }
}
