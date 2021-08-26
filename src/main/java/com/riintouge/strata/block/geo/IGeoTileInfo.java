package com.riintouge.strata.block.geo;

import com.riintouge.strata.block.IForgeRegistrable;
import com.riintouge.strata.block.IModelRetexturizerMap;
import com.riintouge.strata.image.LayeredTextureLayer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.EnumPlantType;

import javax.annotation.Nullable;
import java.util.ArrayList;

public interface IGeoTileInfo extends IHostInfo , IForgeRegistrable
{
    String tileSetName();

    TileType type();

    @Nullable
    ItemStack equivalentItemStack();

    Boolean hasFragment();

    @Nullable
    LayeredTextureLayer[] fragmentTextureLayers();

    @Nullable
    ItemStack equivalentFragmentItemStack();

    ArrayList< EnumPlantType > sustainedPlantTypes();

    ArrayList< IBlockState > sustainsPlantsSustainedBy();

    IModelRetexturizerMap modelTextureMap();

    String localizedName();
}
