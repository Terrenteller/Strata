package com.riintouge.strata.block.loader;

import com.riintouge.strata.Strata;
import com.riintouge.strata.block.MetaResourceLocation;
import com.riintouge.strata.block.ProtoBlockTextureMap;
import com.riintouge.strata.block.SpecialBlockPropertyFlags;
import com.riintouge.strata.block.geo.TileType;
import com.riintouge.strata.image.BlendMode;
import com.riintouge.strata.image.LayeredTextureLayer;
import com.riintouge.strata.item.*;
import com.riintouge.strata.sound.SoundEventRegistry;
import com.riintouge.strata.sound.SoundEventTuple;
import com.riintouge.strata.util.Util;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.EnumPlantType;
import org.apache.commons.lang3.EnumUtils;

import javax.naming.OperationNotSupportedException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TileData
{
    private static final String DropGroupPattern = "^(?:([0-9]+) )?([a-z_:0-9]+|-|\\*) *(.+?)? *$";
    private static final int DropGroupWeightGroup = 1;
    private static final int DropGroupMetaResourceLocationGroup = 2;
    private static final int DropGroupFormulaGroup = 3;
    private static final Pattern DropGroupRegex = Pattern.compile( DropGroupPattern );

    private static final String NumericDropFormulaPattern = "^(?:([0-9]+)(?:-([0-9]+))?)?$";
    private static final String RPNDropFormulaPattern = "^(.+?)(?: ~ (.+?))?$";
    private static final int DropFormulaBaseGroup = 1;
    private static final int DropFormulaBonusGroup = 2;
    private static final Pattern NumericDropFormulaRegex = Pattern.compile( NumericDropFormulaPattern );
    private static final Pattern RPNDropFormulaRegex = Pattern.compile( RPNDropFormulaPattern );

    // IGeoTileInfo
    public String tileSetName = null;
    public TileType tileType = null;
    public String fragmentItemOreDictionaryName = null;
    public MetaResourceLocation equivalentItemResourceLocation = null;
    public MetaResourceLocation furnaceResult = null;
    public Float furnaceExp = null;
    public List< LayeredTextureLayer > fragmentTextureLayers = null;
    public IDropFormula fragmentDropFormula = null;
    public MetaResourceLocation equivalentFragmentItemResourceLocation = null;
    public MetaResourceLocation fragmentFurnaceResult = null;
    public Float fragmentFurnaceExp = null;
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
    public IDropFormula expDropFormula = null;
    public WeightedDropCollections weightedDropCollections = null;
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
    public ResourceLocation blockstateResourceLocation = null;
    public ProtoBlockTextureMap textureMap = null;
    public LayeredTextureLayer[][] layeredTextureLayers = null;
    public Map< String , String > languageMap = null;
    public Map< String , String > tooltipMap = null;

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
                        ambientSound = new SoundEventTuple(
                            Float.parseFloat( values[ 0 ] ),
                            Float.parseFloat( values[ 1 ] ),
                            SoundEventRegistry.INSTANCE.register( values[ 2 ] ) );
                        return true;
                    }
                }

                return false;
            }
            case "blockstate":
            {
                blockstateResourceLocation = new ResourceLocation( value );
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
            case "exp":
            {
                expDropFormula = parseDropFormula( value );
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
                    if( token.contains( ":" ) )
                        sustainsPlantsSustainedByRaw.add( new MetaResourceLocation( token ) );
                    else
                        sustainedPlantTypes.add( EnumPlantType.getPlantType( token ) );
                }

                return true;
            }
            case "type":
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
            case "soundEvents":
            {
                String[] values = value.split( " " );
                switch( values.length )
                {
                    case 5:
                    {
                        soundType = SoundEventRegistry.INSTANCE.registerAndCreate(
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
                        soundType = SoundEventRegistry.INSTANCE.registerAndCreate(
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
            case "witherImmune":
            {
                if( specialBlockPropertyFlags == null )
                    specialBlockPropertyFlags = 0L;

                specialBlockPropertyFlags |= SpecialBlockPropertyFlags.WITHER_IMMUNE;
                return true;
            }
        }

        if( key.startsWith( "dropGroup." ) )
        {
            String dropGroupKey = key.substring( "dropGroup.".length() );
            if( dropGroupKey.length() <= 0 )
                return false;

            Matcher matcher = DropGroupRegex.matcher( value );
            if( !matcher.find() )
                return false;

            String weight = matcher.group( DropGroupWeightGroup );
            String metaResource = matcher.group( DropGroupMetaResourceLocationGroup );
            String formula = matcher.group( DropGroupFormulaGroup );

            IDropFormula dropFormula = formula != null ? parseDropFormula( formula ) : new StaticDropFormula( 1 );
            if( dropFormula == null )
                return false;

            MetaResourceLocation metaResourceLocation;
            switch( metaResource )
            {
                case "*":
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

                    // FIXME: Using tileSetName, oreName, and type here violates the assumption that lines can be in any order
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

    public TileData createMissingChildType( TileType tileType ) throws OperationNotSupportedException
    {
        if( this.tileSetName == null || this.tileType == null || tileType.parentType != this.tileType )
            throw new OperationNotSupportedException();

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
        Matcher matcher = NumericDropFormulaRegex.matcher( value );
        if( matcher.find() )
        {
            String base = matcher.group( DropFormulaBaseGroup );
            String bonus = matcher.group( DropFormulaBonusGroup );

            int numericMinimum = base != null ? Math.max( 0 , Integer.parseInt( base ) ) : 1;
            int numericMaximum = bonus != null ? Math.max( numericMinimum , Integer.parseInt( bonus ) ) : numericMinimum;

            return numericMinimum == numericMaximum
                ? new StaticDropFormula( numericMinimum )
                : new VanillaDropFormula( numericMinimum , numericMaximum );
        }

        matcher = RPNDropFormulaRegex.matcher( value );
        if( matcher.find() )
        {
            String base = matcher.group( DropFormulaBaseGroup );
            String bonus = matcher.group( DropFormulaBonusGroup );

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
