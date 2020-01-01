package com.riintouge.strata.block.loader;

import com.riintouge.strata.Strata;
import com.riintouge.strata.block.geo.IGeoTileInfo;
import com.riintouge.strata.block.geo.TileType;
import com.riintouge.strata.image.LayeredTexture;
import com.riintouge.strata.image.LayeredTextureLayer;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public final class ImmutableTile implements IGeoTileInfo
{
    private ResourceLocation registryName;
    private int meta;
    private TileType type;
    private Material material;
    private SoundType soundType;
    private String harvestTool;
    private int harvestLevel;
    private float hardness;
    private float explosionResistance;
    private ItemStack vanillaEquivalent;
    private LayeredTextureLayer[] layers;

    public ImmutableTile(
        String tileSetName,
        int meta,
        TileType type,
        Material material,
        SoundType soundType,
        String harvestTool,
        int harvestLevel,
        float hardness,
        float explosionResistance,
        LayeredTextureLayer[] layers,
        ItemStack vanillaEquivalent )
    {
        this.registryName = type.registryName( new ResourceLocation( Strata.modid , tileSetName ) );
        this.meta = meta;
        this.type = type;
        this.material = material;
        this.soundType = soundType;
        this.harvestTool = harvestTool;
        this.harvestLevel = harvestLevel;
        this.hardness = hardness;
        this.explosionResistance = explosionResistance;
        this.layers = layers;
        this.vanillaEquivalent = vanillaEquivalent;
    }

    // IGeoTileInfo overrides

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
        return registryName;
    }

    @Override
    public TileType type()
    {
        return type;
    }

    @Override
    public ItemStack vanillaEquivalent()
    {
        return vanillaEquivalent;
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
        textureMap.setTextureEntry( new LayeredTexture( registryName , layers ) );
    }
}
