package com.riintouge.strata.block.loader;

import com.riintouge.strata.Strata;
import com.riintouge.strata.block.GenericCubeTextureMap;
import com.riintouge.strata.block.MetaResourceLocation;
import com.riintouge.strata.block.geo.*;
import com.riintouge.strata.block.ore.*;
import com.riintouge.strata.image.BlendMode;
import com.riintouge.strata.image.LayeredTextureLayer;
import com.riintouge.strata.util.Util;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.EnumPlantType;
import org.apache.commons.lang3.EnumUtils;

import java.io.*;
import java.util.*;

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
    private Float explosionResistance;

    // IHostInfo
    private ResourceLocation hostRegistryName;
    private int hostMeta;

    // IGeoTileInfo
    private String tileSetName;
    private TileType type;
    private MetaResourceLocation equivalentItemResourceLocation;
    private ArrayList< EnumPlantType > sustainedPlantTypes;
    private ArrayList< MetaResourceLocation > sustainedPlantTypesOfBlock;

    // IOreInfo
    private String oreName;
    private String blockOreDictionaryName;
    private String itemOreDictionaryName;
    private MetaResourceLocation proxyOreResourceLocation;
    private int burnTime;
    private int baseDropAmount;
    private String bonusDropExpr;
    private int baseExp;
    private String bonusExpExpr;

    // Shared
    private ResourceLocation textureResource;
    private GenericCubeTextureMap textureMap;

    public TileLoader()
    {
        reset();
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
            case "textureResource":
                textureResource = new ResourceLocation( value );
                return true;
            case "generate":
            {
                String[] values = value.split( " " );
                tileSetName = values[ 0 ];
                processKeyValue( "type" , values[ 1 ] );
                return true;
            }
            case "type":
                type = TileType.valueOf( value.toUpperCase() );
                harvestTool = type.harvestTool;
                // Obsfucation prevents us from using reflection
                // to get non-enum Material and SoundType values from distinct KVs
                material = type.material;
                soundType = type.soundType;
                return true;
            case "host":
            {
                isHost = true;
                if( !value.isEmpty() )
                {
                    String[] values = value.split( " " );
                    hostRegistryName = new ResourceLocation( values[ 0 ] );
                    if( values.length > 1 )
                        hostMeta = Integer.parseInt( values[ 1 ] );
                }
                return true;
            }
            case "harvestLevel":
                harvestLevel = Integer.parseInt( value );
                return true;
            case "hardness":
                hardness = Float.parseFloat( value );
                return true;
            case "resistance":
                explosionResistance = Float.parseFloat( value );
                return true;
            case "convertsTo":
            {
                String[] values = value.split( " " );
                int meta = values.length > 1 ? Integer.parseInt( values[ 1 ] ) : 0;
                equivalentItemResourceLocation = new MetaResourceLocation( values[ 0 ] , meta );
                return true;
            }
            case "sustains":
            {
                sustainedPlantTypes = new ArrayList<>();
                sustainedPlantTypesOfBlock = new ArrayList<>();
                String resourceLocation = null;

                for( String token : value.split( " " ) )
                {
                    try
                    {
                        int meta = Integer.parseInt( token );
                        if( resourceLocation != null )
                        {
                            sustainedPlantTypesOfBlock.add( new MetaResourceLocation( resourceLocation , meta ) );
                            resourceLocation = null;
                        }
                        continue; // Stray number
                    }
                    catch( NumberFormatException e )
                    {
                        // Not unexpected; ignore
                    }

                    if( token.contains( ":" ) )
                    {
                        if( resourceLocation != null )
                            sustainedPlantTypesOfBlock.add( new MetaResourceLocation( resourceLocation , 0 ) );
                        resourceLocation = token;
                        continue;
                    }

                    sustainedPlantTypes.add( EnumPlantType.getPlantType( token ) );
                }

                if( resourceLocation != null )
                    sustainedPlantTypesOfBlock.add( new MetaResourceLocation( resourceLocation , 0 ) );

                return true;
            }
            case "ore":
            {
                String[] values = value.split( " " );
                oreName = values[ 0 ];
                blockOreDictionaryName = values.length > 1 && values[ 1 ].compareTo( "-" ) != 0 ? values[ 1 ] : null;
                itemOreDictionaryName = values.length > 2
                    ? values[ 2 ].compareTo( "-" ) != 0 ? values[ 2 ] : null
                    : blockOreDictionaryName;
                return true;
            }
            case "proxy":
            {
                String[] values = value.split( " " );
                int meta = 0;
                if( values.length > 1 )
                    meta = Integer.parseInt( values[ 1 ] );
                proxyOreResourceLocation = new MetaResourceLocation( values[ 0 ] , meta );
                return true;
            }
            case "burnTime":
                burnTime = Integer.parseInt( value );
                return true;
            case "drops":
            {
                String[] values = value.split( " " , 2 );
                baseDropAmount = Math.max( 0 , Integer.parseInt( values[ 0 ] ) );
                if( values.length > 1 )
                    bonusDropExpr = values[ 1 ];
                return true;
            }
            case "exp":
            {
                String[] values = value.split( " " , 2 );
                baseExp = Integer.parseInt( values[ 0 ] );
                if( values.length > 1 )
                    bonusExpExpr = values[ 1 ];
                return true;
            }
        }

        if( key.startsWith( "texture" ) )
        {
            String facingString = key.substring( "texture".length() ).toUpperCase();
            GenericCubeTextureMap.Layer facing = EnumUtils.isValidEnum( GenericCubeTextureMap.Layer.class , facingString )
                ? GenericCubeTextureMap.Layer.valueOf( facingString )
                : facingString.isEmpty()
                    ? GenericCubeTextureMap.Layer.ALL
                    : null;
            if( facing != null )
            {
                List< LayeredTextureLayer > layers = parseTextureLayers( value );
                LayeredTextureLayer[] layerArray = new LayeredTextureLayer[ layers.size() ];
                layers.toArray( layerArray );
                // FIXME: Using tileSetName, oreName, and type here violates the assumption that lines can be in any order
                if( textureMap == null )
                    textureMap = new GenericCubeTextureMap( type.registryName( tileSetName.isEmpty() ? oreName : tileSetName ).getResourcePath() );
                textureMap.set( facing , layerArray );
                return true;
            }
        }

        return false;
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
            ImmutableTile tile = new ImmutableTile(
                tileSetName,
                0,
                type,
                material,
                soundType,
                harvestTool,
                harvestLevel,
                hardness,
                explosionResistance != null ? explosionResistance : 5.0f * hardness,
                burnTime,
                textureMap,
                equivalentItemResourceLocation,
                sustainedPlantTypes,
                sustainedPlantTypesOfBlock );

            GeoTileSet tileSet = getOrCreateTileSet( tileSetName );
            tileSet.addTile( tile );

            if( isHost )
                HostRegistry.INSTANCE.register( tile.registryName() , tile.meta() , tile );
        }
        else if( hostRegistryName != null && isHost )
        {
            ImmutableHost host = new ImmutableHost(
                hostRegistryName,
                hostMeta,
                textureResource,
                material,
                soundType,
                harvestTool,
                harvestLevel,
                hardness,
                explosionResistance != null ? explosionResistance : 5.0f * hardness );

            HostRegistry.INSTANCE.register( host.registryName() , host.meta() , host );
        }
        else if( !oreName.isEmpty() )
        {
            ImmutableOre ore = new ImmutableOre(
                oreName,
                blockOreDictionaryName,
                itemOreDictionaryName,
                textureMap,
                proxyOreResourceLocation,
                equivalentItemResourceLocation,
                material,
                soundType,
                harvestTool,
                harvestLevel,
                hardness,
                explosionResistance != null ? explosionResistance : 1.7f * hardness,
                burnTime,
                baseDropAmount,
                bonusDropExpr,
                baseExp,
                bonusExpExpr );

            OreRegistry.INSTANCE.register( new OreTileSet( ore ) );
            OreBlockTextureManager.INSTANCE.registerOre( Strata.resource( ore.oreName() ) , 0 , ore );
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
        explosionResistance = null;

        // IHostInfo
        hostRegistryName = null;
        hostMeta = 0;

        // IGeoTileInfo
        tileSetName = "";
        type = null;
        equivalentItemResourceLocation = null;
        sustainedPlantTypes = null;
        sustainedPlantTypesOfBlock = null;

        // IOreInfo
        oreName = null;
        blockOreDictionaryName = null;
        itemOreDictionaryName = null;
        proxyOreResourceLocation = null;
        burnTime = 0;
        baseDropAmount = 1;
        bonusDropExpr = null;
        baseExp = 0;
        bonusExpExpr = null;

        // Shared
        textureResource = null;
        textureMap = null;
    }
}
