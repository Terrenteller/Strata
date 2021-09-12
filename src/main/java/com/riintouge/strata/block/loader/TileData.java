package com.riintouge.strata.block.loader;

import com.riintouge.strata.block.GenericCubeTextureMap;
import com.riintouge.strata.block.MetaResourceLocation;
import com.riintouge.strata.block.geo.TileType;
import com.riintouge.strata.image.BlendMode;
import com.riintouge.strata.image.LayeredTextureLayer;
import com.riintouge.strata.util.Util;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.EnumPlantType;
import org.apache.commons.lang3.EnumUtils;

import javax.naming.OperationNotSupportedException;
import java.util.*;

public class TileData
{
    // IGeoTileInfo
    public String tileSetName = null;
    public TileType type = null;
    public MetaResourceLocation equivalentItemResourceLocation = null;
    public MetaResourceLocation furnaceResult = null;
    public Float furnaceExp = null;
    public List< LayeredTextureLayer > fragmentTextureLayers = null;
    public MetaResourceLocation equivalentFragmentItemResourceLocation = null;
    public MetaResourceLocation fragmentFurnaceResult = null;
    public Float fragmentFurnaceExp = null;
    public ArrayList< EnumPlantType > sustainedPlantTypes = null;
    public ArrayList< MetaResourceLocation > sustainsPlantsSustainedByRaw = null;

    // IOreInfo
    public String oreName = null;
    public String blockOreDictionaryName = null;
    public String itemOreDictionaryName = null;
    public String fragmentItemOreDictionaryName = null;
    public MetaResourceLocation proxyOreResourceLocation = null;
    public Integer burnTime = null;
    public Integer baseDropAmount = null;
    public String bonusDropExpr = null;
    public Integer baseExp = null;
    public String bonusExpExpr = null;

    // IHostInfo
    public MetaResourceLocation hostMetaResource = null;

    // IGenericBlockProperties
    public Material material = null;
    public SoundType soundType = null;
    public String harvestTool = null;
    public Integer harvestLevel = null;
    public Float hardness = null;
    public Float explosionResistance = null;

    // Shared
    public boolean isHost = false;
    public ResourceLocation blockstateResourceLocation = null;
    public GenericCubeTextureMap textureMap = null;
    public Map< String , String > languageMap = null;

    public boolean processKeyValue( String key , String value )
    {
        switch( key )
        {
            case "blockstate":
            {
                blockstateResourceLocation = new ResourceLocation( value );
                return true;
            }
            case "burnTime":
            {
                burnTime = Integer.parseInt( value );
                return true;
            }
            case "convertsTo":
            {
                equivalentItemResourceLocation = new MetaResourceLocation( value );
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
                equivalentFragmentItemResourceLocation = new MetaResourceLocation( value );
                return true;
            }
            case "fragmentTexture":
            {
                fragmentTextureLayers = parseTextureLayers( value );
                return true;
            }
            case "fragmentFurnaceExp":
            {
                fragmentFurnaceExp = Float.parseFloat( value );
                return true;
            }
            case "fragmentFurnaceResult":
            {
                fragmentFurnaceResult = new MetaResourceLocation( value );
                return true;
            }
            case "furnaceExp":
            {
                furnaceExp = Float.parseFloat( value );
                return true;
            }
            case "furnaceResult":
            {
                furnaceResult = new MetaResourceLocation( value );
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
                    hostMetaResource = new MetaResourceLocation( value );
                return true;
            }
            case "ore":
            {
                oreName = value;
                return true;
            }
            case "oreDict":
            {
                String[] values = value.split( " " );
                blockOreDictionaryName = values[ 0 ].equals( "-" ) ? null : values[ 0 ];
                itemOreDictionaryName = values.length > 1 ? ( values[ 1 ].equals( "-" ) ? null : values[ 1 ] ) : blockOreDictionaryName;
                fragmentItemOreDictionaryName = values.length > 2 && !values[ 2 ].equals( "-" ) ? values[ 2 ] : null;
                return true;
            }
            case "proxy":
            {
                proxyOreResourceLocation = new MetaResourceLocation( value );
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

                for( String token : value.split( " " ) )
                {
                    if( token.contains( ":" ) )
                        sustainsPlantsSustainedByRaw.add( new MetaResourceLocation( token ) );
                    else
                        sustainedPlantTypes.add( EnumPlantType.getPlantType( token ) );
                }

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
                            : String.format( "%s_%d" , hostMetaResource.resourceLocation.getResourcePath() , hostMetaResource.meta );

                    textureMap = new GenericCubeTextureMap( registryName );
                }

                textureMap.set( facing , layerArray );
                return true;
            }
        }
        else if( key.startsWith( "lang." ) )
        {
            if( languageMap == null )
                languageMap = new HashMap<>();

            languageMap.put( key.substring( "lang.".length() ) , value );
            return true;
        }

        return false;
    }

    public TileData createMissingChildType( TileType type ) throws OperationNotSupportedException
    {
        if( this.tileSetName == null || this.type == null || type.parentType != this.type )
            throw new OperationNotSupportedException();

        TileData child = new TileData();
        child.tileSetName = this.tileSetName;
        child.type = type;
        child.material = type.material;
        child.soundType = type.soundType;
        child.harvestTool = type.harvestTool;
        child.harvestLevel = this.harvestLevel;
        child.hardness = this.hardness;
        child.explosionResistance = this.explosionResistance;
        child.textureMap = this.textureMap;

        return child;
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
