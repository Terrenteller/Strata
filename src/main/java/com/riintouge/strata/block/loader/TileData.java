package com.riintouge.strata.block.loader;

import com.riintouge.strata.Strata;
import com.riintouge.strata.misc.MetaResourceLocation;
import com.riintouge.strata.block.ProtoBlockTextureMap;
import com.riintouge.strata.block.SpecialBlockPropertyFlags;
import com.riintouge.strata.block.geo.TileType;
import com.riintouge.strata.image.BlendMode;
import com.riintouge.strata.image.LayeredTextureLayer;
import com.riintouge.strata.item.*;
import com.riintouge.strata.sound.SoundEventRegistry;
import com.riintouge.strata.sound.SoundEventTuple;
import com.riintouge.strata.util.StringUtil;
import com.riintouge.strata.util.Util;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.EnumPlantType;
import org.apache.commons.lang3.EnumUtils;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TileData
{
    protected static final String DROP_GROUP_REGEX = "^(?:([0-9]+) )?([a-z_:0-9]+|-|\\*) *(.+?)? *$";
    protected static final Pattern DROP_GROUP_PATTERN = Pattern.compile( DROP_GROUP_REGEX );
    protected static final int DROP_GROUP_WEIGHT_GROUP = 1;
    protected static final int DROP_GROUP_META_RESOURCE_LOCATION_GROUP = 2;
    protected static final int DROP_GROUP_FORMULA_GROUP = 3;

    protected static final String NUMERIC_DROP_FORMULA_REGEX = "^(?:([0-9]+)(?:-([0-9]+))?)?$";
    protected static final Pattern NUMERIC_DROP_FORMULA_PATTERN = Pattern.compile( NUMERIC_DROP_FORMULA_REGEX );
    protected static final String RPN_DROP_FORMULA_REGEX = "^(.+?)(?: ~ (.+?))?$";
    protected static final Pattern RPN_DROP_FORMULA_PATTERN = Pattern.compile( RPN_DROP_FORMULA_REGEX );
    protected static final int DROP_FORMULA_BASE_GROUP = 1;
    protected static final int DROP_FORMULA_BONUS_GROUP = 2;

    // IGeoTileInfo
    public String tileSetName = null;
    public TileType tileType = null;
    public String fragmentItemOreDictionaryName = null;
    public MetaResourceLocation equivalentItemResourceLocation = null;
    public MetaResourceLocation furnaceResult = null;
    public Float furnaceExperience = null;
    public List< LayeredTextureLayer > fragmentTextureLayers = null;
    public IDropFormula fragmentDropFormula = null;
    public MetaResourceLocation equivalentFragmentItemResourceLocation = null;
    public MetaResourceLocation fragmentFurnaceResult = null;
    public Float fragmentFurnaceExperience = null;
    public Integer fragmentBurnTime = null;
    public MetaResourceLocation breaksIntoResourceLocation = null;
    public ArrayList< EnumPlantType > sustainedPlantTypes = null;
    public ArrayList< MetaResourceLocation > sustainsPlantsSustainedByRaw = null;
    public SoundEventTuple ambientSound = null;

    // IOreInfo
    public String oreName = null;
    public String itemOreDictionaryName = null;
    public MetaResourceLocation proxyOreResourceLocation = null;
    public List< LayeredTextureLayer > oreItemTextureLayers = null;
    public IDropFormula experienceDropFormula = null;
    public MetaResourceLocation forcedHost = null;
    public List< MetaResourceLocation > hostAffinities = null;

    // IHostInfo
    public MetaResourceLocation hostMetaResource = null;
    public Float slipperiness = null;
    public Integer meltsAt;
    public MetaResourceLocation meltsInto;
    public Integer sublimatesAt;
    public MetaResourceLocation sublimatesInto;

    // ICommonBlockProperties
    public Material material = null;
    public SoundType soundType = null;
    public String harvestTool = null;
    public Integer harvestLevel = null;
    public Float hardness = null;
    public Float explosionResistance = null;
    public Integer lightLevel = null;
    public Integer lightOpacity = null;
    public Integer burnTime = null;
    public Long specialBlockPropertyFlags = null;

    // Shared / Special
    public boolean isHost = false;
    public String blockOreDictionaryName = null;
    public ResourceLocation blockStateResource = null;
    public ProtoBlockTextureMap textureMap = null;
    public LayeredTextureLayer[][] layeredTextureLayers = null;
    public Map< String , String > languageMap = null;
    public Map< String , String > tooltipMap = null;
    public WeightedDropCollections weightedDropCollections = null;

    // It is imperative that documentation in Strata.txt stay up-to-date with this method!
    public boolean processKeyValue( String key , String value )
    {
        switch( key )
        {
            case "activatable":
            {
                setSpecialBlockPropertyFlag( SpecialBlockPropertyFlags.ACTIVATABLE );
                return true;
            }
            case "affectedByGravity":
            {
                setSpecialBlockPropertyFlag( SpecialBlockPropertyFlags.AFFECTED_BY_GRAVITY );
                return true;
            }
            case "ambientSound":
            {
                String[] values = value.split( " " );
                switch( values.length )
                {
                    case 1:
                    {
                        ambientSound = new SoundEventTuple( SoundEventRegistry.INSTANCE.register( value ) );
                        return true;
                    }
                    case 3:
                    {
                        // Volume and pitch are multipliers. Clamp for safety.
                        ambientSound = new SoundEventTuple(
                            Util.clamp( 0.0f , Float.parseFloat( values[ 0 ] ) , 2.0f ),
                            Util.clamp( 0.0f , Float.parseFloat( values[ 1 ] ) , 2.0f ),
                            SoundEventRegistry.INSTANCE.register( values[ 2 ] ) );
                        return true;
                    }
                }

                return false;
            }
            case "blockState":
            {
                blockStateResource = new ResourceLocation( value );
                return true;
            }
            case "breaksInto":
            {
                breaksIntoResourceLocation = new MetaResourceLocation( value );
                return true;
            }
            case "burnTime":
            {
                burnTime = Integer.parseInt( value );
                return true;
            }
            case "convertsTo":
            {
                equivalentItemResourceLocation = value.equals( "-" )
                    ? new MetaResourceLocation( Blocks.AIR.getRegistryName() , 0 )
                    : new MetaResourceLocation( value );
                return true;
            }
            case "dragonImmune":
            {
                setSpecialBlockPropertyFlag( SpecialBlockPropertyFlags.DRAGON_IMMUNE );
                return true;
            }
            case "experience":
            {
                experienceDropFormula = parseDropFormula( value );
                return true;
            }
            case "explosionResistance":
            {
                explosionResistance = Float.parseFloat( value );
                return true;
            }
            case "fireSource":
            {
                setSpecialBlockPropertyFlag( SpecialBlockPropertyFlags.FIRE_SOURCE );
                return true;
            }
            case "forceHost":
            {
                forcedHost = new MetaResourceLocation( value );
                return true;
            }
            case "fragmentBurnTime":
            {
                fragmentBurnTime = Integer.parseInt( value );
                return true;
            }
            case "fragmentConvertsTo":
            {
                equivalentFragmentItemResourceLocation = new MetaResourceLocation( value );
                return true;
            }
            case "fragmentFurnaceExperience":
            {
                fragmentFurnaceExperience = Float.parseFloat( value );
                return true;
            }
            case "fragmentFurnaceResult":
            {
                fragmentFurnaceResult = new MetaResourceLocation( value );
                return true;
            }
            case "fragments":
            {
                fragmentDropFormula = parseDropFormula( value );
                return true;
            }
            case "fragmentTexture":
            {
                fragmentTextureLayers = parseTextureLayers( value );
                return true;
            }
            case "furnaceExperience":
            {
                furnaceExperience = Float.parseFloat( value );
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
                processKeyValue( "tileType" , values[ 1 ] );
                return true;
            }
            case "hardness":
            {
                hardness = Float.parseFloat( value );
                return true;
            }
            case "hasEffect":
            {
                setSpecialBlockPropertyFlag( SpecialBlockPropertyFlags.HAS_EFFECT );
                return true;
            }
            case "harvestLevel":
            {
                harvestLevel = Integer.parseInt( value );
                return true;
            }
            case "harvestTool":
            {
                harvestTool = value.equalsIgnoreCase( "-" ) ? "" : value;
                return true;
            }
            case "host":
            {
                isHost = true;
                if( !value.isEmpty() )
                    hostMetaResource = new MetaResourceLocation( value );
                return true;
            }
            case "hostAffinities":
            {
                if( hostAffinities == null )
                    hostAffinities = new ArrayList<>();
                for( String hostValue : value.split( " " ) )
                    hostAffinities.add( new MetaResourceLocation( hostValue ) );
                return true;
            }
            case "lightLevel":
            {
                lightLevel = Util.clamp( 0 , Integer.parseInt( value ) , 15 );
                return true;
            }
            case "lightOpacity":
            {
                // Block's constructor uses 255 for a full block. Why not 15?
                // Is that just to ensure a full block never lets light through?
                lightOpacity = Util.clamp( 0 , Integer.parseInt( value ) , 255 );
                return true;
            }
            case "meltsAtInto":
            {
                String[] values = value.split( " " );
                if( values.length < 2 )
                    return false;

                meltsAt = Util.clamp( 0 , Integer.parseInt( values[ 0 ] ) , 15 );
                meltsInto = new MetaResourceLocation( values[ 1 ] );
                return true;
            }
            case "noSilkTouch":
            {
                setSpecialBlockPropertyFlag( SpecialBlockPropertyFlags.NO_SILK_TOUCH );
                return true;
            }
            case "notReconstitutable":
            {
                setSpecialBlockPropertyFlag( SpecialBlockPropertyFlags.NOT_RECONSTITUTABLE );
                return true;
            }
            case "ore":
            {
                String[] values = value.split( " " );
                oreName = values[ 0 ];
                return true;
            }
            case "oreDict":
            {
                String[] values = value.split( " " );
                if( values.length == 0 )
                    return false;

                blockOreDictionaryName = values[ 0 ].equals( "-" ) ? null : values[ 0 ];
                itemOreDictionaryName = values.length > 1 ? ( values[ 1 ].equals( "-" ) ? null : values[ 1 ] ) : blockOreDictionaryName;
                fragmentItemOreDictionaryName = values.length > 2 && !values[ 2 ].equals( "-" ) ? values[ 2 ] : null;
                return true;
            }
            case "oreItemTexture":
            {
                oreItemTextureLayers = parseTextureLayers( value );
                return true;
            }
            case "proxyOre":
            {
                proxyOreResourceLocation = new MetaResourceLocation( value );
                return true;
            }
            case "slipperiness":
            {
                slipperiness = Float.parseFloat( value );
                return true;
            }
            case "sublimatesAtInto":
            {
                // If we sublimate below the melting point everything will still work because
                // GeoBlock.checkRandomHarvest() checks for sublimation first.
                // We ought to have a Strata-wide sanity check in a finalization stage
                // to check if the values make sense and the target block exists.
                String[] values = value.split( " " );
                if( values.length < 2 )
                    return false;

                sublimatesAt = Util.clamp( 0 , Integer.parseInt( values[ 0 ] ) , 15 );
                sublimatesInto = new MetaResourceLocation( values[ 1 ] );
                return true;
            }
            case "sustains":
            {
                sustainedPlantTypes = new ArrayList<>();
                sustainsPlantsSustainedByRaw = new ArrayList<>();

                for( String token : value.split( " " ) )
                {
                    // We have to prioritize resource locations because EnumPlantType.getPlantType()
                    // will add to the enumeration if the value doesn't exist. Checking for the value
                    // in the enumeration by other means is not sufficient because we may initialize
                    // before whatever is responsible for the plant type adds it to the enumeration.
                    if( token.contains( ":" ) )
                        sustainsPlantsSustainedByRaw.add( new MetaResourceLocation( token ) );
                    else
                        sustainedPlantTypes.add( EnumPlantType.getPlantType( token ) );
                }

                return true;
            }
            case "soundEvents":
            {
                String[] values = value.split( " " );
                switch( values.length )
                {
                    case 5:
                    {
                        soundType = SoundEventRegistry.INSTANCE.register(
                            1.0f,
                            1.0f,
                            values[ 0 ],
                            values[ 1 ],
                            values[ 2 ],
                            values[ 3 ],
                            values[ 4 ] );
                        return true;
                    }
                    case 7:
                    {
                        soundType = SoundEventRegistry.INSTANCE.register(
                            Float.parseFloat( values[ 0 ] ),
                            Float.parseFloat( values[ 1 ] ),
                            values[ 2 ],
                            values[ 3 ],
                            values[ 4 ],
                            values[ 5 ],
                            values[ 6 ] );
                        return true;
                    }
                }

                return false;
            }
            case "tileType":
            {
                tileType = TileType.valueOf( value.toUpperCase() );
                if( harvestTool == null )
                    harvestTool = tileType.harvestTool;
                // Obsfucation prevents us from using reflection
                // to get non-enum Material and SoundType values from distinct KVs
                material = tileType.material;
                if( soundType == null )
                    soundType = tileType.soundType;
                return true;
            }
            case "witherImmune":
            {
                setSpecialBlockPropertyFlag( SpecialBlockPropertyFlags.WITHER_IMMUNE );
                return true;
            }
        }

        if( key.startsWith( "dropGroup." ) )
        {
            String dropGroupKey = key.substring( "dropGroup.".length() );
            if( dropGroupKey.length() <= 0 )
                return false;

            Matcher matcher = DROP_GROUP_PATTERN.matcher( value );
            if( !matcher.find() )
                return false;

            String formula = matcher.group( DROP_GROUP_FORMULA_GROUP );
            IDropFormula dropFormula = formula != null ? parseDropFormula( formula ) : new StaticDropFormula( 1 );
            if( dropFormula == null )
                return false;

            String metaResource = matcher.group( DROP_GROUP_META_RESOURCE_LOCATION_GROUP );
            MetaResourceLocation metaResourceLocation;
            switch( metaResource )
            {
                case "*":
                    // A primary tile type may drop fragments normally or may do so because of a server config option.
                    // Since we cannot guarantee consistency, the wildcard is limited to ores.
                    if( StringUtil.isNullOrEmpty( oreName ) )
                        return false;

                    metaResourceLocation = new MetaResourceLocation( Strata.resource( oreName ) , 0 );
                    break;
                case "-":
                    metaResourceLocation = new MetaResourceLocation( Items.AIR.getRegistryName() , 0 );
                    break;
                default:
                    metaResourceLocation = new MetaResourceLocation( metaResource );
                    break;
            }

            if( weightedDropCollections == null )
                weightedDropCollections = new WeightedDropCollections();

            String weight = matcher.group( DROP_GROUP_WEIGHT_GROUP );
            weightedDropCollections.addDropToGroup(
                metaResourceLocation,
                dropFormula,
                weight != null ? Math.max( 0 , Integer.parseInt( weight ) ) : 100,
                dropGroupKey );

            return true;
        }
        else if( key.startsWith( "lang." ) )
        {
            if( languageMap == null )
                languageMap = new HashMap<>();

            languageMap.put( key.substring( "lang.".length() ) , value );
            return true;
        }
        else if( key.startsWith( "texture" ) )
        {
            String facingString = key.substring( "texture".length() ).toUpperCase();
            ProtoBlockTextureMap.Layer layer = EnumUtils.isValidEnum( ProtoBlockTextureMap.Layer.class , facingString )
                ? ProtoBlockTextureMap.Layer.valueOf( facingString )
                : facingString.isEmpty()
                    ? ProtoBlockTextureMap.Layer.ALL
                    : null;

            if( layer != null )
            {
                if( layeredTextureLayers == null )
                {
                    layeredTextureLayers = new LayeredTextureLayer[ ProtoBlockTextureMap.Layer.values().length ][];

                    // FIXME: Using tileSetName, oreName, and tileType here violates the assumption that lines can be in any order
                    String registryName = tileSetName != null
                        ? tileType.registryName( tileSetName ).getResourcePath()
                        : oreName != null
                            ? tileType.registryName( oreName ).getResourcePath()
                            : String.format( "%s_%d" , hostMetaResource.resourceLocation.getResourcePath() , hostMetaResource.meta );

                    textureMap = new ProtoBlockTextureMap( registryName , layeredTextureLayers );
                }

                List< LayeredTextureLayer > layers = parseTextureLayers( value );
                layeredTextureLayers[ layer.ordinal() ] = layers.toArray( new LayeredTextureLayer[ 0 ] );
                return true;
            }
        }
        else if( key.startsWith( "tooltip." ) )
        {
            if( tooltipMap == null )
                tooltipMap = new HashMap<>();

            tooltipMap.put( key.substring( "tooltip.".length() ) , value );
            return true;
        }

        return false;
    }

    public TileData createChildType( TileType tileType ) throws UnsupportedOperationException
    {
        if( this.tileSetName == null || this.tileType == null || tileType.parentType != this.tileType )
            throw new UnsupportedOperationException();

        TileData child = new TileData();
        child.tileSetName = this.tileSetName;
        child.tileType = tileType;
        child.material = this.material;
        child.soundType = this.soundType;
        child.harvestTool = this.harvestTool;
        child.harvestLevel = this.harvestLevel;
        child.hardness = this.hardness;
        child.explosionResistance = this.explosionResistance;
        child.textureMap = this.textureMap;

        return child;
    }

    protected void setSpecialBlockPropertyFlag( long flag )
    {
        if( specialBlockPropertyFlags == null )
            specialBlockPropertyFlags = 0L;

        specialBlockPropertyFlags |= flag;
    }

    // Statics

    public static IDropFormula parseDropFormula( String value )
    {
        Matcher matcher = NUMERIC_DROP_FORMULA_PATTERN.matcher( value );
        if( matcher.find() )
        {
            String base = matcher.group( DROP_FORMULA_BASE_GROUP );
            String bonus = matcher.group( DROP_FORMULA_BONUS_GROUP );

            int numericMinimum = base != null ? Math.max( 0 , Integer.parseInt( base ) ) : 1;
            int numericMaximum = bonus != null ? Math.max( numericMinimum , Integer.parseInt( bonus ) ) : numericMinimum;

            return numericMinimum == numericMaximum
                ? new StaticDropFormula( numericMinimum )
                : new VanillaDropFormula( numericMinimum , numericMaximum );
        }

        matcher = RPN_DROP_FORMULA_PATTERN.matcher( value );
        if( matcher.find() )
        {
            String base = matcher.group( DROP_FORMULA_BASE_GROUP );
            String bonus = matcher.group( DROP_FORMULA_BONUS_GROUP );

            IDropFormula dropFormula = new RPNDropFormula( base , bonus );
            // Perform a test evaluation of the RPN. This will throw if the expression is invalid.
            dropFormula.getAmount( new Random() , null , new BlockPos( 0 , 0 , 0 ) );

            return dropFormula;
        }

        return null;
    }

    public static List< LayeredTextureLayer > parseTextureLayers( String value )
    {
        List< LayeredTextureLayer > layers = new Vector<>();

        String[] components = value.toLowerCase().split( " " );
        if( ( ( components.length - 1 ) % 3 ) != 0 )
            throw new IllegalArgumentException( "Invalid number of layered texture arguments!" );

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
