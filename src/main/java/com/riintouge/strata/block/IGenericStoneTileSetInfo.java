package com.riintouge.strata.block;

import com.riintouge.strata.image.BlendMode;

public interface IGenericStoneTileSetInfo
{
    String stoneName();

    StoneStrength stoneStrength();

    BlendMode cobbleOverlayBlendMode();

    float cobbleOverlayOpacity();

    BlendMode brickHighlightBlendMode();

    float brickHighlightOpacity();

    BlendMode brickShadowBlendMode();

    float brickShadowOpacity();
}
