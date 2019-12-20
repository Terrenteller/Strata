package com.riintouge.strata.block.geo.info;

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
    public float hardness()
    {
        return 0.6f; // Vanilla clay
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
            String.format( "blocks/clay/%s" , this.toString().toLowerCase() ) );
    }
}
