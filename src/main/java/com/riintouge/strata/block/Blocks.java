package com.riintouge.strata.block;

import com.riintouge.strata.Strata;
import com.riintouge.strata.block.loader.TileDataLoader;
import com.riintouge.strata.block.ore.OreBlockTileEntity;
import com.riintouge.strata.resource.ConfigDir;
import com.riintouge.strata.resource.ResourcePacksDir;
import net.minecraft.block.Block;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Enumeration;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

public class Blocks
{
    private static final String StrataAssetConfigPath = String.format( "assets/%s/config/%s" , Strata.modid , Strata.modid );
    private static final String ModIDPattern = "[a-z0-9]+";

    private static final String ResourcePackTileDataPath = String.format( "%s/tiledata" , StrataAssetConfigPath );
    private static final String ResourcePathTileDataPathPattern = String.format( "^%s/(%s)" , ResourcePackTileDataPath , "tiledata" , ModIDPattern );
    private static final int ResourcePathTileDataPathModIDGroup = 1;
    private static final Pattern ResourcePackTileDataPathRegex = Pattern.compile( ResourcePathTileDataPathPattern );

    private static final String ResourcePackRecipePath = String.format( "%s/recipe" , StrataAssetConfigPath );
    private static final String ResourcePathRecipePathPattern = String.format( "^%s/(%s)" , ResourcePackRecipePath , ModIDPattern );
    private static final int ResourcePathRecipePathModIDGroup = 1;
    private static final Pattern ResourcePackRecipePathRegex = Pattern.compile( ResourcePathRecipePathPattern );

    @SubscribeEvent( priority = EventPriority.LOWEST )
    public static void registerBlocks( RegistryEvent.Register< Block > event ) throws IOException
    {
        Strata.LOGGER.trace( "Blocks::registerBlocks()" );

        TileDataLoader tileDataLoader = new TileDataLoader();

        List< String > activeResourcePackPaths = ResourcePacksDir.INSTANCE.activeResourcePackPaths();
        if( activeResourcePackPaths == null )
            activeResourcePackPaths = ResourcePacksDir.INSTANCE.find( s -> true , false );

        // Priority #1: Config files in our domain
        processConfigFilesForMod( tileDataLoader , Strata.modid );

        // Priority #2: Config files outside our domain
        for( ModContainer mod : Loader.instance().getIndexedModList().values() )
        {
            String modID = mod.getModId();
            if( !modID.equalsIgnoreCase( Strata.modid ) )
                processConfigFilesForMod( tileDataLoader , modID );
        }

        // Priority #3: Loose resource pack files in our domain
        for( String path : activeResourcePackPaths )
            if( new File( path ).isDirectory() )
                processLooseResourcePack( tileDataLoader , path , true );

        // Priority #4: ZIP'd resource pack files in our domain
        for( String path : activeResourcePackPaths )
            if( ! new File( path ).isDirectory() )
                processCompressedResourcePack( tileDataLoader , path , true );

        // Priority #5: Loose resource pack files outside our domain
        for( String path : activeResourcePackPaths )
            if( new File( path ).isDirectory() )
                processLooseResourcePack( tileDataLoader , path , false );

        // Priority #6: ZIP'd resource pack files outside our domain
        for( String path : activeResourcePackPaths )
            if( ! new File( path ).isDirectory() )
                processCompressedResourcePack( tileDataLoader , path , false );

        GameRegistry.registerTileEntity(
            OreBlockTileEntity.class,
            new ResourceLocation( String.format( "%s:ore_tile_entity" , Strata.modid ) ) );
    }

    private static void processConfigFilesForMod( TileDataLoader tileDataLoader , String modID ) throws IOException
    {
        String tileDataDir = String.format( "%s/tiledata/%s" , Strata.modid , modID );
        for( String path : ConfigDir.INSTANCE.allIn( tileDataDir , true ) )
            tileDataLoader.loadFile( path );

        String recipeDataDir = String.format( "%s/recipe/%s" , Strata.modid , modID );
        for( String path : ConfigDir.INSTANCE.allIn( recipeDataDir , true ) )
            RecipeReplicator.processRecipeFile( path );
    }

    private static void processLooseResourcePack(
        TileDataLoader tileDataLoader,
        String resourcePackPath,
        boolean inOurDomain )
        throws IOException
    {
        File[] unpackedTileDataDomainDirs = Paths.get( resourcePackPath )
            .resolve( ResourcePackTileDataPath )
            .toFile()
            .listFiles( ( file , s ) -> file.isDirectory() );

        if( unpackedTileDataDomainDirs != null )
        {
            for( File unpackedTileDataDomainDir : unpackedTileDataDomainDirs )
            {
                if( unpackedTileDataDomainDir.getName().equalsIgnoreCase( Strata.modid ) == inOurDomain )
                {
                    List< Path > tileDataFilePaths = Files.walk( unpackedTileDataDomainDir.toPath() )
                        .filter( Files::isRegularFile )
                        .collect( Collectors.toList() );

                    for( Path tileDataFilePath : tileDataFilePaths )
                        tileDataLoader.loadFile( tileDataFilePath.toString() );
                }
            }
        }

        File[] unpackedRecipeDomainDirs = Paths.get( resourcePackPath )
            .resolve( ResourcePackRecipePath )
            .toFile()
            .listFiles( ( file , s ) -> file.isDirectory() );

        if( unpackedRecipeDomainDirs != null )
        {
            for( File unpackedRecipeDomainDir : unpackedRecipeDomainDirs )
            {
                if( unpackedRecipeDomainDir.getName().equalsIgnoreCase( Strata.modid ) == inOurDomain )
                {
                    List< Path > recipeFilePaths = Files.walk( unpackedRecipeDomainDir.toPath() )
                        .filter( Files::isRegularFile )
                        .collect( Collectors.toList() );

                    for( Path recipeFilePath : recipeFilePaths )
                        RecipeReplicator.processRecipeFile( recipeFilePath.toAbsolutePath().toString() );
                }
            }
        }
    }

    private static void processCompressedResourcePack(
        TileDataLoader tileDataLoader,
        String resourcePackFilePath,
        boolean inOurDomain )
        throws IOException
    {
        ZipFile zipFile;

        try
        {
            zipFile = new ZipFile( resourcePackFilePath );
        }
        catch( ZipException e )
        {
            // Not a ZIP?
            return;
        }

        Enumeration< ? extends ZipEntry > entries = zipFile.entries();
        while( entries.hasMoreElements() )
        {
            ZipEntry zipEntry = entries.nextElement();
            if( zipEntry.isDirectory() )
                continue;

            String zipEntryName = zipEntry.getName();

            Matcher matcher = ResourcePackTileDataPathRegex.matcher( zipEntryName );
            if( matcher.find() )
            {
                String modID = matcher.group( ResourcePathTileDataPathModIDGroup );
                if( modID.equalsIgnoreCase( Strata.modid ) == inOurDomain )
                    tileDataLoader.loadStream( zipFile.getInputStream( zipEntry ) );
            }

            matcher = ResourcePackRecipePathRegex.matcher( zipEntryName );
            if( matcher.find() )
            {
                String modID = matcher.group( ResourcePathRecipePathModIDGroup );
                if( modID.equalsIgnoreCase( Strata.modid ) == inOurDomain )
                    RecipeReplicator.processRecipeStream( zipFile.getInputStream( zipEntry ) );
            }
        }
    }
}
