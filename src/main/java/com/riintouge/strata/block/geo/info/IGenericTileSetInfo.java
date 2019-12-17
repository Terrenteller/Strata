package com.riintouge.strata.block.geo.info;

import com.riintouge.strata.block.geo.IGenericBlockProperties;
import net.minecraft.util.ResourceLocation;

// TODO: This is becoming a poor name for its purposes. IHostInfo?
public interface IGenericTileSetInfo extends IGenericBlockProperties
{
    // TODO: Rename this so it makes sense to return a FQRL
    String stoneName();

    // TODO: Can't assume a single texture on all sides
    ResourceLocation baseTextureLocation();
}
