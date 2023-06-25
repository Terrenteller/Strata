package com.riintouge.strata.block.ore;

import com.riintouge.strata.misc.IForgeRegistrable;
import com.riintouge.strata.misc.MetaResourceLocation;
import com.riintouge.strata.block.ProtoBlockTextureMap;
import com.riintouge.strata.block.ICommonBlockProperties;
import com.riintouge.strata.block.geo.TileType;
import com.riintouge.strata.image.LayeredTextureLayer;
import com.riintouge.strata.item.IDropFormula;
import com.riintouge.strata.item.WeightedDropCollections;
import com.riintouge.strata.sound.SoundEventTuple;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public interface IOreInfo extends ICommonBlockProperties , IForgeRegistrable
{
    @Nonnull
    String oreName();

    @Nonnull
    TileType type();

    @Nullable
    String blockOreDictionaryName();

    @Nullable
    String itemOreDictionaryName();

    @Nonnull
    @SideOnly( Side.CLIENT )
    ProtoBlockTextureMap modelTextureMap();

    @SideOnly( Side.CLIENT )
    int particleFallingColor();

    @Nonnull
    ResourceLocation blockStateResourceLocation();

    @Nullable
    @SideOnly( Side.CLIENT )
    LayeredTextureLayer[] oreItemTextureLayers();

    @Nullable
    ItemStack equivalentItemStack();

    @Nullable
    ItemStack furnaceResult();

    float furnaceExperience();

    @Nullable
    IBlockState proxyBlockState();

    @Nullable
    ItemStack proxyBlockItemStack();

    @Nullable
    MetaResourceLocation forcedHost();

    @Nullable
    List< MetaResourceLocation > hostAffinities();

    @Nullable
    MetaResourceLocation breaksInto();

    @Nullable
    WeightedDropCollections weightedDropGroups();

    @Nullable
    IDropFormula experienceDropFormula();

    @Nullable
    SoundEventTuple ambientSound();

    @Nullable
    String localizedName();

    @Nullable
    List< String > localizedTooltip();
}
