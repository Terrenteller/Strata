package com.riintouge.strata.block.geo;

import com.riintouge.strata.item.WeightedDropCollections;
import com.riintouge.strata.misc.IForgeRegistrable;
import com.riintouge.strata.misc.MetaResourceLocation;
import com.riintouge.strata.block.host.IHostInfo;
import com.riintouge.strata.image.LayeredTextureLayer;
import com.riintouge.strata.item.IDropFormula;
import com.riintouge.strata.sound.SoundEventTuple;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.EnumPlantType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public interface IGeoTileInfo extends IHostInfo, IForgeRegistrable
{
    @Nonnull
    String tileSetName();

    @Nonnull
    TileType tileType();

    @Nullable
    String blockOreDictionaryName();

    @Nullable
    String fragmentItemOreDictionaryName();

    @Nullable
    ItemStack equivalentItemStack();

    @Nullable
    ItemStack furnaceResult();

    float furnaceExperience();

    boolean hasFragment();

    @Nullable
    LayeredTextureLayer[] fragmentTextureLayers();

    @Nullable
    IDropFormula fragmentDropFormula();

    @Nullable
    ItemStack equivalentFragmentItemStack();

    @Nullable
    ItemStack fragmentFurnaceResult();

    float fragmentFurnaceExperience();

    int fragmentBurnTime();

    @Nullable
    MetaResourceLocation breaksInto();

    @Nullable
    WeightedDropCollections weightedDropGroups();

    @Nonnull
    ArrayList< EnumPlantType > sustainedPlantTypes();

    @Nonnull
    ArrayList< IBlockState > sustainsPlantsSustainedBy();

    @Nonnull
    ResourceLocation blockStateResource();

    @Nullable
    IDropFormula experienceDropFormula();

    @Nullable
    SoundEventTuple ambientSound();

    @Nullable
    Integer lightOpacity();

    @Nullable
    String localizedName();

    @Nullable
    List< String > localizedTooltip();
}
