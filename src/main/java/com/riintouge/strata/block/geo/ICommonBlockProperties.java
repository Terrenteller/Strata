package com.riintouge.strata.block.geo;

import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;

public interface ICommonBlockProperties
{
    Material material();

    SoundType soundType();

    String harvestTool();

    int harvestLevel();

    float hardness();

    float explosionResistance();

    int burnTime();
}
