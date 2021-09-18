package com.riintouge.strata.block.ore;

import com.riintouge.strata.block.ProtoBlockTextureMap;
import com.riintouge.strata.block.IForgeRegistrable;
import com.riintouge.strata.block.MetaResourceLocation;
import com.riintouge.strata.block.geo.ICommonBlockProperties;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;

public interface IOreInfo extends ICommonBlockProperties, IForgeRegistrable
{
    String oreName();

    @Nullable
    String blockOreDictionaryName();

    @Nullable
    String itemOreDictionaryName();

    ProtoBlockTextureMap modelTextureMap();

    ResourceLocation blockstateResourceLocation();

    ResourceLocation oreItemTextureResource();

    @Nullable
    ItemStack equivalentItemStack();

    @Nullable
    MetaResourceLocation furnaceResult();

    @Nullable
    Float furnaceExp();

    @Nullable
    IBlockState proxyBlockState();

    int baseDropAmount();

    @Nullable
    String bonusDropExpr();

    int baseExp();

    @Nullable
    String bonusExpExpr();

    String localizedName();
}
