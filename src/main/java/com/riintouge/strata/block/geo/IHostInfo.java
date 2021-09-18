package com.riintouge.strata.block.geo;

import com.riintouge.strata.block.ProtoBlockTextureMap;
import net.minecraft.util.ResourceLocation;

public interface IHostInfo extends ICommonBlockProperties
{
    ResourceLocation registryName();

    int meta();

    ProtoBlockTextureMap modelTextureMap();

    int particleFallingColor();
}
