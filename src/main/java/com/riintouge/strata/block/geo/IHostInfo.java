package com.riintouge.strata.block.geo;

import net.minecraft.util.ResourceLocation;

public interface IHostInfo extends IGenericBlockProperties
{
    ResourceLocation registryName();

    default int meta()
    {
        return 0;
    }

    // TODO: Can't assume a single texture on all sides
    ResourceLocation baseTextureLocation();
}
