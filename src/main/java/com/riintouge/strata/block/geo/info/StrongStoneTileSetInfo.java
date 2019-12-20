package com.riintouge.strata.block.geo.info;

import com.riintouge.strata.Strata;
import com.riintouge.strata.image.BlendMode;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.util.ResourceLocation;

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
    public ResourceLocation registryName()
    {
        return new ResourceLocation( Strata.modid , this.toString().toLowerCase() );
    }

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
        return 1;
    }

    @Override
    public float hardness()
    {
        return 2.0f;
    }

    @Override
    public ResourceLocation baseTextureLocation()
    {
        return new ResourceLocation(
            Strata.modid,
            String.format( "blocks/stone/strong/%s" , this.toString().toLowerCase() ) );
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
