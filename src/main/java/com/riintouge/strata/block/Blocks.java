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
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

public class Blocks
{
    private static final String ResourcePackTileDataPath = String.format( "assets/%s/config/%s/tiledata" , Strata.modid , Strata.modid );
    private static final String ResourcePathTileDataPathPattern = String.format( "^%s/([a-z0-9]+)" , ResourcePackTileDataPath );
    private static final int ResourcePathTileDataPathModIDGroup = 1;
    private static final Pattern ResourcePackTileDataPathRegex = Pattern.compile( ResourcePathTileDataPathPattern );

    @SubscribeEvent( priority = EventPriority.LOWEST )
    public static void registerBlocks( RegistryEvent.Register< Block > event ) throws IOException
    {
        Strata.LOGGER.trace( "Blocks::registerBlocks()" );

        // Priority #1: On-disk files in our domain
        TileDataLoader tileDataLoader = new TileDataLoader();
        String tileDataDir = String.format( "%s/tiledata/%s" , Strata.modid , Strata.modid );
        for( String path : ConfigDir.INSTANCE.allIn( tileDataDir , true ) )
            tileDataLoader.loadFile( path );

        List< String > activeResourcePackPaths = ResourcePacksDir.INSTANCE.activeResourcePackPaths();
        if( activeResourcePackPaths == null )
            activeResourcePackPaths = ResourcePacksDir.INSTANCE.find( s -> true , false );

        // Tile data outside our domain has a lower priority.
        // Cache where to load it from so we only make a single pass through resource packs.
        Set< File > otherUnpackedTileDataDomainDirs = new HashSet<>();
        Set< Pair< ZipFile , ZipEntry > > otherZipTileData = new HashSet<>();

        for( String path : activeResourcePackPaths )
        {
            if( new File( path ).isDirectory() )
                otherUnpackedTileDataDomainDirs.addAll( loadTileDataFromResourcePackDir( tileDataLoader , path ) );
            else
                otherZipTileData.addAll( loadTileDataFromResourcePackFile( tileDataLoader , path ) );
        }

        // Priority #3: On-disk files outside our domain
        for( ModContainer mod : Loader.instance().getIndexedModList().values() )
        {
            String modID = mod.getModId();
            if( !modID.equalsIgnoreCase( Strata.modid ) )
            {
                tileDataDir = String.format( "%s/tiledata/%s" , Strata.modid , modID );
                for( String path : ConfigDir.INSTANCE.allIn( tileDataDir , true ) )
                    tileDataLoader.loadFile( path );
            }
        }

        // Priority #4: On-disk resource pack files outside our domain
        for( File otherUnpackedTileDataDomainDir : otherUnpackedTileDataDomainDirs )
            tileDataLoader.loadFile( otherUnpackedTileDataDomainDir.toString() );

        // Priority #5: Resource pack files outside our domain
        for( Pair< ZipFile , ZipEntry > pair : otherZipTileData )
        {
            try( InputStream stream = pair.getKey().getInputStream( pair.getValue() ) )
            {
                tileDataLoader.loadStream( stream );
            }
        }

        GameRegistry.registerTileEntity(
            OreBlockTileEntity.class,
            new ResourceLocation( String.format( "%s:ore_tile_entity" , Strata.modid ) ) );
    }

    private static Set< File > loadTileDataFromResourcePackDir(
        TileDataLoader tileDataLoader,
        String resourcePackPath )
        throws IOException
    {
        Set< File > otherUnpackedTileDataDomainDirs = new HashSet<>();
        File resourcePackTileDataDir = Paths.get( resourcePackPath ).resolve( ResourcePackTileDataPath ).toFile();
        File[] unpackedTileDataDomainDirs = resourcePackTileDataDir.listFiles( ( file , s ) -> file.isDirectory() );
        if( unpackedTileDataDomainDirs != null )
        {
            for( File unpackedTileDataDomainDir : unpackedTileDataDomainDirs )
            {
                if( unpackedTileDataDomainDir.getName().equalsIgnoreCase( Strata.modid ) )
                {
                    // Priority #2: Resource pack files in our domain
                    List< Path > tileDataFilePaths = Files.walk( unpackedTileDataDomainDir.toPath() )
                        .filter( Files::isRegularFile )
                        .collect( Collectors.toList() );
                    for( Path tileDataFilePath : tileDataFilePaths )
                        tileDataLoader.loadFile( tileDataFilePath.toString() );
                }
                else
                {
                    // Priority #4: On-disk resource pack files outside our domain
                    otherUnpackedTileDataDomainDirs.add( unpackedTileDataDomainDir );
                }
            }
        }

        return otherUnpackedTileDataDomainDirs;
    }

    private static Set< Pair< ZipFile , ZipEntry > > loadTileDataFromResourcePackFile(
        TileDataLoader tileDataLoader,
        String resourcePackFilePath )
        throws IOException
    {
        Set< Pair< ZipFile , ZipEntry > > otherZipTileData = new HashSet<>();
        ZipFile zipFile;
        try
        {
            zipFile = new ZipFile( resourcePackFilePath );
        }
        catch( ZipException e )
        {
            // Not a ZIP?
            return otherZipTileData;
        }

        Enumeration< ? extends ZipEntry > entries = zipFile.entries();
        while( entries.hasMoreElements() )
        {
            ZipEntry zipEntry = entries.nextElement();
            if( zipEntry.isDirectory() )
                continue;

            String zipEntryName = zipEntry.getName();
            Matcher matcher = ResourcePackTileDataPathRegex.matcher( zipEntryName );
            if( !matcher.find() )
                continue;

            String modID = matcher.group( ResourcePathTileDataPathModIDGroup );
            if( modID.equalsIgnoreCase( Strata.modid ) )
            {
                // Priority #2: Resource pack files in our domain
                tileDataLoader.loadStream( zipFile.getInputStream( zipEntry ) );
            }
            else
            {
                // Priority #5: Resource pack files outside our domain
                otherZipTileData.add( new ImmutablePair<>( zipFile , zipEntry ) );
            }
        }

        return otherZipTileData;
    }
}
