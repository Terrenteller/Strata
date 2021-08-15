package com.riintouge.strata.block.geo;

import com.riintouge.strata.block.IForgeRegistrable;
import com.riintouge.strata.block.IModelRetexturizerMap;
import com.riintouge.strata.image.LayeredTextureLayer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.EnumPlantType;

import java.util.ArrayList;

public interface IGeoTileInfo extends IHostInfo , IForgeRegistrable
{
    String tileSetName();

    TileType type();

    ItemStack equivalentItem();

    Boolean hasFragment();

    LayeredTextureLayer[] fragmentTextureLayers();

    ItemStack equivalentFragmentItem();

    ArrayList< EnumPlantType > sustainedPlantTypes();

    ArrayList< IBlockState > sustainsPlantsSustainedBy();

    IModelRetexturizerMap modelTextureMap();
}
