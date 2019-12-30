package com.riintouge.strata.block.geo;

import com.riintouge.strata.Strata;
import com.riintouge.strata.image.LayeredTexture;
import com.riintouge.strata.image.LayeredTextureLayer;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class GenericTile implements IGenericTile
{
    protected String groupName;
    protected ResourceLocation registryName;
    protected int meta;
    protected TileType type;
    protected Material material;
    protected SoundType soundType;
    protected String harvestTool;
    protected int harvestLevel;
    protected float hardness;
    protected float explosionResistance;
    protected ItemStack vanillaEquivalent;
    protected LayeredTextureLayer[] layers;
    protected TextureAtlasSprite texture;

    public GenericTile(
        String groupName,
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
        this.groupName = groupName;
        this.registryName = type.registryName( new ResourceLocation( Strata.modid , groupName ) );
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

    // IGenericTile overrides

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
    public LayeredTextureLayer[] textureLayers()
    {
        return layers;
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
