package com.riintouge.strata.block.ore.info;

import com.riintouge.strata.Strata;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.util.ResourceLocation;

public enum SandOreInfo implements IOreInfo
{
    BASALTIC_MINERAL_SAND( null ),
    CASSITERITE_SAND( null ),
    GARNET_SAND( null ),
    GRANITIC_MINERAL_SAND( null ),
    QUARTZ_SAND( null ),
    VOLCANIC_ASH( null ),
    GLAUCONITE( null ),
    DIATOMITE( null ),
    SULFUR( null ),
    OIL_SAND( null );

    private String oreDictionaryName;

    SandOreInfo( String oreDictionaryName )
    {
        this.oreDictionaryName = oreDictionaryName;
    }

    // IOreInfo overrides

    @Override
    public String oreName()
    {
        return this.toString().toLowerCase();
    }

    @Override
    public String oreDictionaryName()
    {
        return oreDictionaryName;
    }

    @Override
    public Material material()
    {
        return Material.SAND;
    }

    @Override
    public SoundType soundType()
    {
        return SoundType.SAND;
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
        return 1.0f; // 2x sand
    }

    @Override
    public ResourceLocation oreBlockOverlayTextureResource()
    {
        return new ResourceLocation(
            Strata.modid,
            String.format( "blocks/ore/sand/%s" , oreName() ) );
    }

    @Override
    public ResourceLocation oreItemTextureResource()
    {
        return oreBlockOverlayTextureResource();
    }
}
