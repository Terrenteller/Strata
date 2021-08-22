package com.riintouge.strata.block.loader;

import com.riintouge.strata.block.GenericCubeTextureMap;
import com.riintouge.strata.block.MetaResourceLocation;
import com.riintouge.strata.block.geo.TileType;
import com.riintouge.strata.image.BlendMode;
import com.riintouge.strata.image.LayeredTextureLayer;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.EnumPlantType;
import org.apache.commons.lang3.EnumUtils;

import java.util.*;

public class TileData
{
    // IGeoTileInfo
    public String tileSetName = null;
    public TileType type = null;
    public MetaResourceLocation equivalentItemResourceLocation = null;
    public List< LayeredTextureLayer > fragmentTextureLayers = null;
    public MetaResourceLocation equivalentFragmentItemResourceLocation = null;
    public ArrayList< EnumPlantType > sustainedPlantTypes = null;
    public ArrayList< MetaResourceLocation > sustainsPlantsSustainedByRaw = null;

    // IOreInfo
    public String oreName = null;
    public String blockOreDictionaryName = null;
    public String itemOreDictionaryName = null;
    public MetaResourceLocation proxyOreResourceLocation = null;
    public Integer burnTime = null;
    public Integer baseDropAmount = null;
    public String bonusDropExpr = null;
    public Integer baseExp = null;
    public String bonusExpExpr = null;

    // IHostInfo
    public ResourceLocation hostRegistryName = null;
    public Integer hostMeta = null;

    // IGenericBlockProperties
    public Material material = null;
    public SoundType soundType = null;
    public String harvestTool = null;
    public Integer harvestLevel = null;
    public Float hardness = null;
    public Float explosionResistance = null;

    // Shared
    public boolean isHost = false;
    public GenericCubeTextureMap textureMap = null;

    public boolean processKeyValue( String key , String value )
    {
        switch( key )
        {
            case "burnTime":
            {
                burnTime = Integer.parseInt( value );
                return true;
            }
            case "convertsTo":
            {
                String[] values = value.split( " " );
                int meta = values.length > 1 ? Integer.parseInt( values[ 1 ] ) : 0;
                equivalentItemResourceLocation = new MetaResourceLocation( values[ 0 ] , meta );
                return true;
            }
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
            case "fragmentConvertsTo":
            {
                String[] values = value.split( " " );
                int meta = values.length > 1 ? Integer.parseInt( values[ 1 ] ) : 0;
                equivalentFragmentItemResourceLocation = new MetaResourceLocation( values[ 0 ] , meta );
                return true;
            }
            case "fragmentTexture":
            {
                fragmentTextureLayers = parseTextureLayers( value );
                return true;
            }
            case "generate":
            {
                String[] values = value.split( " " );
                tileSetName = values[ 0 ].toLowerCase();
                processKeyValue( "type" , values[ 1 ] );
                return true;
            }
            case "hardness":
            {
                hardness = Float.parseFloat( value );
                return true;
            }
            case "harvestLevel":
            {
                harvestLevel = Integer.parseInt( value );
                return true;
            }
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
            case "ore":
            {
                String[] values = value.split( " " );
                oreName = values[ 0 ];
                blockOreDictionaryName = values.length > 1 && values[ 1 ].compareTo( "-" ) != 0 ? values[ 1 ] : null;
                itemOreDictionaryName = values.length > 2
                    ? ( values[ 2 ].compareTo( "-" ) != 0 ? values[ 2 ] : null )
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
            case "resistance":
            {
                explosionResistance = Float.parseFloat( value );
                return true;
            }
            case "sustains":
            {
                sustainedPlantTypes = new ArrayList<>();
                sustainsPlantsSustainedByRaw = new ArrayList<>();
                String resourceLocation = null;

                for( String token : value.split( " " ) )
                {
                    try
                    {
                        int meta = Integer.parseInt( token );
                        if( resourceLocation != null )
                        {
                            sustainsPlantsSustainedByRaw.add( new MetaResourceLocation( resourceLocation , meta ) );
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
                            sustainsPlantsSustainedByRaw.add( new MetaResourceLocation( resourceLocation , 0 ) );
                        resourceLocation = token;
                        continue;
                    }

                    sustainedPlantTypes.add( EnumPlantType.getPlantType( token ) );
                }

                if( resourceLocation != null )
                    sustainsPlantsSustainedByRaw.add( new MetaResourceLocation( resourceLocation , 0 ) );

                return true;
            }
            case "type":
            {
                type = TileType.valueOf( value.toUpperCase() );
                harvestTool = type.harvestTool;
                // Obsfucation prevents us from using reflection
                // to get non-enum Material and SoundType values from distinct KVs
                material = type.material;
                soundType = type.soundType;
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

                if( textureMap == null )
                {
                    // FIXME: Using tileSetName, oreName, and type here violates the assumption that lines can be in any order
                    String registryName = tileSetName != null
                        ? type.registryName( tileSetName ).getResourcePath()
                        : oreName != null
                            ? type.registryName( oreName ).getResourcePath()
                            : String.format( "%s_%d" , hostRegistryName.getResourcePath() , hostMeta );

                    textureMap = new GenericCubeTextureMap( registryName );
                }

                textureMap.set( facing , layerArray );
                return true;
            }
        }

        return false;
    }

    // Statics

    public static List< LayeredTextureLayer > parseTextureLayers( String value )
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
}
