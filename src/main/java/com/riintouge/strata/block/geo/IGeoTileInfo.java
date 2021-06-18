package com.riintouge.strata.block.geo;

import com.riintouge.strata.block.IForgeRegistrable;
import com.riintouge.strata.block.IModelRetexturizerMap;
import net.minecraft.item.ItemStack;

public interface IGeoTileInfo extends IHostInfo , IForgeRegistrable
{
    String tileSetName();

    TileType type();

    ItemStack equivalentItem();

    IModelRetexturizerMap modelTextureMap();
}
