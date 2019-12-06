package com.riintouge.strata.block;

import com.riintouge.strata.image.BlendMode;

public enum WeakStoneTileSetInfo implements IGenericStoneTileSetInfo
{
    BRECCIA( 86.0f , 39.0f , 86.0f ), // Original BlendMode.NORMAL was 82
    CLAYSTONE( 85.0f , 60.0f , 53.0f ), // Original BlendMode.NORMAL was 84
    CARBONATITE( 85.0f , 32.0f , 60.0f ),
    CONGLOMERATE( 98.0f , 57.0f , 100.0f ),
    MUDSTONE( 91.0f , 38.0f , 76.0f );

    private float cobbleOverlayOpacity;
    private float brickLightenOverlayOpacity;
    private float brickDarkenOverlayOpacity;

    WeakStoneTileSetInfo(
        float cobbleOverlayOpacity,
        float brickLightenOverlayOpacity,
        float brickDarkenOverlayOpacity )
    {
        this.cobbleOverlayOpacity = cobbleOverlayOpacity;
        this.brickLightenOverlayOpacity = brickLightenOverlayOpacity;
        this.brickDarkenOverlayOpacity = brickDarkenOverlayOpacity;
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
    public float brickHighlightOverlayOpacity()
    {
        return brickLightenOverlayOpacity;
    }

    @Override
    public BlendMode brickShadowBlendMode()
    {
        return BlendMode.DARKEN;
    }

    @Override
    public float brickShadowOverlayOpacity()
    {
        return brickDarkenOverlayOpacity;
    }
}
