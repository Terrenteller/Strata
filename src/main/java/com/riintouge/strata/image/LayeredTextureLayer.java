package com.riintouge.strata.image;

import net.minecraft.util.ResourceLocation;

public class LayeredTextureLayer
{
    public final ResourceLocation textureResource;
    public final BlendMode blendMode;
    public final float opacity;

    public LayeredTextureLayer( ResourceLocation textureResource )
    {
        this( textureResource , BlendMode.NORMAL , 100.0f );
    }

    public LayeredTextureLayer( ResourceLocation textureResource , BlendMode blendMode )
    {
        this( textureResource , blendMode , 100.0f );
    }

    public LayeredTextureLayer( ResourceLocation textureResource , float opacity )
    {
        this( textureResource , BlendMode.NORMAL , opacity );
    }

    public LayeredTextureLayer( ResourceLocation textureResource , BlendMode blendMode , float opacity )
    {
        this.textureResource = textureResource;
        this.blendMode = blendMode;
        this.opacity = opacity;
    }
}
