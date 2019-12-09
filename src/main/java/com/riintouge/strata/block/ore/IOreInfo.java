package com.riintouge.strata.block.ore;

import com.riintouge.strata.block.StoneStrength;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.util.ResourceLocation;

public interface IOreInfo
{
    String oreName();

    String oreDictionaryName();

    Material material();

    SoundType soundType();

    StoneStrength stoneStrength();

    ResourceLocation oreBlockOverlayTextureResource();

    ResourceLocation oreItemTextureResource();
}
