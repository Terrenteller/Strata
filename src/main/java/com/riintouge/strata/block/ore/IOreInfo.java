package com.riintouge.strata.block.ore;

import com.riintouge.strata.block.StoneStrength;
import net.minecraft.util.ResourceLocation;

public interface IOreInfo
{
    String oreName();

    String oreDictionaryName();

    StoneStrength stoneStrength();

    ResourceLocation oreBlockOverlayTextureResource();

    ResourceLocation oreItemTextureResource();
}
