package com.riintouge.strata.block.geo;

import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;

public interface IGenericBlockProperties
{
    Material material();

    SoundType soundType();

    String harvestTool();

    int harvestLevel();

    float hardness();

    default float explosionResistance()
    {
        return 5.0f * hardness();
    }
}
