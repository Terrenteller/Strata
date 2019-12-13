package com.riintouge.strata.block.ore;

import com.riintouge.strata.Strata;
import com.riintouge.strata.block.StoneStrength;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.util.ResourceLocation;

public enum ClayOreInfo implements IOreInfo
{
    BAUXITE( "oreBauxite" ),
    SODIUM_BENTONITE( null ),
    CALCIUM_BENTONITE( null ),
    KAOLINITE( null ),
    BROWN_LIMONITE( "oreIron" ),
    YELLOW_LIMONITE( "oreIron" ),
    VERMICULITE( null );

    private String oreDictionaryName;

    ClayOreInfo( String oreDictionaryName )
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
        return Material.CLAY;
    }

    @Override
    public SoundType soundType()
    {
        return SoundType.GROUND;
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
            Strata.modid ,
            String.format( "blocks/ore/clay/%s" , oreName() ) );
    }

    @Override
    public ResourceLocation oreItemTextureResource()
    {
        return oreBlockOverlayTextureResource();
    }
}
