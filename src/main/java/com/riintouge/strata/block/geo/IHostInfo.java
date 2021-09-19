package com.riintouge.strata.block.geo;

import com.riintouge.strata.block.ProtoBlockTextureMap;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;

public interface IHostInfo extends ICommonBlockProperties
{
    @Nonnull
    ResourceLocation registryName();

    int meta();

    @Nonnull
    ProtoBlockTextureMap modelTextureMap();

    int particleFallingColor();
}
