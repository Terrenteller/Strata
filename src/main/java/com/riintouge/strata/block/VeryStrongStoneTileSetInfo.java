package com.riintouge.strata.block;

import com.riintouge.strata.image.BlendMode;

public enum VeryStrongStoneTileSetInfo implements IGenericStoneTileSetInfo
{
    DIORITE    ( 100.0f ,  36.0f ,  41.0f ),
    GABBRO     (  96.0f ,  26.0f ,  36.0f ),
    HORNFELS   (  96.0f ,  25.0f ,  39.0f ),
    PERIDOTITE (  96.0f ,  21.0f ,  31.0f ),
    QUARTZITE  (  95.0f ,  27.0f ,  34.0f );

    private float cobbleOverlayOpacity;
    private float brickHighlightOpacity;
    private float brickShadowOpacity;

    VeryStrongStoneTileSetInfo(
        float cobbleOverlayOpacity,
        float brickHighlightOpacity,
        float brickShadowOpacity )
    {
        this.cobbleOverlayOpacity = cobbleOverlayOpacity;
        this.brickHighlightOpacity = brickHighlightOpacity;
        this.brickShadowOpacity = brickShadowOpacity;
    }

    // IGenericStoneTileSetInfo overrides

    @Override
    public String stoneName()
    {
        return this.toString().toLowerCase();
    }

    @Override
    public StoneStrength stoneStrength()
    {
        return StoneStrength.VERY_STRONG;
    }

    @Override
    public BlendMode cobbleOverlayBlendMode()
    {
        return BlendMode.NORMAL;
    }

    @Override
    public float cobbleOverlayOpacity()
    {
        return cobbleOverlayOpacity;
    }

    @Override
    public BlendMode brickHighlightBlendMode()
    {
        return BlendMode.LIGHTEN;
    }

    @Override
    public float brickHighlightOpacity()
    {
        return brickHighlightOpacity;
    }

    @Override
    public BlendMode brickShadowBlendMode()
    {
        return BlendMode.DARKEN;
    }

    @Override
    public float brickShadowOpacity()
    {
        return brickShadowOpacity;
    }
}
