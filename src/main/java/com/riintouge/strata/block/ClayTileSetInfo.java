package com.riintouge.strata.block;

import com.riintouge.strata.Strata;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.util.ResourceLocation;

public enum ClayTileSetInfo implements IGenericTileSetInfo
{
    LATERITE;

    ClayTileSetInfo()
    {
    }

    // IGenericTileSetInfo overrides

    @Override
    public Material material()
    {
        return Material.CLAY;
    }

    @Override
    public SoundType soundType()
    {
        return SoundType.GROUND;
    }

    @Override
    public String harvestTool()
    {
        return "shovel";
    }

    @Override
    public int harvestLevel()
    {
        return 0;
    }

    @Override
    public String stoneName()
    {
        return this.toString().toLowerCase();
    }

    @Override
    public ResourceLocation baseTextureLocation()
    {
        return new ResourceLocation(
            Strata.modid,
            String.format( "blocks/clay/%s" , stoneName() ) );
    }
}
