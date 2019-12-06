package com.riintouge.strata.block;

import com.riintouge.strata.image.BlendMode;

public enum WeakStoneTileSetInfo implements IGenericStoneTileSetInfo
{
    BRECCIA      (  86.0f ,  21.0f ,  30.0f ),
    CLAYSTONE    (  85.0f ,  35.0f ,  23.0f ),
    CARBONATITE  (  85.0f ,  22.0f ,  23.0f ),
    CONGLOMERATE (  98.0f ,  36.0f ,  39.0f ),
    MUDSTONE     (  91.0f ,  23.0f ,  23.0f );

    private float cobbleOverlayOpacity;
    private float brickHighlightOpacity;
    private float brickShadowOpacity;

    WeakStoneTileSetInfo(
        float cobbleOverlayOpacity,
        float brickHighlightOpacity,
        float brickShadowOpacity )
    {
        this.cobbleOverlayOpacity = cobbleOverlayOpacity;
        this.brickHighlightOpacity = brickHighlightOpacity;
        this.brickShadowOpacity = brickShadowOpacity;
    }

    @Override
    public String stoneName()
    {
        return this.toString().toLowerCase();
    }

    @Override
    public StoneStrength stoneStrength()
    {
        return StoneStrength.WEAK;
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
