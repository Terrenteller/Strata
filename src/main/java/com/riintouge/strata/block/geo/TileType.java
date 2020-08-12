package com.riintouge.strata.block.geo;

import com.riintouge.strata.Strata;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.util.ResourceLocation;

public enum TileType
{
    // Primaries
    CLAY   ( true , "%s:%s" , Material.CLAY   , SoundType.GROUND , "shovel"  , null , "generic_cube" ),
    GROUND ( true , "%s:%s" , Material.GROUND , SoundType.GROUND , "shovel"  , null , "generic_cube" ),
    SAND   ( true , "%s:%s" , Material.SAND   , SoundType.SAND   , "shovel"  , null , "generic_cube" ),
    STONE  ( true , "%s:%s" , Material.ROCK   , SoundType.STONE  , "pickaxe" , null , "generic_cube" ),

    // Secondaries - manually specified derivatives of a primary
    COBBLE     ( false , "%s:%s_cobble"     , Material.ROCK , SoundType.STONE , "pickaxe" , null , "generic_cube" ),
    STONEBRICK ( false , "%s:%s_stonebrick" , Material.ROCK , SoundType.STONE , "pickaxe" , null , "generic_cube" ),

    // Ternaries - internally auto-generated from a primary or secondary
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
                return COBBLESTAIRS;
            case STONEBRICK:
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
                return COBBLESLAB;
            case STONEBRICK:
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
                return COBBLESLABS;
            case STONEBRICK:
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
                return COBBLEWALL;
            case STONEBRICK:
                return STONEBRICKWALL;
        }

        return null;
    }
}
