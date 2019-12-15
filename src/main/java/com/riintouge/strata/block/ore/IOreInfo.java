package com.riintouge.strata.block.ore;

import com.riintouge.strata.block.IGenericBlockProperties;
import net.minecraft.util.ResourceLocation;

public interface IOreInfo extends IGenericBlockProperties
{
    String oreName();

    String oreDictionaryName();

    ResourceLocation oreBlockOverlayTextureResource();

    ResourceLocation oreItemTextureResource();

    // IGenericBlockProperties overrides

    @Override
    default float explosionResistance()
    {
        return 1.7f * hardness(); // Roughly matches vanilla ore resistance
    }
}
