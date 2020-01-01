package com.riintouge.strata.block.loader;

import com.riintouge.strata.Strata;
import com.riintouge.strata.block.geo.*;
import com.riintouge.strata.block.ore.*;
import com.riintouge.strata.image.BlendMode;
import com.riintouge.strata.image.LayeredTextureLayer;
import com.riintouge.strata.util.Util;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

public class TileLoader
{
    private Map< String , GeoTileSet > tileSetMap = new HashMap<>();
    private boolean isHost = false;

    // IGenericBlockProperties
    private Material material;
    private SoundType soundType;
    private String harvestTool;
    private int harvestLevel;
    private float hardness;
    private float explosionResistance;

    // IHostInfo
    private ResourceLocation registryName;
    private int meta;

    // IGeoTileInfo
    private String tileSetName;
    private TileType type;
    private List< LayeredTextureLayer > layers;
    private ItemStack vanillaEquivalent;

    // IOreInfo
    private String oreName;
    private String oreDictionaryName;
    private ResourceLocation proxyOre;

    // Shared
    private ResourceLocation textureResource;

    public TileLoader()
    {
        reset();
    }

    public void load( String path ) throws IOException
    {
        InputStream stream = getClass().getClassLoader().getResourceAsStream( path );
        BufferedReader buffer = new BufferedReader( new InputStreamReader( stream , "UTF-8" ) );
        boolean meaningfulLineProcessed = false;

        while( buffer.ready() )
        {
            String line = buffer.readLine().trim();

            if( line.isEmpty() )
            {
                if( meaningfulLineProcessed )
                {
                    createTileAndReset();
                    meaningfulLineProcessed = false;
                }
            }
            else if( line.charAt( 0 ) != '#' )
            {
                String[] kv = Util.splitKV( line );
                meaningfulLineProcessed = processKeyValue( kv[ 0 ] , kv[ 1 ] );
            }
        }
        buffer.close();

        if( meaningfulLineProcessed )
            createTileAndReset();

        for( String key : tileSetMap.keySet() )
            GeoTileSetRegistry.INSTANCE.register( tileSetMap.get( key ) , key );
    }

    private boolean processKeyValue( String key , String value )
    {
        switch( key )
        {
            case "registryName":
                registryName = new ResourceLocation( value );
                break;
            case "textureResource":
                textureResource = new ResourceLocation( value );
                break;
            case "tileset":
                tileSetName = value;
                break;
            case "type":
                type = TileType.valueOf( value.toUpperCase() );
                // Obsfucation unfortunately prevents us from using reflection
                // to get non-enum Material and SoundType values from distinct KVs
                harvestTool = type.harvestTool;
                material = type.material;
                soundType = type.soundType;
                break;
            case "host":
                isHost = true;
                break;
            case "meta":
                meta = Integer.parseInt( value );
                break;
            case "harvestLevel":
                harvestLevel = Integer.parseInt( value );
                break;
            case "hardness":
                hardness = Float.parseFloat( value );
                break;
            case "resistance":
                explosionResistance = Float.parseFloat( value );
                break;
            case "texture":
                layers = parseTextureLayers( value );
                break;
            case "vanillaItem":
            {
                String[] values = value.split( " " );
                vanillaEquivalent = new ItemStack(
                    Item.getByNameOrId( values[ 0 ] ),
                    1,
                    values.length > 1 ? Integer.parseInt( values[ 1 ] ) : 0 );
                break;
            }
            case "ore":
            {
                String[] values = value.split( " " );
                oreName = values[ 0 ];
                if( values.length > 1 )
                    oreDictionaryName = values[ 1 ];
                break;
            }
            case "proxy":
                proxyOre = new ResourceLocation( value );
                break;
            default:
                return false;
        }

        return true;
    }

    private List< LayeredTextureLayer > parseTextureLayers( String value )
    {
        List< LayeredTextureLayer > layers = new Vector<>();

        String[] components = value.split( " " );
        if( ( ( components.length - 1 ) % 3 ) != 0 )
            return layers;

        if( components.length > 1 )
        {
            for( int index = 0 ; index < ( components.length - 1 ) ; index += 3 )
            {
                layers.add( new LayeredTextureLayer(
                    new ResourceLocation( components[ index ] ),
                    BlendMode.valueOf( components[ index + 1 ].toUpperCase() ),
                    Float.parseFloat( components[ index + 2 ] ) ) );
            }
        }

        layers.add( new LayeredTextureLayer( new ResourceLocation( components[ components.length - 1 ] ) ) );
        return layers;
    }

    private GeoTileSet getOrCreateTileSet( String name )
    {
        GeoTileSet tileSet = tileSetMap.getOrDefault( name , null );
        if( tileSet == null )
            tileSetMap.put( name , tileSet = new GeoTileSet() );

        return tileSet;
    }

    private void createTileAndReset()
    {
        if( !tileSetName.isEmpty() )
        {
            LayeredTextureLayer[] layerArray = new LayeredTextureLayer[ layers.size() ];
            layers.toArray( layerArray );

            ImmutableTile tile = new ImmutableTile(
                tileSetName,
                meta,
                type,
                material,
                soundType,
                harvestTool,
                harvestLevel,
                hardness,
                explosionResistance,
                layerArray,
                vanillaEquivalent );

            GeoTileSet tileSet = getOrCreateTileSet( tileSetName );
            tileSet.addTile( tile );

            if( isHost )
                HostRegistry.INSTANCE.register( tile.registryName() , tile.meta() , tile );
        }
        else if( registryName != null && isHost )
        {
            ImmutableHost host = new ImmutableHost(
                registryName,
                meta,
                textureResource,
                material,
                soundType,
                harvestTool,
                harvestLevel,
                hardness,
                explosionResistance );

            HostRegistry.INSTANCE.register( host.registryName() , host.meta() , host );
        }
        else if( !oreName.isEmpty() )
        {
            ImmutableOre ore = new ImmutableOre(
                oreName,
                oreDictionaryName,
                textureResource,
                proxyOre,
                material,
                soundType,
                harvestTool,
                harvestLevel,
                hardness,
                explosionResistance );

            OreRegistry.INSTANCE.register( new OreTileSet( ore ) );
            OreBlockTextureManager.INSTANCE.registerOre( new ResourceLocation( Strata.modid , ore.oreName() ) , 0 , ore );
            OreItemTextureManager.INSTANCE.register( ore.oreName() , ore.oreItemTextureResource() );
        }

        reset();
    }

    private void reset()
    {
        isHost = false;

        // IGenericBlockProperties
        material = null;
        soundType = null;
        harvestTool = "";
        harvestLevel = 0;
        hardness = 0.0f;
        explosionResistance = 0.0f;

        // IHostInfo
        registryName = null;
        meta = 0;

        // IGeoTileInfo
        tileSetName = "";
        type = null;
        layers = null;
        vanillaEquivalent = null;

        // IOreInfo
        oreName = null;
        oreDictionaryName = null;
        proxyOre = null;

        // Shared
        textureResource = null;
    }
}
