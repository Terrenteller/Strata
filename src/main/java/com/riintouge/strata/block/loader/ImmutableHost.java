package com.riintouge.strata.block.loader;

import com.riintouge.strata.block.geo.IHostInfo;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.util.ResourceLocation;

public final class ImmutableHost implements IHostInfo
{
    private ResourceLocation registryName;
    private int meta;
    private ResourceLocation textureResource;
    private Material material;
    private SoundType soundType;
    private String harvestTool;
    private int harvestLevel;
    private float hardness;
    private float explosionResistance;

    public ImmutableHost(
        ResourceLocation registryName,
        int meta,
        ResourceLocation textureResource,
        Material material,
        SoundType soundType,
        String harvestTool,
        int harvestLevel,
        float hardness,
        float explosionResistance )
    {
        this.registryName = registryName;
        this.meta = meta;
        this.textureResource = textureResource;
        this.material = material;
        this.soundType = soundType;
        this.harvestTool = harvestTool;
        this.harvestLevel = harvestLevel;
        this.hardness = hardness;
        this.explosionResistance = explosionResistance;
    }

    @Override
    public ResourceLocation registryName()
    {
        return registryName;
    }

    @Override
    public int meta()
    {
        return meta;
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
