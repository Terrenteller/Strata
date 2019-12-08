package com.riintouge.strata.block.ore;

import com.riintouge.strata.Strata;
import com.riintouge.strata.block.StoneStrength;
import net.minecraft.util.ResourceLocation;

public enum StrongOreInfo implements IOreInfo
{
    BANDED_IRON( "oreIron" ),
    QUARTZ( null ),
    CASSITERITE( "oreTin" ),
    CHROMITE( "oreChromium" ),
    ILMENITE( "oreTitanium" ),
    MAGNETITE( "oreIron" ),
    POLLUCITE( "oreCaesium" ),
    SPODUMENE( "oreLithium" ),
    TANTALITE( "oreTantalum" ),
    PITCHBLENDE( "oreUranium" ), // TODO: Do other mods use this as an ACTUAL ore or uranium itself?
    VANADIUM_MAGNETITE( "oreVanadium" ),
    APATITE( null ),
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
