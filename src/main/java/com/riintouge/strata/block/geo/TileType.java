package com.riintouge.strata.block.geo;

import com.riintouge.strata.Strata;
import net.minecraft.block.BlockStoneSlab;
import net.minecraft.block.BlockWall;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public enum TileType
{
    // Primaries - There can be only one
    CLAY   ( Tier.PRIMARY , "" , Material.CLAY   , SoundType.GROUND , "shovel"  , null , "proto_cube_gimbal" , null ),
    GRAVEL ( Tier.PRIMARY , "" , Material.SAND   , SoundType.GROUND , "shovel"  , null , "proto_cube_gimbal" , null ),
    GROUND ( Tier.PRIMARY , "" , Material.GROUND , SoundType.GROUND , "shovel"  , null , "proto_cube_gimbal" , null ),
    SAND   ( Tier.PRIMARY , "" , Material.SAND   , SoundType.SAND   , "shovel"  , null , "proto_cube_gimbal" , null ),
    STONE  ( Tier.PRIMARY , "" , Material.ROCK   , SoundType.STONE  , "pickaxe" , null , "proto_cube_gimbal" , null ),
    GLASS  ( Tier.PRIMARY , "" , Material.GLASS  , SoundType.GLASS  , ""        , null , "proto_cube_gimbal" , null ),

    // Secondaries - Manually specified derivatives of a primary
    COBBLE          ( Tier.SECONDARY , "_cobble"          , Material.ROCK , SoundType.STONE , "pickaxe" , null , "proto_cube" , null ),
    COBBLEMOSSY     ( Tier.SECONDARY , "_cobblemossy"     , Material.ROCK , SoundType.STONE , "pickaxe" , null , "proto_cube" , null ),
    STONEBRICK      ( Tier.SECONDARY , "_stonebrick"      , Material.ROCK , SoundType.STONE , "pickaxe" , null , "proto_cube" , null ),
    STONEBRICKMOSSY ( Tier.SECONDARY , "_stonebrickmossy" , Material.ROCK , SoundType.STONE , "pickaxe" , null , "proto_cube" , null ),
    STONEPOLISHED   ( Tier.SECONDARY , "_stonepolished"   , Material.ROCK , SoundType.STONE , "pickaxe" , null , "proto_cube" , null ),

    // Tertiaries - Manually* specified derivatives of a primary or secondary
    // * double slabs are special and should not be specified in config files
    COBBLESTAIRS        ( Tier.TERTIARY , "_cobblestairs"        , Material.ROCK     , SoundType.STONE , "pickaxe" , COBBLE          , "proto_stairs"         , "facing=east,half=bottom,shape=straight" ),
    STONESTAIRS         ( Tier.TERTIARY , "_stonestairs"         , Material.ROCK     , SoundType.STONE , "pickaxe" , STONE           , "proto_stairs"         , "facing=east,half=bottom,shape=straight" ),
    STONEBRICKSTAIRS    ( Tier.TERTIARY , "_stonebrickstairs"    , Material.ROCK     , SoundType.STONE , "pickaxe" , STONEBRICK      , "proto_stairs"         , "facing=east,half=bottom,shape=straight" ),
    COBBLESLAB          ( Tier.TERTIARY , "_cobbleslab"          , Material.ROCK     , SoundType.STONE , "pickaxe" , COBBLE          , "proto_slab"           , "half=bottom,variant=default"            ),
    COBBLESLABS         ( Tier.TERTIARY , "_cobbleslabs"         , Material.ROCK     , SoundType.STONE , "pickaxe" , COBBLESLAB      , "proto_slab"           , "half=bottom,variant=default"            ),
    STONESLAB           ( Tier.TERTIARY , "_stoneslab"           , Material.ROCK     , SoundType.STONE , "pickaxe" , STONE           , "proto_slab"           , "half=bottom,variant=default"            ),
    STONESLABS          ( Tier.TERTIARY , "_stoneslabs"          , Material.ROCK     , SoundType.STONE , "pickaxe" , STONESLAB       , "proto_slab"           , "half=bottom,variant=default"            ),
    STONEBRICKSLAB      ( Tier.TERTIARY , "_stonebrickslab"      , Material.ROCK     , SoundType.STONE , "pickaxe" , STONEBRICK      , "proto_slab"           , "half=bottom,variant=default"            ),
    STONEBRICKSLABS     ( Tier.TERTIARY , "_stonebrickslabs"     , Material.ROCK     , SoundType.STONE , "pickaxe" , STONEBRICKSLAB  , "proto_slab"           , "half=bottom,variant=default"            ),
    COBBLEWALL          ( Tier.TERTIARY , "_cobblewall"          , Material.ROCK     , SoundType.STONE , "pickaxe" , COBBLE          , "proto_wall"           , "inventory"                              ),
    COBBLEWALLMOSSY     ( Tier.TERTIARY , "_cobblewallmossy"     , Material.ROCK     , SoundType.STONE , "pickaxe" , COBBLEMOSSY     , "proto_wall"           , "inventory"                              ),
    STONEWALL           ( Tier.TERTIARY , "_stonewall"           , Material.ROCK     , SoundType.STONE , "pickaxe" , STONE           , "proto_wall"           , "inventory"                              ),
    STONEBRICKWALL      ( Tier.TERTIARY , "_stonebrickwall"      , Material.ROCK     , SoundType.STONE , "pickaxe" , STONEBRICK      , "proto_wall"           , "inventory"                              ),
    STONEBRICKWALLMOSSY ( Tier.TERTIARY , "_stonebrickwallmossy" , Material.ROCK     , SoundType.STONE , "pickaxe" , STONEBRICKMOSSY , "proto_wall"           , "inventory"                              ),
    BUTTON              ( Tier.TERTIARY , "_button"              , Material.CIRCUITS , SoundType.STONE , "pickaxe" , STONE           , "proto_button"         , "inventory"                              ),
    LEVER               ( Tier.TERTIARY , "_lever"               , Material.CIRCUITS , SoundType.WOOD  , "pickaxe" , COBBLE          , "proto_lever"          , "facing=up_z,powered=false"              ),
    PRESSUREPLATE       ( Tier.TERTIARY , "_pressureplate"       , Material.ROCK     , SoundType.STONE , "pickaxe" , STONE           , "proto_pressure_plate" , "powered=false"                          );

    public enum Tier
    {
        PRIMARY,
        SECONDARY,
        TERTIARY
    }

    public final Tier tier;
    public final Boolean isPrimary;
    public final String resourceLocationSuffix;
    public final Material material;
    public final SoundType soundType;
    public final String harvestTool;
    public final TileType parentType;
    public final ResourceLocation blockstate;
    public final String defaultVariant;
    public final ItemStack vanillaItemStack;

    TileType(
        Tier tier,
        String resourceLocationSuffix,
        Material material,
        SoundType soundType,
        String harvestTool,
        TileType parentType,
        String blockstate,
        String defaultVariant )
    {
        this.tier = tier;
        this.isPrimary = tier == Tier.PRIMARY;
        this.resourceLocationSuffix = resourceLocationSuffix;
        this.material = material;
        this.soundType = soundType;
        this.harvestTool = harvestTool;
        this.parentType = parentType;
        this.blockstate = Strata.resource( blockstate );
        this.defaultVariant = defaultVariant;
        this.vanillaItemStack = vanillaItemStack( this );
    }

    @Nonnull
    public ResourceLocation registryName( String tileSetName )
    {
        return Strata.resource( tileSetName + resourceLocationSuffix );
    }

    // Statics

    @Nullable
    public static TileType tryValueOf( String value )
    {
        try
        {
            return TileType.valueOf( value );
        }
        catch( IllegalArgumentException e )
        {
            return null;
        }
    }

    @Nullable
    public static ItemStack vanillaItemStack( TileType type )
    {
        // Can't switch on type because the constructor calls this
        switch( type.name() )
        {
            case "STONE":
                return new ItemStack( Blocks.STONE );
            case "COBBLE":
                return new ItemStack( Blocks.COBBLESTONE );
            case "COBBLEMOSSY":
                return new ItemStack( Blocks.MOSSY_COBBLESTONE );
            case "STONEBRICK":
                return new ItemStack( Blocks.STONEBRICK );
            case "STONEBRICKMOSSY":
                return new ItemStack( Blocks.STONEBRICK , 1 , 1 );
            case "COBBLESTAIRS":
                return new ItemStack( Blocks.STONE_STAIRS );
            case "STONEBRICKSTAIRS":
                return new ItemStack( Blocks.STONE_BRICK_STAIRS );
            case "COBBLESLAB":
                return new ItemStack( Blocks.STONE_SLAB , 1 , BlockStoneSlab.EnumType.COBBLESTONE.getMetadata() );
            case "STONESLAB":
                return new ItemStack( Blocks.STONE_SLAB , 1 , BlockStoneSlab.EnumType.STONE.getMetadata() );
            case "STONEBRICKSLAB":
                return new ItemStack( Blocks.STONE_SLAB , 1 , BlockStoneSlab.EnumType.SMOOTHBRICK.getMetadata() );
            case "COBBLEWALL":
                return new ItemStack( Blocks.COBBLESTONE_WALL );
            case "COBBLEWALLMOSSY":
                return new ItemStack( Blocks.COBBLESTONE_WALL , 1 , BlockWall.EnumType.MOSSY.getMetadata() );
            case "BUTTON":
                return new ItemStack( Blocks.STONE_BUTTON );
            case "LEVER":
                return new ItemStack( Blocks.LEVER );
            case "PRESSUREPLATE":
                return new ItemStack( Blocks.STONE_PRESSURE_PLATE );
        }

        return null;
    }
}
