package com.riintouge.strata.block.loader;

import com.riintouge.strata.Strata;
import com.riintouge.strata.block.geo.GeoTileSet;
import com.riintouge.strata.block.geo.GeoTileSetRegistry;
import com.riintouge.strata.block.geo.HostRegistry;
import com.riintouge.strata.block.geo.TileType;
import com.riintouge.strata.block.ore.OreParticleTextureManager;
import com.riintouge.strata.block.ore.OreItemTextureManager;
import com.riintouge.strata.block.ore.OreRegistry;
import com.riintouge.strata.block.ore.OreTileSet;
import com.riintouge.strata.util.Util;

import javax.naming.OperationNotSupportedException;
import java.io.*;
import java.util.*;

public class TileDataLoader
{
    private Map< String , Map< TileType , TileData > > tileSetTileDataMap = null;

    public TileDataLoader()
    {
        // Nothing to do
    }

    public void loadResource( String path ) throws IOException
    {
        InputStream stream = getClass().getClassLoader().getResourceAsStream( path );
        load( new BufferedReader( new InputStreamReader( stream , "UTF-8" ) ) );
    }

    public void loadFile( String path ) throws IOException
    {
        InputStream stream = new FileInputStream( path );
        load( new BufferedReader( new InputStreamReader( stream , "UTF-8" ) ) );
    }

    public void load( BufferedReader buffer ) throws IOException
    {
        try
        {
            tileSetTileDataMap = new HashMap<>();

            boolean meaningfulLineProcessed = false;
            TileData data = new TileData();

            while( buffer.ready() )
            {
                String line = buffer.readLine().trim();

                if( line.isEmpty() )
                {
                    if( meaningfulLineProcessed )
                    {
                        processTileData( data );
                        meaningfulLineProcessed = false;
                        data = new TileData();
                    }
                }
                else if( line.charAt( 0 ) != '#' )
                {
                    String[] kv = Util.splitKV( line );
                    data.processKeyValue( kv[ 0 ] , kv[ 1 ] );
                    meaningfulLineProcessed = true;
                }
            }
            buffer.close();

            if( meaningfulLineProcessed )
                processTileData( data );

            finalizeTileSets();
        }
        finally
        {
            tileSetTileDataMap = null;
        }
    }

    protected void processTileData( TileData tileData )
    {
        if( tileData.tileSetName != null && tileData.type != null )
        {
            String tileSetName = tileData.tileSetName.toLowerCase();
            Map< TileType , TileData > tileDataMap = tileSetTileDataMap.computeIfAbsent( tileSetName , x -> new HashMap<>() );

            for( TileType parentType = tileData.type.parentType ; parentType != null ; parentType = parentType.parentType )
            {
                TileData parentData = tileDataMap.getOrDefault( parentType , null );
                if( parentData == null )
                    throw new UnsupportedOperationException( String.format( "'%s' child type '%s' loaded before parent type '%s'!" , tileData.tileSetName , tileData.type.toString() , parentType.toString() ) );

                // Only select KVs make sense to propagate from parent to child
                if( tileData.harvestLevel == null )
                    tileData.harvestLevel = parentData.harvestLevel;
                if( tileData.hardness == null )
                    tileData.hardness = parentData.hardness;
                if( tileData.explosionResistance == null )
                    tileData.explosionResistance = parentData.explosionResistance;

                // Surprising as it may be, the the texture map is also inheritable.
                // It will have already been initialized with the owner's registry name which prevents duplication.
                if( tileData.textureMap == null )
                    tileData.textureMap = parentData.textureMap;
            }

            tileDataMap.put( tileData.type , tileData );
        }
        else if( tileData.hostRegistryName != null )
        {
            ImmutableHost host;

            try
            {
                host = new ImmutableHost( tileData );
            }
            catch( IllegalArgumentException e )
            {
                String informativeMessage = String.format(
                    "Failed to create host '%s:%d' with invalid '%s'!",
                    tileData.hostRegistryName.toString(),
                    tileData.hostMeta,
                    e.getMessage() );

                throw new IllegalArgumentException( informativeMessage , e );
            }

            HostRegistry.INSTANCE.register( host.registryName() , host.meta() , host );
        }
        else if( tileData.oreName != null )
        {
            ImmutableOre ore;

            try
            {
                ore = new ImmutableOre( tileData );
            }
            catch( IllegalArgumentException e )
            {
                String informativeMessage = String.format(
                    "Failed to create ore '%s' with invalid '%s'!",
                    tileData.oreName,
                    e.getMessage() );

                throw new IllegalArgumentException( informativeMessage , e );
            }

            OreRegistry.INSTANCE.register( new OreTileSet( ore ) );
            OreParticleTextureManager.INSTANCE.registerOre( Strata.resource( ore.oreName() ) , ore );
            OreItemTextureManager.INSTANCE.register( ore.oreName() , ore.oreItemTextureResource() );
        }

        // Not all data is guaranteed to be meaningful
    }

    protected void finalizeTileSets()
    {
        Map< String , GeoTileSet > tileSetMap = new HashMap<>();

        for( String tileSetName : tileSetTileDataMap.keySet() )
        {
            GeoTileSet tileSet = tileSetMap.computeIfAbsent( tileSetName , x -> new GeoTileSet() );
            Map< TileType , TileData > tileDataMap = tileSetTileDataMap.get( tileSetName );

            for( TileType type : TileType.values() )
            {
                TileData tileData = tileDataMap.getOrDefault( type , null );
                if( tileData == null )
                {
                    if( type.parentType == null )
                        continue;

                    // All tertiary types are meant to be defined in config files, but double slabs are special
                    try
                    {
                        TileData parentData = tileDataMap.get( type.parentType );
                        if( parentData != null )
                            tileDataMap.put( type , tileData = parentData.createMissingChildType( type ) );
                        else
                            continue;
                    }
                    catch( OperationNotSupportedException e )
                    {
                        e.printStackTrace();
                    }
                }

                ImmutableTile tile;
                try
                {
                    tile = new ImmutableTile( tileData );
                }
                catch( IllegalArgumentException e )
                {
                    String informativeMessage = String.format(
                        "Failed to create '%s' for '%s' with invalid '%s'!",
                        tileData.type.toString(),
                        tileSetName,
                        e.getMessage() );

                    throw new IllegalArgumentException( informativeMessage , e );
                }

                tileSet.addTile( tile );
                if( tileData.isHost && tileData.type.isPrimary )
                    HostRegistry.INSTANCE.register( tile.registryName() , tile.meta() , tile );
            }
        }

        for( String key : tileSetMap.keySet() )
            GeoTileSetRegistry.INSTANCE.register( tileSetMap.get( key ) , key );
    }
}
