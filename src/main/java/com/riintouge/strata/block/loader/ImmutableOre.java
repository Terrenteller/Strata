package com.riintouge.strata.block.loader;

import com.riintouge.strata.block.GenericCubeTextureMap;
import com.riintouge.strata.block.IForgeRegistrable;
import com.riintouge.strata.block.ore.IOreInfo;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;

public final class ImmutableOre implements IOreInfo , IForgeRegistrable
{
    private String oreName;
    private String oreDictionaryName;
    private GenericCubeTextureMap genericCubeTextureMap;
    private Material material;
    private SoundType soundType;
    private String harvestTool;
    private int harvestLevel;
    private float hardness;
    private float explosionResistance;
    private ResourceLocation proxyBlockResource;
    private Block proxyBlock;
    private ItemStack vanillaEquivalent;
    private int burnTime;

    public ImmutableOre(
        String oreName,
        String oreDictionaryName,
        GenericCubeTextureMap textureMap,
        ResourceLocation proxyBlockResource,
        ItemStack vanillaEquivalent,
        Material material,
        SoundType soundType,
        String harvestTool,
        int harvestLevel,
        float hardness,
        float explosionResistance,
        int burnTime )
    {
        this.oreName = oreName;
        this.oreDictionaryName = oreDictionaryName;
        this.genericCubeTextureMap = textureMap;
        this.material = material;
        this.soundType = soundType;
        this.harvestTool = harvestTool;
        this.harvestLevel = harvestLevel;
        this.hardness = hardness;
        this.explosionResistance = explosionResistance == 0.0f ? 1.7f * hardness : explosionResistance;
        this.proxyBlockResource = proxyBlockResource;
        this.vanillaEquivalent = vanillaEquivalent;
        this.burnTime = burnTime;
    }

    // IOreInfo overrides

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
    public GenericCubeTextureMap modelTextureMap()
    {
        return genericCubeTextureMap;
    }

    @Override
    public ResourceLocation oreItemTextureResource()
    {
        return genericCubeTextureMap.getOrDefault( (EnumFacing)null );
    }

    @Override
    public ItemStack vanillaEquivalent()
    {
        return vanillaEquivalent;
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

    // IGenericBlockProperties overrides

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

    @Override
    public int burnTime()
    {
        return burnTime;
    }

    // IForgeRegistrable overrides

    @Override
    public void stitchTextures( TextureMap textureMap )
    {
        genericCubeTextureMap.stitchTextures( oreName , textureMap );
    }
}
