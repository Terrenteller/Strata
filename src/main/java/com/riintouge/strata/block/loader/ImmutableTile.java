package com.riintouge.strata.block.loader;

import com.riintouge.strata.Strata;
import com.riintouge.strata.block.GenericCubeTextureMap;
import com.riintouge.strata.block.IFacingTextureMap;
import com.riintouge.strata.block.IModelRetexturizerMap;
import com.riintouge.strata.block.geo.IGeoTileInfo;
import com.riintouge.strata.block.geo.TileType;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public final class ImmutableTile implements IGeoTileInfo
{
    private String tileSetName;
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
    private GenericCubeTextureMap genericCubeTextureMap;

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
        GenericCubeTextureMap textureMap,
        ItemStack vanillaEquivalent )
    {
        this.tileSetName = tileSetName;
        this.registryName = type.registryName( new ResourceLocation( Strata.modid , tileSetName ) );
        this.meta = meta;
        this.type = type;
        this.material = material;
        this.soundType = soundType;
        this.harvestTool = harvestTool;
        this.harvestLevel = harvestLevel;
        this.hardness = hardness;
        this.explosionResistance = explosionResistance;
        this.genericCubeTextureMap = textureMap;
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
    public TileType type()
    {
        return type;
    }

    @Override
    public ItemStack vanillaEquivalent()
    {
        return vanillaEquivalent;
    }

    // IHostInfo overrides

    @Override
    public IFacingTextureMap facingTextureMap()
    {
        return genericCubeTextureMap;
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
    public IModelRetexturizerMap modelTextureMap()
    {
        return genericCubeTextureMap;
    }

    // IForgeRegistrable overrides

    @Override
    public void stitchTextures( TextureMap textureMap )
    {
        genericCubeTextureMap.stitchTextures( registryName.getResourcePath() , textureMap );
    }
}
