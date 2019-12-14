package com.riintouge.strata.block;

import net.minecraft.util.ResourceLocation;

public interface IGenericTileSetInfo extends IGenericBlockProperties
{
    String stoneName();

    ResourceLocation baseTextureLocation();
}
