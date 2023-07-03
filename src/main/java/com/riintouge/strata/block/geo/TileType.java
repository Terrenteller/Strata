package com.riintouge.strata.block.geo;

import com.riintouge.strata.Strata;
import com.riintouge.strata.misc.MetaResourceLocation;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public enum TileType
{
    // Primaries - There can be only one
    CLAY   ( Tier.PRIMARY , "" , "_ball"  , Material.CLAY   , SoundType.GROUND , "shovel"  , null , "proto_cube_gimbal" , null ),
    GRAVEL ( Tier.PRIMARY , "" , "_mound" , Material.SAND   , SoundType.GROUND , "shovel"  , null , "proto_cube_gimbal" , null ),
    GROUND ( Tier.PRIMARY , "" , "_clump" , Material.GROUND , SoundType.GROUND , "shovel"  , null , "proto_cube_gimbal" , null ),
    SAND   ( Tier.PRIMARY , "" , "_pile"  , Material.SAND   , SoundType.SAND   , "shovel"  , null , "proto_cube_gimbal" , null ),
    STONE  ( Tier.PRIMARY , "" , "_rock"  , Material.ROCK   , SoundType.STONE  , "pickaxe" , null , "proto_cube_gimbal" , null ),
    GLASS  ( Tier.PRIMARY , "" , "_shard" , Material.GLASS  , SoundType.GLASS  , ""        , null , "proto_cube_gimbal" , null ),

    // Secondaries - Manually specified derivatives of a primary
    COBBLE          ( Tier.SECONDARY , "_cobble"          , null , Material.ROCK , SoundType.STONE , "pickaxe" , STONE , "proto_cube" , null ),
    COBBLEMOSSY     ( Tier.SECONDARY , "_cobblemossy"     , null , Material.ROCK , SoundType.STONE , "pickaxe" , STONE , "proto_cube" , null ),
    STONEBRICK      ( Tier.SECONDARY , "_stonebrick"      , null , Material.ROCK , SoundType.STONE , "pickaxe" , STONE , "proto_cube" , null ),
    STONEBRICKMOSSY ( Tier.SECONDARY , "_stonebrickmossy" , null , Material.ROCK , SoundType.STONE , "pickaxe" , STONE , "proto_cube" , null ),
    STONEPOLISHED   ( Tier.SECONDARY , "_stonepolished"   , null , Material.ROCK , SoundType.STONE , "pickaxe" , STONE , "proto_cube" , null ),

    // Tertiaries - Manually* specified derivatives of a primary or secondary
    // * Double slabs are special, should not be specified in config files, and must immediately follow the single slab in this enum for indexing purposes
    COBBLESTAIRS        ( Tier.TERTIARY , "_cobblestairs"        , null , Material.ROCK     , SoundType.STONE , "pickaxe" , COBBLE          , "proto_stairs"         , "facing=east,half=bottom,shape=straight" ),
    STONESTAIRS         ( Tier.TERTIARY , "_stonestairs"         , null , Material.ROCK     , SoundType.STONE , "pickaxe" , STONE           , "proto_stairs"         , "facing=east,half=bottom,shape=straight" ),
    STONEBRICKSTAIRS    ( Tier.TERTIARY , "_stonebrickstairs"    , null , Material.ROCK     , SoundType.STONE , "pickaxe" , STONEBRICK      , "proto_stairs"         , "facing=east,half=bottom,shape=straight" ),
    COBBLESLAB          ( Tier.TERTIARY , "_cobbleslab"          , null , Material.ROCK     , SoundType.STONE , "pickaxe" , COBBLE          , "proto_slab"           , "half=bottom,variant=default"            ),
    COBBLESLABS         ( Tier.TERTIARY , "_cobbleslabs"         , null , Material.ROCK     , SoundType.STONE , "pickaxe" , COBBLESLAB      , "proto_slab"           , "half=bottom,variant=default"            ),
    STONESLAB           ( Tier.TERTIARY , "_stoneslab"           , null , Material.ROCK     , SoundType.STONE , "pickaxe" , STONE           , "proto_slab"           , "half=bottom,variant=default"            ),
    STONESLABS          ( Tier.TERTIARY , "_stoneslabs"          , null , Material.ROCK     , SoundType.STONE , "pickaxe" , STONESLAB       , "proto_slab"           , "half=bottom,variant=default"            ),
    STONEBRICKSLAB      ( Tier.TERTIARY , "_stonebrickslab"      , null , Material.ROCK     , SoundType.STONE , "pickaxe" , STONEBRICK      , "proto_slab"           , "half=bottom,variant=default"            ),
    STONEBRICKSLABS     ( Tier.TERTIARY , "_stonebrickslabs"     , null , Material.ROCK     , SoundType.STONE , "pickaxe" , STONEBRICKSLAB  , "proto_slab"           , "half=bottom,variant=default"            ),
    COBBLEWALL          ( Tier.TERTIARY , "_cobblewall"          , null , Material.ROCK     , SoundType.STONE , "pickaxe" , COBBLE          , "proto_wall"           , "inventory"                              ),
    COBBLEWALLMOSSY     ( Tier.TERTIARY , "_cobblewallmossy"     , null , Material.ROCK     , SoundType.STONE , "pickaxe" , COBBLEMOSSY     , "proto_wall"           , "inventory"                              ),
    STONEWALL           ( Tier.TERTIARY , "_stonewall"           , null , Material.ROCK     , SoundType.STONE , "pickaxe" , STONE           , "proto_wall"           , "inventory"                              ),
    STONEBRICKWALL      ( Tier.TERTIARY , "_stonebrickwall"      , null , Material.ROCK     , SoundType.STONE , "pickaxe" , STONEBRICK      , "proto_wall"           , "inventory"                              ),
    STONEBRICKWALLMOSSY ( Tier.TERTIARY , "_stonebrickwallmossy" , null , Material.ROCK     , SoundType.STONE , "pickaxe" , STONEBRICKMOSSY , "proto_wall"           , "inventory"                              ),
    BUTTON              ( Tier.TERTIARY , "_button"              , null , Material.CIRCUITS , SoundType.STONE , "pickaxe" , STONE           , "proto_button"         , "inventory"                              ),
    LEVER               ( Tier.TERTIARY , "_lever"               , null , Material.CIRCUITS , SoundType.WOOD  , "pickaxe" , COBBLE          , "proto_lever"          , "facing=up_z,powered=false"              ),
    PRESSUREPLATE       ( Tier.TERTIARY , "_pressureplate"       , null , Material.ROCK     , SoundType.STONE , "pickaxe" , STONE           , "proto_pressure_plate" , "powered=false"                          );

    public enum Tier
    {
        PRIMARY,
        SECONDARY,
        TERTIARY
    }

    public final Tier tier;
    public final Boolean isPrimary;
    public final String resourceLocationSuffix;
    public final String fragmentResourceLocationSuffix;
    public final Material material;
    public final SoundType soundType;
    public final String harvestTool;
    public final TileType parentType;
    public final ResourceLocation blockStateResource;
    public final String defaultVariant;
    public final MetaResourceLocation vanillaItemRegistryName;

    TileType(
        Tier tier,
        String resourceLocationSuffix,
        String fragmentResourceLocationSuffix,
        Material material,
        SoundType soundType,
        String harvestTool,
        TileType parentType,
        String blockStateName,
        String defaultVariant )
    {
        this.tier = tier;
        this.isPrimary = tier == Tier.PRIMARY;
        this.resourceLocationSuffix = resourceLocationSuffix;
        this.fragmentResourceLocationSuffix = fragmentResourceLocationSuffix;
        this.material = material;
        this.soundType = soundType;
        this.harvestTool = harvestTool;
        this.parentType = parentType;
        this.blockStateResource = Strata.resource( blockStateName );
        this.defaultVariant = defaultVariant;
        this.vanillaItemRegistryName = vanillaItemRegistryName( this );

        assert ( tier == Tier.PRIMARY ) == ( fragmentResourceLocationSuffix != null );
        assert ( tier == Tier.TERTIARY ) == ( parentType != null );
    }

    @Nonnull
    public ResourceLocation registryName( @Nonnull String tileSetName )
    {
        return Strata.resource( tileSetName + resourceLocationSuffix );
    }

    @Nullable
    public ResourceLocation fragmentRegistryName( @Nonnull String tileSetName )
    {
        return fragmentResourceLocationSuffix != null
            ? Strata.resource( tileSetName + fragmentResourceLocationSuffix )
            : null;
    }

    // Statics

    @Nonnull
    private static MetaResourceLocation vanillaItemRegistryName( @Nonnull TileType tileType )
    {
        // Can't switch on tileType because the constructor calls this
        switch( tileType.name() )
        {
            case "STONE":
                return new MetaResourceLocation( "minecraft:stone" );
            case "COBBLE":
                return new MetaResourceLocation( "minecraft:cobblestone" );
            case "COBBLEMOSSY":
                return new MetaResourceLocation( "minecraft:mossy_cobblestone" );
            case "STONEBRICK":
                return new MetaResourceLocation( "minecraft:stonebrick" );
            case "STONEBRICKMOSSY":
                return new MetaResourceLocation( "minecraft:stonebrick:1" );
            case "COBBLESTAIRS":
                return new MetaResourceLocation( "minecraft:stone_stairs" );
            case "STONEBRICKSTAIRS":
                return new MetaResourceLocation( "minecraft:stone_brick_stairs" );
            case "COBBLESLAB":
                return new MetaResourceLocation( "minecraft:stone_slab:3" );
            case "STONESLAB":
                return new MetaResourceLocation( "minecraft:stone_slab" );
            case "STONEBRICKSLAB":
                return new MetaResourceLocation( "minecraft:stone_slab:5" );
            case "COBBLEWALL":
                return new MetaResourceLocation( "minecraft:cobblestone_wall" );
            case "COBBLEWALLMOSSY":
                return new MetaResourceLocation( "minecraft:cobblestone_wall:1" );
            case "BUTTON":
                return new MetaResourceLocation( "minecraft:stone_button" );
            case "LEVER":
                return new MetaResourceLocation( "minecraft:lever" );
            case "PRESSUREPLATE":
                return new MetaResourceLocation( "minecraft:stone_pressure_plate" );
        }

        // FIXME: This ought to return null but the tile data loader cannot differentiate
        // between an implicit default and a value explicitly set to the default
        return new MetaResourceLocation( "minecraft:air" );
    }
}
