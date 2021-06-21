package com.riintouge.strata.block.geo;

import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;

import javax.annotation.Nullable;

public interface IGenericBlockProperties
{
    Material material();

    SoundType soundType();

    String harvestTool();

    int harvestLevel();

    float hardness();

    float explosionResistance();

    default int burnTime()
    {
        return 0;
    }

    default int baseDropAmount()
    {
        return 1;
    }

    @Nullable
    default String bonusDropExpr()
    {
        return null;
    }

    default int baseExp()
    {
        return 0;
    }

    @Nullable
    default String bonusExpExpr()
    {
        return null;
    }
}
