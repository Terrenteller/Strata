package com.riintouge.strata.block.geo;

import com.riintouge.strata.block.GenericCubeTextureMap;
import com.riintouge.strata.block.IForgeRegistrable;
import com.riintouge.strata.block.MetaResourceLocation;
import com.riintouge.strata.image.LayeredTextureLayer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.EnumPlantType;

import javax.annotation.Nullable;
import java.util.ArrayList;

public interface IGeoTileInfo extends IHostInfo , IForgeRegistrable
{
    String tileSetName();

    TileType type();

    @Nullable
    String blockOreDictionaryName();

    @Nullable
    String fragmentItemOreDictionaryName();

    @Nullable
    ItemStack equivalentItemStack();

    @Nullable
    MetaResourceLocation furnaceResult();

    @Nullable
    Float furnaceExp();

    Boolean hasFragment();

    @Nullable
    LayeredTextureLayer[] fragmentTextureLayers();

    @Nullable
    ItemStack equivalentFragmentItemStack();

    @Nullable
    MetaResourceLocation fragmentFurnaceResult();

    @Nullable
    Float fragmentFurnaceExp();

    ArrayList< EnumPlantType > sustainedPlantTypes();

    ArrayList< IBlockState > sustainsPlantsSustainedBy();

    GenericCubeTextureMap modelTextureMap();

    ResourceLocation blockstateResourceLocation();

    String localizedName();
}
