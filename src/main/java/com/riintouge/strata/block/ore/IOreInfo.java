package com.riintouge.strata.block.ore;

import com.riintouge.strata.block.GenericCubeTextureMap;
import com.riintouge.strata.block.IForgeRegistrable;
import com.riintouge.strata.block.geo.IGenericBlockProperties;
import net.minecraft.block.Block;
import net.minecraft.util.ResourceLocation;

public interface IOreInfo extends IGenericBlockProperties , IForgeRegistrable
{
    String oreName();

    String oreDictionaryName();

    GenericCubeTextureMap modelTextureMap();

    ResourceLocation oreItemTextureResource();

    default Block proxyBlock()
    {
        return null;
    }

    // IGenericBlockProperties overrides

    @Override
    default float explosionResistance()
    {
        return 1.7f * hardness(); // Roughly matches vanilla ore resistance
    }
}
