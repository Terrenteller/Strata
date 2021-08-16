package com.riintouge.strata.block.loader;

import com.riintouge.strata.block.GenericCubeTextureMap;
import com.riintouge.strata.block.IFacingTextureMap;
import com.riintouge.strata.block.IForgeRegistrable;
import com.riintouge.strata.block.geo.HostRegistry;
import com.riintouge.strata.block.geo.IHostInfo;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.ResourceLocation;

public final class ImmutableHost implements IHostInfo , IForgeRegistrable
{
    private ResourceLocation registryName;
    private int meta;
    private Material material;
    private SoundType soundType;
    private String harvestTool;
    private int harvestLevel;
    private float hardness;
    private float explosionResistance;
    private GenericCubeTextureMap genericCubeTextureMap;
    private Integer particleFallingColor = null;

    public ImmutableHost(
        ResourceLocation registryName,
        int meta,
        Material material,
        SoundType soundType,
        String harvestTool,
        int harvestLevel,
        float hardness,
        float explosionResistance,
        GenericCubeTextureMap textureMap )
    {
        this.registryName = registryName;
        this.meta = meta;
        this.material = material;
        this.soundType = soundType;
        this.harvestTool = harvestTool;
        this.harvestLevel = harvestLevel;
        this.hardness = hardness;
        this.explosionResistance = explosionResistance;
        this.genericCubeTextureMap = textureMap;
    }

    // IHostInfo overrides

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
    public IFacingTextureMap facingTextureMap()
    {
        return genericCubeTextureMap;
    }

    @Override
    public int particleFallingColor()
    {
        return particleFallingColor != null
            ? particleFallingColor
            : ( particleFallingColor = HostRegistry.getParticleFallingColor( this ) );
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

    // IForgeRegistrable overrides

    @Override
    public void stitchTextures( TextureMap textureMap )
    {
        genericCubeTextureMap.stitchTextures( textureMap );
    }
}
