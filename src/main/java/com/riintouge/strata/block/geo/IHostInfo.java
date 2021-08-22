package com.riintouge.strata.block.geo;

import com.riintouge.strata.block.IFacingTextureMap;
import net.minecraft.util.ResourceLocation;

public interface IHostInfo extends IGenericBlockProperties
{
    ResourceLocation registryName();

    int meta();

    IFacingTextureMap facingTextureMap();

    int particleFallingColor();
}
