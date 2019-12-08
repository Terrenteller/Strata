package com.riintouge.strata.block;

import com.riintouge.strata.Strata;
import com.riintouge.strata.image.BlendMode;
import net.minecraft.util.ResourceLocation;

public interface IGenericStoneTileSetInfo
{
    String stoneName();

    StoneStrength stoneStrength();

    default ResourceLocation baseTextureLocation()
    {
        return new ResourceLocation(
            Strata.modid,
            String.format( "blocks/stone/%s/%s" , stoneStrength().toString() , this.toString() ) );
    }

    BlendMode cobbleOverlayBlendMode();

    float cobbleOverlayOpacity();

    BlendMode brickHighlightBlendMode();

    float brickHighlightOpacity();

    BlendMode brickShadowBlendMode();

    float brickShadowOpacity();
}
