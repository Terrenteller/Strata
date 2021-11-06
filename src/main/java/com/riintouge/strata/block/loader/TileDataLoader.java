package com.riintouge.strata.block.loader;

import com.riintouge.strata.Strata;
import com.riintouge.strata.block.geo.GeoTileSet;
import com.riintouge.strata.block.geo.GeoTileSetRegistry;
import com.riintouge.strata.block.geo.HostRegistry;
import com.riintouge.strata.block.geo.TileType;
import com.riintouge.strata.block.ore.OreItemTextureManager;
import com.riintouge.strata.block.ore.OreRegistry;
import com.riintouge.strata.block.ore.OreTileSet;
import com.riintouge.strata.util.Util;

import javax.naming.OperationNotSupportedException;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class TileDataLoader
{
    private Map< String , Map< TileType , TileData > > tileSetTileDataMap = null;

    public TileDataLoader()
    {
        // Nothing to do
    }

    public void loadFile( String path ) throws IOException
    {
        loadStream( new FileInputStream( path ) );
    }

    public void loadStream( InputStream stream ) throws IOException
    {
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
                    meaningfulLineProcessed |= data.processKeyValue( kv[ 0 ] , kv[ 1 ] );
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
        if( tileData.tileSetName != null && tileData.tileType != null )
        {
            String tileSetName = tileData.tileSetName.toLowerCase();
            Map< TileType , TileData > tileDataMap = tileSetTileDataMap.computeIfAbsent( tileSetName , x -> new HashMap<>() );

            if( tileDataMap.containsKey( tileData.tileType ) )
            {
                Strata.LOGGER.warn( String.format( "Tile '%s' (%s) already loaded. Skipping!" , tileData.tileSetName , tileData.tileType.toString().toLowerCase() ) );
                return;
            }

            for( TileType parentType = tileData.tileType.parentType ; parentType != null ; parentType = parentType.parentType )
            {
                TileData parentData = tileDataMap.getOrDefault( parentType , null );
                if( parentData == null )
                    throw new UnsupportedOperationException( String.format( "'%s' child type '%s' loaded before parent type '%s'!" , tileData.tileSetName , tileData.tileType.toString() , parentType.toString() ) );

                // Only select KVs make sense to propagate from parent to child
                if( tileData.harvestLevel == null )
                    tileData.harvestLevel = parentData.harvestLevel;
                if( tileData.hardness == null )
                    tileData.hardness = parentData.hardness;
                if( tileData.explosionResistance == null )
                    tileData.explosionResistance = parentData.explosionResistance;

                // Surprising as it may be, the texture map is also inheritable.
                // It will have already been initialized with the owner's registry name which prevents duplication.
                if( tileData.textureMap == null )
                    tileData.textureMap = parentData.textureMap;

                // If our sound type matches what the parent would normally use, use whatever the parent actually is.
                // The sound type should never be null as it initially comes from the tile type.
                if( tileData.soundType == parentType.soundType )
                    tileData.soundType = parentData.soundType;
            }

            tileDataMap.put( tileData.tileType , tileData );
        }
        else if( tileData.hostMetaResource != null )
        {
            if( HostRegistry.INSTANCE.find( tileData.hostMetaResource ) != null )
            {
                Strata.LOGGER.warn( String.format( "Host '%s' already registered. Skipping!" , tileData.hostMetaResource.toString() ) );
                return;
            }

            ImmutableHost host;

            try
            {
                host = new ImmutableHost( tileData );
            }
            catch( IllegalArgumentException e )
            {
                String informativeMessage = String.format(
                    "Failed to create host '%s:%d' with invalid '%s'!",
                    tileData.hostMetaResource.resourceLocation.toString(),
                    tileData.hostMetaResource.meta,
                    e.getMessage() );

                throw new IllegalArgumentException( informativeMessage , e );
            }

            HostRegistry.INSTANCE.register( host.registryName() , host.meta() , host );
        }
        else if( tileData.oreName != null )
        {
            if( OreRegistry.INSTANCE.find( tileData.oreName ) != null )
            {
                Strata.LOGGER.warn( String.format( "Ore '%s' already registered. Skipping!" , tileData.oreName ) );
                return;
            }

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

            for( TileType tileType : TileType.values() )
            {
                TileData tileData = tileDataMap.getOrDefault( tileType , null );
                if( tileData == null )
                {
                    if( tileType.parentType == null )
                        continue;

                    // All tertiary types are meant to be defined in config files, but double slabs are special
                    switch( tileType )
                    {
                        case COBBLESLABS:
                        case STONESLABS:
                        case STONEBRICKSLABS:
                            break;
                        default:
                            continue;
                    }

                    try
                    {
                        TileData parentData = tileDataMap.get( tileType.parentType );
                        if( parentData != null )
                            tileDataMap.put( tileType , tileData = parentData.createMissingChildType( tileType ) );
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
                        tileData.tileType.toString(),
                        tileSetName,
                        e.getMessage() );

                    throw new IllegalArgumentException( informativeMessage , e );
                }

                tileSet.addTile( tile );
                if( tileData.isHost && tileData.tileType.isPrimary )
                    HostRegistry.INSTANCE.register( tile.registryName() , tile.meta() , tile );
            }
        }

        for( String key : tileSetMap.keySet() )
            GeoTileSetRegistry.INSTANCE.register( tileSetMap.get( key ) , key );
    }
}
