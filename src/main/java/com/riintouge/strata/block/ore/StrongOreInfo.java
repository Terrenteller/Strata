package com.riintouge.strata.block.ore;

import com.riintouge.strata.Strata;
import com.riintouge.strata.block.StoneStrength;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.util.ResourceLocation;

public enum StrongOreInfo implements IOreInfo
{
    BANDED_IRON( "oreIron" ),
    QUARTZ( null ),
    CASSITERITE( "oreTin" ),
    CHROMITE( "oreChromium" ),
    ILMENITE( "oreTitanium" ),
    MAGNETITE( "oreIron" ),
    POLLUCITE( "oreCaesium" ), // This is the IUPAC name. Beware of oreCesium
    SPODUMENE( "oreLithium" ),
    TANTALITE( "oreTantalum" ),
    PITCHBLENDE( "oreUranium" ), // orePitchblende? TODO: Do other mods use this as an ACTUAL ore or uranium itself?
    VANADIUM_MAGNETITE( "oreVanadium" ),
    APATITE( "oreApatite" ), // orePhosphate or orePhosphorus? COG XML uses oreApatite
    KYANITE( null ),
    PERLITE( null ),
    PUMICE( null ),
    PYRITE( null );

    private String oreDictionaryName;

    StrongOreInfo( String oreDictionaryName )
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
        return Material.ROCK;
    }

    @Override
    public SoundType soundType()
    {
        return SoundType.STONE;
    }

    @Override
    public StoneStrength stoneStrength()
    {
        return StoneStrength.STRONG;
    }

    @Override
    public ResourceLocation oreBlockOverlayTextureResource()
    {
        return new ResourceLocation(
            Strata.modid,
            String.format( "blocks/ore/%s/%s" , stoneStrength().toString() , oreName() ) );
    }

    @Override
    public ResourceLocation oreItemTextureResource()
    {
        return oreBlockOverlayTextureResource();
    }
}