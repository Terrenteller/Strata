package com.riintouge.strata.block;

import net.minecraft.util.ResourceLocation;

public interface IGenericTileSetInfo
{
    String stoneName();

    StoneStrength stoneStrength();

    // TODO
    //Material material();
    //SoundType soundType();
    // harvest tool? level? resistance? hardness?

    ResourceLocation baseTextureLocation();
}
