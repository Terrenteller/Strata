package com.riintouge.strata.block.geo.info;

import com.riintouge.strata.Strata;
import com.riintouge.strata.image.BlendMode;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.util.ResourceLocation;

public enum MediumStoneTileSetInfo implements IGenericStoneTileSetInfo
{
    LIMESTONE    (  94.0f ,  43.0f ,  23.0f ),
    SCHIST       (  97.0f ,  46.0f ,  40.0f ),
    SERPENTINITE ( 100.0f ,  19.0f ,  36.0f ),
    SLATE        (  94.0f ,  10.0f ,  28.0f ),
    SKARN        (  95.0f ,  23.0f ,  28.0f ),
    CHALK        (  88.0f , 100.0f ,   8.0f );

    private float cobbleOverlayOpacity;
    private float brickHighlightOpacity;
    private float brickShadowOpacity;

    MediumStoneTileSetInfo(
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
    public Material material()
    {
        return Material.ROCK;
    }

    @Override
    public SoundType soundType()
    {
        return SoundType.STONE;
    }

    @Override
    public String harvestTool()
    {
        return "pickaxe";
    }

    @Override
    public int harvestLevel()
    {
        return 0;
    }

    @Override
    public float hardness()
    {
        return 1.5f;
    }

    @Override
    public ResourceLocation registryName()
    {
        return new ResourceLocation( Strata.modid , this.toString().toLowerCase() );
    }

    @Override
    public ResourceLocation baseTextureLocation()
    {
        return new ResourceLocation(
            Strata.modid,
            String.format( "blocks/stone/medium/%s" , this.toString().toLowerCase() ) );
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
