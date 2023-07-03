package com.riintouge.strata.block.loader;

import com.riintouge.strata.block.geo.GeoTileSet;
import com.riintouge.strata.block.geo.GeoTileSetRegistry;
import com.riintouge.strata.block.geo.TileType;
import com.riintouge.strata.block.host.HostRegistry;
import com.riintouge.strata.block.ore.OreRegistry;
import com.riintouge.strata.block.ore.OreTileSet;
import com.riintouge.strata.util.Util;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class TileDataLoader
{
    protected Map< String , Map< TileType , TileData > > tileSetTileDataMap = null;

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
        BufferedReader buffer = new BufferedReader( new InputStreamReader( stream , "UTF-8" ) );
        load( buffer );
        buffer.close();
    }

    public void load( BufferedReader buffer ) throws IOException
    {
        tileSetTileDataMap = new HashMap<>();

        try
        {
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
                else if( line.charAt( 0 ) != '#' && line.charAt( 0 ) != ';' && !line.startsWith( "//" ) )
                {
                    String[] kv = Util.splitKV( line );
                    meaningfulLineProcessed |= data.processKeyValue( kv[ 0 ] , kv[ 1 ] );
                }
            }

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
            TileType tileType = tileData.tileType;
            String tileSetName = tileData.tileSetName.toLowerCase();
            Map< TileType , TileData > tileDataMap = tileSetTileDataMap.computeIfAbsent( tileSetName , x -> new HashMap<>() );

            if( tileDataMap.containsKey( tileType ) )
            {
                throw new IllegalStateException(
                    String.format(
                        "Tile '%s' (%s) already loaded!",
                        tileData.tileSetName,
                        tileType.toString().toLowerCase() ) );
            }

            TileType parentType = tileType.parentType;
            if( parentType != null )
            {
                TileData parentData = tileDataMap.getOrDefault( parentType , null );
                if( parentData == null )
                {
                    throw new IllegalStateException(
                        String.format(
                            "'%s' child type '%s' loaded before parent type '%s'!",
                            tileData.tileSetName,
                            tileType.toString(),
                            parentType.toString() ) );
                }

                inheritFrom( tileData , parentData );
            }

            tileDataMap.put( tileType , tileData );

            // All tertiary types are meant to be defined in config files, but double slabs are special
            switch( tileType )
            {
                case COBBLESLAB:
                case STONESLAB:
                case STONEBRICKSLAB:
                {
                    TileType doubleSlabsType = TileType.values()[ tileType.ordinal() + 1 ];
                    TileData doubleSlabsData = new TileData();
                    doubleSlabsData.tileType = doubleSlabsType;
                    inheritFrom( doubleSlabsData , tileData );
                    tileDataMap.put( doubleSlabsType , doubleSlabsData );
                }
            }
        }
        else if( tileData.hostMetaResource != null )
        {
            ImmutableHost host;
            try
            {
                host = new ImmutableHost( tileData );
            }
            catch( NullPointerException e )
            {
                String informativeMessage = String.format(
                    "Failed to create host '%s:%d' with invalid '%s'!",
                    tileData.hostMetaResource.resourceLocation.toString(),
                    tileData.hostMetaResource.meta,
                    e.getMessage() );

                throw new IllegalStateException( informativeMessage , e );
            }

            HostRegistry.INSTANCE.register( host );
        }
        else if( tileData.oreName != null )
        {
            ImmutableOre ore;
            try
            {
                ore = new ImmutableOre( tileData );
            }
            catch( NullPointerException e )
            {
                String informativeMessage = String.format(
                    "Failed to create ore '%s' with invalid '%s'!",
                    tileData.oreName,
                    e.getMessage() );

                throw new IllegalStateException( informativeMessage , e );
            }

            OreRegistry.INSTANCE.register( new OreTileSet( ore ) );
        }
        else
            throw new IllegalArgumentException( "TileData does not represent a tile set, host, or ore!" );
    }

    protected void inheritFrom( TileData child , TileData parent )
    {
        // Only select KVs make sense to inherit
        if( child.tileSetName == null )
            child.tileSetName = parent.tileSetName;
        if( child.material == null )
            child.material = parent.material;
        if( child.harvestTool == null )
            child.harvestTool = parent.harvestTool;
        if( child.harvestLevel == null )
            child.harvestLevel = parent.harvestLevel;
        if( child.hardness == null )
            child.hardness = parent.hardness;
        if( child.explosionResistance == null )
            child.explosionResistance = parent.explosionResistance;
        if( child.lightLevel == null )
            child.lightLevel = parent.lightLevel;
        if( child.lightOpacity == null )
            child.lightOpacity = parent.lightOpacity;
        if( child.slipperiness == null )
            child.slipperiness = parent.slipperiness;
        if( child.ambientSound == null )
            child.ambientSound = parent.ambientSound;

        // The texture map only makes sense to inherit for tertiary tile types.
        // It will have already been initialized with the owner's registry name which prevents duplication.
        if( child.textureMap == null && child.tileType.tier == TileType.Tier.TERTIARY )
            child.textureMap = parent.textureMap;

        // If our sound type matches what the parent would normally use, use whatever the parent actually is.
        // The sound type may be null when we are initializing a double slab.
        if( child.soundType == null || child.soundType == parent.tileType.soundType )
            child.soundType = parent.soundType;

        // The child should not convert to anything if the parent does not convert to anything
        if( parent.equivalentItemResourceLocation == null )
            child.equivalentItemResourceLocation = null;
    }

    protected void finalizeTileSets()
    {
        Map< String , GeoTileSet > tileSetMap = new HashMap<>();

        for( String tileSetName : tileSetTileDataMap.keySet() )
        {
            GeoTileSet tileSet = tileSetMap.computeIfAbsent( tileSetName , x -> new GeoTileSet() );
            Map< TileType , TileData > tileDataMap = tileSetTileDataMap.get( tileSetName );

            for( TileData tileData : tileDataMap.values() )
            {
                ImmutableTile tile;
                try
                {
                    tile = new ImmutableTile( tileData );
                }
                catch( NullPointerException e )
                {
                    String informativeMessage = String.format(
                        "Failed to create '%s' for '%s' with invalid '%s'!",
                        tileData.tileType.toString(),
                        tileSetName,
                        e.getMessage() );

                    throw new IllegalStateException( informativeMessage , e );
                }

                tileSet.addTile( tile );
                if( tileData.isHost && tileData.tileType.isPrimary )
                    HostRegistry.INSTANCE.register( tile );
            }
        }

        for( String key : tileSetMap.keySet() )
            GeoTileSetRegistry.INSTANCE.register( tileSetMap.get( key ) , key );
    }
}
