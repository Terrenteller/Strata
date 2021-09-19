package com.riintouge.strata.block.loader;

import com.riintouge.strata.block.ProtoBlockTextureMap;
import com.riintouge.strata.block.IForgeRegistrable;
import com.riintouge.strata.block.MetaResourceLocation;
import com.riintouge.strata.block.ParticleHelper;
import com.riintouge.strata.block.geo.IHostInfo;
import com.riintouge.strata.util.Util;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;

public final class ImmutableHost implements IHostInfo , IForgeRegistrable
{
    // IHostInfo
    private MetaResourceLocation hostMetaResource;
    private ProtoBlockTextureMap modelTextureMap;
    private Integer particleFallingColor = null; // Lazily evaluated

    // ICommonBlockProperties
    private Material material;
    private SoundType soundType;
    private String harvestTool;
    private int harvestLevel;
    private float hardness;
    private float explosionResistance;
    private int burnTime;

    public ImmutableHost( TileData tileData ) throws IllegalArgumentException
    {
        // IHostInfo
        this.hostMetaResource = Util.argumentNullCheck( tileData.hostMetaResource , "hostMetaResource" );
        this.modelTextureMap = Util.argumentNullCheck( tileData.textureMap , "texture" );

        // ICommonBlockProperties
        this.material = Util.argumentNullCheck( tileData.material , "material" );
        this.soundType = Util.argumentNullCheck( tileData.soundType , "soundType" );
        this.harvestTool = Util.argumentNullCheck( tileData.harvestTool , "harvestTool" );
        this.harvestLevel = Util.coalesce( tileData.harvestLevel , 0 );
        this.hardness = Util.coalesce( tileData.hardness , 1.0f );
        this.explosionResistance = Util.coalesce( tileData.explosionResistance , 5.0f * this.hardness );
        this.burnTime = Util.coalesce( tileData.burnTime , 0 );
    }

    // IHostInfo overrides

    @Nonnull
    @Override
    public ResourceLocation registryName()
    {
        return hostMetaResource.resourceLocation;
    }

    @Override
    public int meta()
    {
        return hostMetaResource.meta;
    }

    @Nonnull
    @Override
    public ProtoBlockTextureMap modelTextureMap()
    {
        return modelTextureMap;
    }

    @Override
    public int particleFallingColor()
    {
        return particleFallingColor != null
            ? particleFallingColor
            : ( particleFallingColor = ParticleHelper.getParticleFallingColor( modelTextureMap().get( EnumFacing.DOWN ) ) );
    }

    // ICommonBlockProperties overrides

    @Nonnull
    @Override
    public Material material()
    {
        return material;
    }

    @Nonnull
    @Override
    public SoundType soundType()
    {
        return soundType;
    }

    @Nonnull
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
    @SideOnly( Side.CLIENT )
    public void stitchTextures( TextureMap textureMap )
    {
        modelTextureMap.stitchTextures( textureMap );
    }
}
