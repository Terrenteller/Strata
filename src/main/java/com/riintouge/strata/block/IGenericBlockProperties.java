package com.riintouge.strata.block;

import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;

public interface IGenericBlockProperties
{
    Material material();

    SoundType soundType();

    String harvestTool();

    int harvestLevel();

    // TODO: resistance, hardness
}
