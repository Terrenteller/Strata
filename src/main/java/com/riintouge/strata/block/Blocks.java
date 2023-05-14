package com.riintouge.strata.block;

import com.riintouge.strata.Strata;
import com.riintouge.strata.block.loader.TileDataLoader;
import com.riintouge.strata.resource.ConfigDir;
import com.riintouge.strata.resource.ResourcePacksDir;
import com.riintouge.strata.util.DebugUtil;
import net.minecraft.block.Block;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Enumeration;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

public final class Blocks
{
    private static final String STRATA_ASSET_CONFIG_PATH = String.format( "assets/%s/config/%s" , Strata.MOD_ID , Strata.MOD_ID );
    private static final String MOD_ID_REGEX = "[a-z0-9]+";

    private static final String RESOURCE_PACK_TILE_DATA_PATH = String.format( "%s/tiledata" , STRATA_ASSET_CONFIG_PATH );
    private static final String RESOURCE_PACK_TILE_DATA_PATH_REGEX = String.format( "^%s/(%s)" , RESOURCE_PACK_TILE_DATA_PATH , MOD_ID_REGEX );
    private static final Pattern RESOURCE_PACK_TILE_DATA_PATH_PATTERN = Pattern.compile( RESOURCE_PACK_TILE_DATA_PATH_REGEX );
    private static final int RESOURCE_PACK_TILE_DATA_PATH_MOD_ID_GROUP = 1;

    private static final String RESOURCE_PACK_RECIPE_PATH = String.format( "%s/recipe" , STRATA_ASSET_CONFIG_PATH );
    private static final String RESOURCE_PACK_RECIPE_PATH_REGEX = String.format( "^%s/(%s)" , RESOURCE_PACK_RECIPE_PATH , MOD_ID_REGEX );
    private static final Pattern RESOURCE_PACK_RECIPE_PATH_PATTERN = Pattern.compile( RESOURCE_PACK_RECIPE_PATH_REGEX );
    private static final int RESOURCE_PACK_RECIPE_PATH_MOD_ID_GROUP = 1;

    @SubscribeEvent( priority = EventPriority.LOWEST )
    public static void registerBlocks( RegistryEvent.Register< Block > event ) throws IOException
    {
        Strata.LOGGER.trace( "Blocks::registerBlocks()" );

        TileDataLoader tileDataLoader = new TileDataLoader();
        ConfigDir configDir = new ConfigDir();
        List< String > activeResourcePackPaths = ( new ResourcePacksDir() ).activeResourcePackPaths();

        // Priority #1: Config files in our domain
        processConfigFilesForMod( tileDataLoader , configDir , Strata.MOD_ID );

        // Priority #2: Config files outside our domain
        for( ModContainer mod : Loader.instance().getIndexedModList().values() )
        {
            String modID = mod.getModId();
            if( !modID.equalsIgnoreCase( Strata.MOD_ID ) )
                processConfigFilesForMod( tileDataLoader , configDir , modID );
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
    }

    private static void processConfigFilesForMod( TileDataLoader tileDataLoader , ConfigDir configDir , String modID ) throws IOException
    {
        String tileDataDir = String.format( "%s/tiledata/%s" , Strata.MOD_ID , modID );
        for( String path : configDir.allIn( tileDataDir , true ) )
        {
            try
            {
                tileDataLoader.loadFile( path );
            }
            catch( Exception e )
            {
                Strata.LOGGER.error( DebugUtil.prettyPrintThrowable( e , String.format( "Saw %%s while loading '%s'!" , path ) ) );
                throw e;
            }
        }

        String recipeDataDir = String.format( "%s/recipe/%s" , Strata.MOD_ID , modID );
        for( String path : configDir.allIn( recipeDataDir , true ) )
        {
            try
            {
                RecipeReplicator.processRecipeFile( path );
            }
            catch( Exception e )
            {
                Strata.LOGGER.error( DebugUtil.prettyPrintThrowable( e , String.format( "Saw %%s while loading '%s'!" , path ) ) );
                throw e;
            }
        }
    }

    private static void processLooseResourcePack(
        TileDataLoader tileDataLoader,
        String resourcePackPath,
        boolean inOurDomain )
        throws IOException
    {
        File[] unpackedTileDataDomainDirs = Paths.get( resourcePackPath )
            .resolve( RESOURCE_PACK_TILE_DATA_PATH )
            .toFile()
            .listFiles( ( file , s ) -> file.isDirectory() );

        if( unpackedTileDataDomainDirs != null )
        {
            for( File unpackedTileDataDomainDir : unpackedTileDataDomainDirs )
            {
                if( unpackedTileDataDomainDir.getName().equalsIgnoreCase( Strata.MOD_ID ) == inOurDomain )
                {
                    List< String > tileDataFilePaths = Files.walk( unpackedTileDataDomainDir.toPath() )
                        .filter( Files::isRegularFile )
                        .map( x -> x.toAbsolutePath().toString() )
                        .collect( Collectors.toList() );

                    for( String tileDataFilePath : tileDataFilePaths )
                    {
                        try
                        {
                            tileDataLoader.loadFile( tileDataFilePath );
                        }
                        catch( Exception e )
                        {
                            Strata.LOGGER.error( DebugUtil.prettyPrintThrowable( e , String.format( "Saw %%s while loading '%s'!" , tileDataFilePath ) ) );
                            throw e;
                        }
                    }
                }
            }
        }

        File[] unpackedRecipeDomainDirs = Paths.get( resourcePackPath )
            .resolve( RESOURCE_PACK_RECIPE_PATH )
            .toFile()
            .listFiles( ( file , s ) -> file.isDirectory() );

        if( unpackedRecipeDomainDirs != null )
        {
            for( File unpackedRecipeDomainDir : unpackedRecipeDomainDirs )
            {
                if( unpackedRecipeDomainDir.getName().equalsIgnoreCase( Strata.MOD_ID ) == inOurDomain )
                {
                    List< String > recipeFilePaths = Files.walk( unpackedRecipeDomainDir.toPath() )
                        .filter( Files::isRegularFile )
                        .map( x -> x.toAbsolutePath().toString() )
                        .collect( Collectors.toList() );

                    for( String recipeFilePath : recipeFilePaths )
                    {
                        try
                        {
                            RecipeReplicator.processRecipeFile( recipeFilePath );
                        }
                        catch( Exception e )
                        {
                            Strata.LOGGER.error( DebugUtil.prettyPrintThrowable( e , String.format( "Saw %%s while loading '%s'!" , recipeFilePath ) ) );
                            throw e;
                        }
                    }
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
            // Not a ZIP? Don't warn because we may end up with a lot of false positives.
            return;
        }

        Enumeration< ? extends ZipEntry > entries = zipFile.entries();
        while( entries.hasMoreElements() )
        {
            ZipEntry zipEntry = entries.nextElement();
            if( zipEntry.isDirectory() )
                continue;

            String zipEntryName = zipEntry.getName();
            try
            {
                Matcher matcher = RESOURCE_PACK_TILE_DATA_PATH_PATTERN.matcher( zipEntryName );
                if( matcher.find() )
                {
                    String modID = matcher.group( RESOURCE_PACK_TILE_DATA_PATH_MOD_ID_GROUP );
                    if( modID.equalsIgnoreCase( Strata.MOD_ID ) == inOurDomain )
                        tileDataLoader.loadStream( zipFile.getInputStream( zipEntry ) );
                }
                else
                {
                    matcher = RESOURCE_PACK_RECIPE_PATH_PATTERN.matcher( zipEntryName );
                    if( matcher.find() )
                    {
                        String modID = matcher.group( RESOURCE_PACK_RECIPE_PATH_MOD_ID_GROUP );
                        if( modID.equalsIgnoreCase( Strata.MOD_ID ) == inOurDomain )
                            RecipeReplicator.processRecipeStream( zipFile.getInputStream( zipEntry ) );
                    }
                }
            }
            catch( Exception e )
            {
                Strata.LOGGER.error( DebugUtil.prettyPrintThrowable( e , String.format( "Saw %%s while loading '%s' from '%s'!" , zipEntryName , resourcePackFilePath ) ) );
                throw e;
            }
        }
    }
}
