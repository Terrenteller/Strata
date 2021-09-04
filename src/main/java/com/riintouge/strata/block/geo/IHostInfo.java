package com.riintouge.strata.block.geo;

import com.riintouge.strata.block.GenericCubeTextureMap;
import net.minecraft.util.ResourceLocation;

public interface IHostInfo extends IGenericBlockProperties
{
    ResourceLocation registryName();

    int meta();

    GenericCubeTextureMap modelTextureMap();

    int particleFallingColor();
}
