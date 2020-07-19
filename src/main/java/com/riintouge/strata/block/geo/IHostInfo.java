package com.riintouge.strata.block.geo;

import com.riintouge.strata.block.IFacingTextureMap;
import net.minecraft.util.ResourceLocation;

public interface IHostInfo extends IGenericBlockProperties
{
    ResourceLocation registryName();

    default int meta()
    {
        return 0;
    }

    IFacingTextureMap facingTextureMap();
}
