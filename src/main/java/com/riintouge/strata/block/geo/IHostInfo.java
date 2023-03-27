package com.riintouge.strata.block.geo;

import com.riintouge.strata.block.ProtoBlockTextureMap;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface IHostInfo extends ICommonBlockProperties
{
    @Nonnull
    ResourceLocation registryName();

    int meta();

    @Nullable
    Float slipperiness();

    @Nullable
    Integer meltsAt();

    @SideOnly( Side.CLIENT )
    ProtoBlockTextureMap modelTextureMap();

    @SideOnly( Side.CLIENT )
    int particleFallingColor();
}
