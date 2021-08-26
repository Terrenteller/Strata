package com.riintouge.strata.block.ore;

import com.riintouge.strata.block.GenericCubeTextureMap;
import com.riintouge.strata.block.IForgeRegistrable;
import com.riintouge.strata.block.geo.IGenericBlockProperties;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;

public interface IOreInfo extends IGenericBlockProperties , IForgeRegistrable
{
    String oreName();

    @Nullable
    String blockOreDictionaryName();

    @Nullable
    String itemOreDictionaryName();

    GenericCubeTextureMap modelTextureMap();

    ResourceLocation oreItemTextureResource();

    @Nullable
    ItemStack equivalentItemStack();

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
