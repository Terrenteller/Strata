package com.riintouge.strata.block;

import com.riintouge.strata.image.BlendMode;

public interface IGenericStoneTileSetInfo extends IGenericTileSetInfo
{
    BlendMode cobbleOverlayBlendMode();

    float cobbleOverlayOpacity();

    BlendMode brickHighlightBlendMode();

    float brickHighlightOpacity();

    BlendMode brickShadowBlendMode();

    float brickShadowOpacity();
}
