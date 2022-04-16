package com.riintouge.strata.block.geo;

import com.riintouge.strata.block.IForgeRegistrable;
import com.riintouge.strata.block.MetaResourceLocation;
import com.riintouge.strata.image.LayeredTextureLayer;
import com.riintouge.strata.sound.SoundEventTuple;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.EnumPlantType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;

public interface IGeoTileInfo extends IHostInfo , IForgeRegistrable
{
    @Nonnull
    String tileSetName();

    @Nonnull
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

    boolean hasFragment();

    @Nullable
    LayeredTextureLayer[] fragmentTextureLayers();

    @Nullable
    ItemStack equivalentFragmentItemStack();

    @Nullable
    MetaResourceLocation fragmentFurnaceResult();

    @Nullable
    Float fragmentFurnaceExp();

    @Nonnull
    ArrayList< EnumPlantType > sustainedPlantTypes();

    @Nonnull
    ArrayList< IBlockState > sustainsPlantsSustainedBy();

    @Nonnull
    ResourceLocation blockstateResourceLocation();

    SoundEventTuple ambientSound();

    @Nullable
    String localizedName();
}
