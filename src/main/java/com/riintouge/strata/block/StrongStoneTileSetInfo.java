package com.riintouge.strata.block;

import com.riintouge.strata.image.BlendMode;

public enum StrongStoneTileSetInfo implements IGenericStoneTileSetInfo
{
    ANDESITE      (  95.0f ,  46.0f ,  34.0f ),
    BASALT        (  82.0f ,  10.0f ,  27.0f ),
    GNEISS        ( 100.0f ,  19.0f ,  26.0f ),
    GRANITE       (  87.0f ,  21.0f ,  28.0f ),
    GREENSCHIST   ( 100.0f ,  10.0f ,  37.0f ),
    MARBLE        (  71.0f ,  59.0f ,  36.0f ),
    PEGMATITE     (  78.0f ,  58.0f ,  29.0f ),
    RHYOLITE      (  78.0f ,  59.0f ,  34.0f ),
    SANDSTONE     (  82.0f ,  39.0f ,  29.0f ),
    RED_SANDSTONE (  58.0f ,  13.0f ,  27.0f );

    private float cobbleOverlayOpacity;
    private float brickHighlightOpacity;
    private float brickShadowOpacity;

    StrongStoneTileSetInfo(
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
        return StoneStrength.STRONG;
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
