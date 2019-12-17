package com.riintouge.strata.block.geo.info;

import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.util.ResourceLocation;

public enum VanillaHostTileSetInfo implements IGenericTileSetInfo
{
    STONE(    "stone" , 0 , "blocks/stone"          , Material.ROCK   , SoundType.STONE  , "pickaxe" , 0 , 1.5f , 10.0f ),
    GRANITE(  "stone" , 1 , "blocks/stone_granite"  , Material.ROCK   , SoundType.STONE  , "pickaxe" , 0 , 1.5f , 10.0f ),
    DIORITE(  "stone" , 3 , "blocks/stone_diorite"  , Material.ROCK   , SoundType.STONE  , "pickaxe" , 0 , 1.5f , 10.0f ),
    ANDESITE( "stone" , 5 , "blocks/stone_andesite" , Material.ROCK   , SoundType.STONE  , "pickaxe" , 0 , 1.5f , 10.0f ),
    DIRT(     "dirt"  , 0 , "blocks/dirt"           , Material.GROUND , SoundType.GROUND , "shovel"  , 0 , 0.5f ,  2.5f ),
    GRASS(    "grass" , 0 , "blocks/dirt"           , Material.GROUND , SoundType.GROUND , "shovel"  , 0 , 0.6f ,  3.0f ),
    CLAY(     "clay"  , 0 , "blocks/clay"           , Material.GROUND , SoundType.GROUND , "shovel"  , 0 , 0.6f ,  3.0f );

    private String hostRegistryName;
    private int meta;
    private ResourceLocation textureResource;
    private Material material;
    private SoundType soundType;
    private String harvestTool;
    private int harvestLevel;
    private float hardness;
    private float explosionResistance;

    VanillaHostTileSetInfo(
        String name,
        int meta,
        String textureResource,
        Material material,
        SoundType soundType,
        String harvestTool,
        int harvestLevel,
        float hardness,
        float explosionResistance )
    {
        this.hostRegistryName = new ResourceLocation( "minecraft" , name ).toString();
        this.meta = meta;
        this.textureResource = new ResourceLocation( "minecraft" , textureResource );
        this.material = material;
        this.soundType = soundType;
        this.harvestTool = harvestTool;
        this.harvestLevel = harvestLevel;
        this.hardness = hardness;
        this.explosionResistance = explosionResistance;
    }

    public int getMeta()
    {
        return meta;
    }

    // IGenericTileSetInfo overrides

    @Override
    public String stoneName()
    {
        return hostRegistryName;
    }

    @Override
    public ResourceLocation baseTextureLocation()
    {
        return textureResource;
    }

    @Override
    public Material material()
    {
        return material;
    }

    @Override
    public SoundType soundType()
    {
        return soundType;
    }

    @Override
    public String harvestTool()
    {
        return harvestTool;
    }

    @Override
    public int harvestLevel()
    {
        return harvestLevel;
    }

    @Override
    public float hardness()
    {
        return hardness;
    }

    @Override
    public float explosionResistance()
    {
        return explosionResistance;
    }
}
