package com.riintouge.strata.block.geo;

import com.riintouge.strata.Strata;
import net.minecraft.block.BlockStoneSlab;
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
    CLAY   ( true , "%s:%s" , Material.CLAY   , SoundType.GROUND , "shovel"  , null , "generic_cube" ),
    GROUND ( true , "%s:%s" , Material.GROUND , SoundType.GROUND , "shovel"  , null , "generic_cube" ),
    SAND   ( true , "%s:%s" , Material.SAND   , SoundType.SAND   , "shovel"  , null , "generic_cube" ),
    STONE  ( true , "%s:%s" , Material.ROCK   , SoundType.STONE  , "pickaxe" , null , "generic_cube" ),

    // Secondaries - manually specified derivatives of a primary
    COBBLE          ( false , "%s:%s_cobble"          , Material.ROCK , SoundType.STONE , "pickaxe" , null , "generic_cube" ),
    COBBLEMOSSY     ( false , "%s:%s_cobblemossy"     , Material.ROCK , SoundType.STONE , "pickaxe" , null , "generic_cube" ),
    STONEBRICK      ( false , "%s:%s_stonebrick"      , Material.ROCK , SoundType.STONE , "pickaxe" , null , "generic_cube" ),
    STONEBRICKMOSSY ( false , "%s:%s_stonebrickmossy" , Material.ROCK , SoundType.STONE , "pickaxe" , null , "generic_cube" ),
    STONEPOLISHED   ( false , "%s:%s_stonepolished"   , Material.ROCK , SoundType.STONE , "pickaxe" , null , "generic_cube" ),

    // Tertiaries - internally auto-generated from a primary or secondary
    COBBLESTAIRS     ( false , "%s:%s_cobblestairs"     , Material.ROCK , SoundType.STONE , "pickaxe" , COBBLE     , "generic_stairs" ),
    STONESTAIRS      ( false , "%s:%s_stonestairs"      , Material.ROCK , SoundType.STONE , "pickaxe" , STONE      , "generic_stairs" ),
    STONEBRICKSTAIRS ( false , "%s:%s_stonebrickstairs" , Material.ROCK , SoundType.STONE , "pickaxe" , STONEBRICK , "generic_stairs" ),
    COBBLESLAB       ( false , "%s:%s_cobbleslab"       , Material.ROCK , SoundType.STONE , "pickaxe" , COBBLE     , "generic_slab"   ),
    COBBLESLABS      ( false , "%s:%s_cobbleslabs"      , Material.ROCK , SoundType.STONE , "pickaxe" , COBBLE     , "generic_slab"   ),
    STONESLAB        ( false , "%s:%s_stoneslab"        , Material.ROCK , SoundType.STONE , "pickaxe" , STONE      , "generic_slab"   ),
    STONESLABS       ( false , "%s:%s_stoneslabs"       , Material.ROCK , SoundType.STONE , "pickaxe" , STONE      , "generic_slab"   ),
    STONEBRICKSLAB   ( false , "%s:%s_stonebrickslab"   , Material.ROCK , SoundType.STONE , "pickaxe" , STONEBRICK , "generic_slab"   ),
    STONEBRICKSLABS  ( false , "%s:%s_stonebrickslabs"  , Material.ROCK , SoundType.STONE , "pickaxe" , STONEBRICK , "generic_slab"   ),
    COBBLEWALL       ( false , "%s:%s_cobblewall"       , Material.ROCK , SoundType.STONE , "pickaxe" , COBBLE     , "generic_wall"   ),
    STONEWALL        ( false , "%s:%s_stonewall"        , Material.ROCK , SoundType.STONE , "pickaxe" , STONE      , "generic_wall"   ),
    STONEBRICKWALL   ( false , "%s:%s_stonebrickwall"   , Material.ROCK , SoundType.STONE , "pickaxe" , STONEBRICK , "generic_wall"   );

    // Cache to satisfy instance comparisons
    private static Map< TileType , ItemStack > ItemStackMap = new HashMap<>();

    public final Boolean isPrimary; // This could stand to be improved. Perhaps TileTypeTier?
    public final Material material;
    public final SoundType soundType;
    public final String harvestTool;
    private String resourceLocationFormat;
    public TileType parentType;
    public ResourceLocation modelName;

    TileType(
        Boolean isPrimary,
        String resourceLocationFormat,
        Material material,
        SoundType soundType,
        String harvestTool,
        TileType parentType,
        String modelName )
    {
        this.isPrimary = isPrimary;
        this.resourceLocationFormat = resourceLocationFormat;
        this.material = material;
        this.soundType = soundType;
        this.harvestTool = harvestTool;
        this.parentType = parentType;
        this.modelName = new ResourceLocation( Strata.modid , modelName );
    }

    public ResourceLocation registryName( ResourceLocation baseLocation )
    {
        return new ResourceLocation( String.format( resourceLocationFormat , baseLocation.getResourceDomain() , baseLocation.getResourcePath() ) );
    }

    public ResourceLocation registryName( String tileSetName )
    {
        return new ResourceLocation( String.format( resourceLocationFormat , Strata.modid , tileSetName ) );
    }

    public TileType stairType()
    {
        switch( this )
        {
            case STONE:
                return STONESTAIRS;
            case COBBLE:
            case COBBLEMOSSY:
                return COBBLESTAIRS;
            case STONEBRICK:
            case STONEBRICKMOSSY:
                return STONEBRICKSTAIRS;
        }

        return null;
    }

    public TileType slabType()
    {
        switch( this )
        {
            case STONE:
                return STONESLAB;
            case COBBLE:
            case COBBLEMOSSY:
                return COBBLESLAB;
            case STONEBRICK:
            case STONEBRICKMOSSY:
                return STONEBRICKSLAB;
        }

        return null;
    }

    public TileType slabsType()
    {
        switch( this )
        {
            case STONE:
                return STONESLABS;
            case COBBLE:
            case COBBLEMOSSY:
                return COBBLESLABS;
            case STONEBRICK:
            case STONEBRICKMOSSY:
                return STONEBRICKSLABS;
        }

        return null;
    }

    public TileType wallType()
    {
        switch( this )
        {
            case STONE:
                return STONEWALL;
            case COBBLE:
            case COBBLEMOSSY:
                return COBBLEWALL;
            case STONEBRICK:
            case STONEBRICKMOSSY:
                return STONEBRICKWALL;
        }

        return null;
    }

    public ItemStack vanillaItemStack()
    {
        ItemStack vanillaItem = ItemStackMap.getOrDefault( this , null );
        if( vanillaItem != null )
            return vanillaItem;

        switch( this )
        {
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
        }

        ItemStackMap.put( this , vanillaItem );
        return vanillaItem;
    }
}
