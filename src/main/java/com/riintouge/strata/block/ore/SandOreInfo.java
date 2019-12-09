package com.riintouge.strata.block.ore;

import com.riintouge.strata.Strata;
import com.riintouge.strata.block.StoneStrength;
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
    SULFUR( null );

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
    public StoneStrength stoneStrength()
    {
        return StoneStrength.WEAK;
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
