package com.riintouge.strata.block;

import com.riintouge.strata.Strata;
import net.minecraft.util.ResourceLocation;

public enum ClayTileSetInfo implements IGenericTileSetInfo
{
    LATERITE;

    ClayTileSetInfo()
    {
    }

    // IGenericTileSetInfo overrides

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
    public ResourceLocation baseTextureLocation()
    {
        return new ResourceLocation(
            Strata.modid,
            String.format( "blocks/clay/%s" , stoneName() ) );
    }
}
