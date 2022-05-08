package com.riintouge.strata.block.ore;

import com.riintouge.strata.block.ProtoBlockTextureMap;
import com.riintouge.strata.block.IForgeRegistrable;
import com.riintouge.strata.block.MetaResourceLocation;
import com.riintouge.strata.block.geo.ICommonBlockProperties;
import com.riintouge.strata.image.LayeredTextureLayer;
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

    @Nullable
    String blockOreDictionaryName();

    @Nullable
    String itemOreDictionaryName();

    @Nonnull
    @SideOnly( Side.CLIENT )
    ProtoBlockTextureMap modelTextureMap();

    @Nonnull
    ResourceLocation blockstateResourceLocation();

    @Nullable
    @SideOnly( Side.CLIENT )
    List< LayeredTextureLayer > oreItemTextureLayers();

    @Nullable
    ItemStack equivalentItemStack();

    @Nullable
    ItemStack furnaceResult();

    float furnaceExp();

    @Nullable
    IBlockState proxyBlockState();

    @Nullable
    ItemStack proxyBlockItemStack();

    SoundEventTuple ambientSound();

    @Nullable
    WeightedDropCollections weightedDropGroups();

    @Nullable
    MetaResourceLocation forcedHost();

    @Nullable
    List< MetaResourceLocation > hostAffinities();

    int baseExp();

    @Nullable
    String bonusExpExpr();

    @Nullable
    String localizedName();

    @Nullable
    List< String > localizedTooltip();
}
