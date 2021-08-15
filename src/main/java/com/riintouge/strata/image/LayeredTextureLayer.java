package com.riintouge.strata.image;

import net.minecraft.util.ResourceLocation;

public class LayeredTextureLayer
{
    public ResourceLocation resource;
    public BlendMode blendMode;
    public float opacity;

    public LayeredTextureLayer( ResourceLocation resource )
    {
        this( resource , BlendMode.NORMAL, 100.0f );
    }

    public LayeredTextureLayer( ResourceLocation resource , BlendMode blendMode )
    {
        this( resource , blendMode , 100.0f );
    }

    public LayeredTextureLayer( ResourceLocation resource , float opacity )
    {
        this( resource , BlendMode.NORMAL , opacity );
    }

    public LayeredTextureLayer( ResourceLocation resource , BlendMode blendMode , float opacity )
    {
        this.resource = resource;
        this.blendMode = blendMode;
        this.opacity = opacity;
    }
}
