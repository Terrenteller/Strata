package com.riintouge.strata.block;

import com.riintouge.strata.image.BlendMode;
import net.minecraft.util.ResourceLocation;

public interface IGenericStoneTileSetInfo
{
    String stoneName();

    StoneStrength stoneStrength();

    ResourceLocation baseTextureLocation();

    BlendMode cobbleOverlayBlendMode();

    float cobbleOverlayOpacity();

    BlendMode brickHighlightBlendMode();

    float brickHighlightOpacity();

    BlendMode brickShadowBlendMode();

    float brickShadowOpacity();
}
