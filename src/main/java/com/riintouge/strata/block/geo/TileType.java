package com.riintouge.strata.block.geo;

import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.util.ResourceLocation;

public enum TileType
{
    // Primaries
    CLAY   ( true , "%s:%s" , Material.CLAY   , SoundType.GROUND , "shovel"  ),
    GROUND ( true , "%s:%s" , Material.GROUND , SoundType.GROUND , "shovel"  ),
    SAND   ( true , "%s:%s" , Material.SAND   , SoundType.SAND   , "shovel"  ),
    STONE  ( true , "%s:%s" , Material.ROCK   , SoundType.STONE  , "pickaxe" ),

    // Secondaries
    BRICK      ( false , "%s:%s_brick"      , Material.ROCK , SoundType.STONE , "pickaxe" ),
    COBBLE     ( false , "%s:%s_cobble"     , Material.ROCK , SoundType.STONE , "pickaxe" ),
    STONEBRICK ( false , "%s:%s_stonebrick" , Material.ROCK , SoundType.STONE , "pickaxe" );

    public final Boolean isPrimary;
    public final Material material;
    public final SoundType soundType;
    public final String harvestTool;
    private String resourceLocationFormat;

    TileType( Boolean isPrimary , String resourceLocationFormat , Material material , SoundType soundType , String harvestTool )
    {
        this.isPrimary = isPrimary;
        this.resourceLocationFormat = resourceLocationFormat;
        this.material = material;
        this.soundType = soundType;
        this.harvestTool = harvestTool;
    }

    public ResourceLocation registryName( ResourceLocation baseLocation )
    {
        return new ResourceLocation( String.format( resourceLocationFormat , baseLocation.getResourceDomain() , baseLocation.getResourcePath() ) );
    }

    // TODO: slabs (cobble, stone, brick), walls (cobble, stone, brick), stairs (cobble, stone, brick), etc.
}
