package com.riintouge.strata.block.geo;

import com.riintouge.strata.Strata;
import net.minecraft.block.BlockStoneSlab;
import net.minecraft.block.BlockWall;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

public enum TileType
{
    // Primaries
    CLAY   ( true , "%s:%s" , Material.CLAY   , SoundType.GROUND , "shovel"  , null , "generic_cube" , null ),
    GRAVEL ( true , "%s:%s" , Material.SAND   , SoundType.GROUND , "shovel"  , null , "generic_cube" , null ),
    GROUND ( true , "%s:%s" , Material.GROUND , SoundType.GROUND , "shovel"  , null , "generic_cube" , null ),
    SAND   ( true , "%s:%s" , Material.SAND   , SoundType.SAND   , "shovel"  , null , "generic_cube" , null ),
    STONE  ( true , "%s:%s" , Material.ROCK   , SoundType.STONE  , "pickaxe" , null , "generic_cube" , null ),

    // Secondaries - manually specified derivatives of a primary
    COBBLE          ( false , "%s:%s_cobble"          , Material.ROCK , SoundType.STONE , "pickaxe" , null , "generic_cube" , null ),
    COBBLEMOSSY     ( false , "%s:%s_cobblemossy"     , Material.ROCK , SoundType.STONE , "pickaxe" , null , "generic_cube" , null ),
    STONEBRICK      ( false , "%s:%s_stonebrick"      , Material.ROCK , SoundType.STONE , "pickaxe" , null , "generic_cube" , null ),
    STONEBRICKMOSSY ( false , "%s:%s_stonebrickmossy" , Material.ROCK , SoundType.STONE , "pickaxe" , null , "generic_cube" , null ),
    STONEPOLISHED   ( false , "%s:%s_stonepolished"   , Material.ROCK , SoundType.STONE , "pickaxe" , null , "generic_cube" , null ),

    // Tertiaries - internally auto-generated from a primary or secondary
    COBBLESTAIRS        ( false , "%s:%s_cobblestairs"        , Material.ROCK     , SoundType.STONE , "pickaxe" , COBBLE          , "generic_stairs"         , "facing=east,half=bottom,shape=straight" ),
    STONESTAIRS         ( false , "%s:%s_stonestairs"         , Material.ROCK     , SoundType.STONE , "pickaxe" , STONE           , "generic_stairs"         , "facing=east,half=bottom,shape=straight" ),
    STONEBRICKSTAIRS    ( false , "%s:%s_stonebrickstairs"    , Material.ROCK     , SoundType.STONE , "pickaxe" , STONEBRICK      , "generic_stairs"         , "facing=east,half=bottom,shape=straight" ),
    COBBLESLAB          ( false , "%s:%s_cobbleslab"          , Material.ROCK     , SoundType.STONE , "pickaxe" , COBBLE          , "generic_slab"           , "half=bottom,variant=default"            ),
    COBBLESLABS         ( false , "%s:%s_cobbleslabs"         , Material.ROCK     , SoundType.STONE , "pickaxe" , COBBLE          , "generic_slab"           , "half=bottom,variant=default"            ),
    STONESLAB           ( false , "%s:%s_stoneslab"           , Material.ROCK     , SoundType.STONE , "pickaxe" , STONE           , "generic_slab"           , "half=bottom,variant=default"            ),
    STONESLABS          ( false , "%s:%s_stoneslabs"          , Material.ROCK     , SoundType.STONE , "pickaxe" , STONE           , "generic_slab"           , "half=bottom,variant=default"            ),
    STONEBRICKSLAB      ( false , "%s:%s_stonebrickslab"      , Material.ROCK     , SoundType.STONE , "pickaxe" , STONEBRICK      , "generic_slab"           , "half=bottom,variant=default"            ),
    STONEBRICKSLABS     ( false , "%s:%s_stonebrickslabs"     , Material.ROCK     , SoundType.STONE , "pickaxe" , STONEBRICK      , "generic_slab"           , "half=bottom,variant=default"            ),
    COBBLEWALL          ( false , "%s:%s_cobblewall"          , Material.ROCK     , SoundType.STONE , "pickaxe" , COBBLE          , "generic_wall"           , "inventory"                              ),
    COBBLEWALLMOSSY     ( false , "%s:%s_cobblewallmossy"     , Material.ROCK     , SoundType.STONE , "pickaxe" , COBBLEMOSSY     , "generic_wall"           , "inventory"                              ),
    STONEWALL           ( false , "%s:%s_stonewall"           , Material.ROCK     , SoundType.STONE , "pickaxe" , STONE           , "generic_wall"           , "inventory"                              ),
    STONEBRICKWALL      ( false , "%s:%s_stonebrickwall"      , Material.ROCK     , SoundType.STONE , "pickaxe" , STONEBRICK      , "generic_wall"           , "inventory"                              ),
    STONEBRICKWALLMOSSY ( false , "%s:%s_stonebrickwallmossy" , Material.ROCK     , SoundType.STONE , "pickaxe" , STONEBRICKMOSSY , "generic_wall"           , "inventory"                              ),
    BUTTON              ( false , "%s:%s_button"              , Material.CIRCUITS , SoundType.STONE , "pickaxe" , STONE           , "generic_button"         , "inventory"                              ),
    LEVER               ( false , "%s:%s_lever"               , Material.CIRCUITS , SoundType.WOOD  , "pickaxe" , COBBLE          , "generic_lever"          , "facing=up_z,powered=false"              ),
    PRESSUREPLATE       ( false , "%s:%s_pressureplate"       , Material.ROCK     , SoundType.STONE , "pickaxe" , STONE           , "generic_pressure_plate" , "powered=false"                          );

    // Cache to satisfy instance comparisons
    private static Map< TileType , ItemStack > ItemStackMap = new HashMap<>();

    public final Boolean isPrimary; // This could stand to be improved. Perhaps TileTypeTier?
    public final Material material;
    public final SoundType soundType;
    public final String harvestTool;
    private final String resourceLocationFormat;
    public final TileType parentType;
    public final ResourceLocation modelName;
    public final String defaultModelVariant;

    TileType(
        Boolean isPrimary,
        String resourceLocationFormat,
        Material material,
        SoundType soundType,
        String harvestTool,
        TileType parentType,
        String modelName,
        String defaultModelVariant )
    {
        this.isPrimary = isPrimary;
        this.resourceLocationFormat = resourceLocationFormat;
        this.material = material;
        this.soundType = soundType;
        this.harvestTool = harvestTool;
        this.parentType = parentType;
        this.modelName = new ResourceLocation( Strata.modid , modelName );
        this.defaultModelVariant = defaultModelVariant;
    }

    public ResourceLocation registryName( ResourceLocation baseLocation )
    {
        return new ResourceLocation( String.format( resourceLocationFormat , baseLocation.getResourceDomain() , baseLocation.getResourcePath() ) );
    }

    public ResourceLocation registryName( String tileSetName )
    {
        return new ResourceLocation( String.format( resourceLocationFormat , Strata.modid , tileSetName ) );
    }

    public ItemStack vanillaItemStack()
    {
        ItemStack vanillaItem = ItemStackMap.getOrDefault( this , null );
        if( vanillaItem != null )
            return vanillaItem;

        switch( this )
        {
            case CLAY:
                vanillaItem = new ItemStack( Blocks.CLAY );
                break;
            // TODO: Evaluate GRAVEL when we have a use case
            // TODO: Not all SANDs are equivalent to vanilla sand
            case STONE:
                vanillaItem = new ItemStack( Blocks.STONE );
                break;
            case COBBLE:
                vanillaItem = new ItemStack( Blocks.COBBLESTONE );
                break;
            case COBBLEMOSSY:
                vanillaItem = new ItemStack( Blocks.MOSSY_COBBLESTONE );
                break;
            case STONEBRICK:
                vanillaItem = new ItemStack( Blocks.STONEBRICK );
                break;
            case STONEBRICKMOSSY:
                vanillaItem = new ItemStack( Blocks.STONEBRICK , 1 , 1 );
                break;
            case COBBLESTAIRS:
                vanillaItem = new ItemStack( Blocks.STONE_STAIRS );
                break;
            case STONEBRICKSTAIRS:
                vanillaItem = new ItemStack( Blocks.STONE_BRICK_STAIRS );
                break;
            case COBBLESLAB:
                vanillaItem = new ItemStack( Blocks.STONE_SLAB , 1 , BlockStoneSlab.EnumType.COBBLESTONE.getMetadata() );
                break;
            case STONESLAB:
                vanillaItem = new ItemStack( Blocks.STONE_SLAB , 1 , BlockStoneSlab.EnumType.STONE.getMetadata() );
                break;
            case STONEBRICKSLAB:
                vanillaItem = new ItemStack( Blocks.STONE_SLAB , 1 , BlockStoneSlab.EnumType.SMOOTHBRICK.getMetadata() );
                break;
            case COBBLEWALL:
                vanillaItem = new ItemStack( Blocks.COBBLESTONE_WALL );
                break;
            case COBBLEWALLMOSSY:
                vanillaItem = new ItemStack( Blocks.COBBLESTONE_WALL , 1 , BlockWall.EnumType.MOSSY.getMetadata() );
                break;
            case BUTTON:
                vanillaItem = new ItemStack( Blocks.STONE_BUTTON );
                break;
            case LEVER:
                vanillaItem = new ItemStack( Blocks.LEVER );
                break;
            case PRESSUREPLATE:
                vanillaItem = new ItemStack( Blocks.STONE_PRESSURE_PLATE );
                break;
        }

        ItemStackMap.put( this , vanillaItem );
        return vanillaItem;
    }
}
