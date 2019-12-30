package com.riintouge.strata.block.geo;

import com.riintouge.strata.block.IForgeRegistrable;
import com.riintouge.strata.image.LayeredTextureLayer;
import net.minecraft.item.ItemStack;

public interface IGenericTile extends IHostInfo , IForgeRegistrable
{
    TileType type();

    LayeredTextureLayer[] textureLayers();

    ItemStack vanillaEquivalent();
}
