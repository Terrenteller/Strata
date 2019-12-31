package com.riintouge.strata.block.ore;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.util.ResourceLocation;

public class CustomOre implements IOreInfo
{
    private String oreName;
    private String oreDictionaryName;
    private ResourceLocation textureResource;
    private Material material;
    private SoundType soundType;
    private String harvestTool;
    private int harvestLevel;
    private float hardness;
    private float explosionResistance;
    private ResourceLocation proxyBlockResource;
    private Block proxyBlock;

    public CustomOre(
        String oreName,
        String oreDictionaryName,
        ResourceLocation textureResource,
        ResourceLocation proxyBlockResource,
        Material material,
        SoundType soundType,
        String harvestTool,
        int harvestLevel,
        float hardness,
        float explosionResistance )
    {
        this.oreName = oreName;
        this.oreDictionaryName = oreDictionaryName;
        this.textureResource = textureResource;
        this.material = material;
        this.soundType = soundType;
        this.harvestTool = harvestTool;
        this.harvestLevel = harvestLevel;
        this.hardness = hardness;
        this.explosionResistance = explosionResistance == 0.0f ? 1.7f * hardness : explosionResistance;
        this.proxyBlockResource = proxyBlockResource;
    }

    @Override
    public String oreName()
    {
        return oreName;
    }

    @Override
    public String oreDictionaryName()
    {
        return oreDictionaryName;
    }

    @Override
    public ResourceLocation oreBlockOverlayTextureResource()
    {
        return textureResource;
    }

    @Override
    public ResourceLocation oreItemTextureResource()
    {
        return oreBlockOverlayTextureResource();
    }

    @Override
    public Block proxyBlock()
    {
        // Defer resolution until reasonably sure the block has been created
        if( proxyBlockResource != null )
        {
            proxyBlock = Block.REGISTRY.getObject( proxyBlockResource );
            proxyBlockResource = null;
        }

        return proxyBlock;
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
