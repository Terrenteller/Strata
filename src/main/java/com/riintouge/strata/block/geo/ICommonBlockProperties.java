package com.riintouge.strata.block.geo;

import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;

import javax.annotation.Nonnull;

public interface ICommonBlockProperties
{
    @Nonnull
    Material material();

    @Nonnull
    SoundType soundType();

    @Nonnull
    String harvestTool();

    int harvestLevel();

    float hardness();

    float explosionResistance();

    int burnTime();
}