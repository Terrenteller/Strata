package com.riintouge.strata.block.loader;

import com.riintouge.strata.block.IForgeRegistrable;
import com.riintouge.strata.block.MetaResourceLocation;
import com.riintouge.strata.block.ParticleHelper;
import com.riintouge.strata.block.ProtoBlockTextureMap;
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
import javax.annotation.Nullable;

public final class ImmutableHost implements IHostInfo , IForgeRegistrable
{
    // IHostInfo
    private MetaResourceLocation hostMetaResource;
    private Float slipperiness;
    private Integer meltsAt;
    private MetaResourceLocation meltsInto;
    private Integer sublimatesAt;
    private MetaResourceLocation sublimatesInto;
    private ProtoBlockTextureMap modelTextureMap;
    private Integer particleFallingColor = null; // Lazily evaluated

    // ICommonBlockProperties
    private Material material;
    private SoundType soundType;
    private String harvestTool;
    private int harvestLevel;
    private float hardness;
    private float explosionResistance;
    private int lightLevel;
    private int burnTime;
    private long specialBlockPropertyFlags;

    public ImmutableHost( TileData tileData ) throws NullPointerException
    {
        Util.argumentNullCheck( tileData , "tileData" );

        // IHostInfo
        this.hostMetaResource = Util.argumentNullCheck( tileData.hostMetaResource , "hostMetaResource" );
        this.slipperiness = tileData.slipperiness;
        this.meltsAt = tileData.meltsAt;
        this.meltsInto = tileData.meltsInto;
        this.sublimatesAt = tileData.sublimatesAt;
        this.sublimatesInto = tileData.sublimatesInto;
        this.modelTextureMap = Util.argumentNullCheck( tileData.textureMap , "texture" );

        // ICommonBlockProperties
        this.material = Util.argumentNullCheck( tileData.material , "material" );
        this.soundType = Util.argumentNullCheck( tileData.soundType , "soundType" );
        this.harvestTool = Util.argumentNullCheck( tileData.harvestTool , "harvestTool" );
        this.harvestLevel = Util.coalesce( tileData.harvestLevel , 0 );
        this.hardness = Util.coalesce( tileData.hardness , 1.5f );
        this.explosionResistance = Util.coalesce( tileData.explosionResistance , 10.0f );
        this.lightLevel = Util.coalesce( tileData.lightLevel , 0 );
        this.burnTime = Util.coalesce( tileData.burnTime , 0 );
        this.specialBlockPropertyFlags = Util.coalesce( tileData.specialBlockPropertyFlags , 0L );
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

    @Nullable
    @Override
    public Float slipperiness()
    {
        return slipperiness;
    }

    @Nullable
    @Override
    public Integer meltsAt()
    {
        return meltsAt;
    }

    @Nullable
    @Override
    public MetaResourceLocation meltsInto()
    {
        return meltsInto;
    }

    @Nullable
    @Override
    public Integer sublimatesAt()
    {
        return sublimatesAt;
    }

    @Nullable
    @Override
    public MetaResourceLocation sublimatesInto()
    {
        return sublimatesInto;
    }

    @Override
    public boolean ticksRandomly()
    {
        return meltsAt != null;
    }

    @Override
    @SideOnly( Side.CLIENT )
    public ProtoBlockTextureMap modelTextureMap()
    {
        return modelTextureMap;
    }

    @Override
    @SideOnly( Side.CLIENT )
    public int particleFallingColor()
    {
        if( particleFallingColor == null )
            particleFallingColor = ParticleHelper.getParticleFallingColor( modelTextureMap().get( EnumFacing.DOWN ) );

        return particleFallingColor;
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
    public int lightLevel()
    {
        return lightLevel;
    }

    @Override
    public int burnTime()
    {
        return burnTime;
    }

    @Override
    public long specialBlockPropertyFlags()
    {
        return specialBlockPropertyFlags;
    }

    // IForgeRegistrable overrides

    @Override
    @SideOnly( Side.CLIENT )
    public void stitchTextures( TextureMap textureMap )
    {
        modelTextureMap.stitchTextures( textureMap );
    }
}
