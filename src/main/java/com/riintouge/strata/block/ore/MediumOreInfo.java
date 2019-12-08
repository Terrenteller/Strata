package com.riintouge.strata.block.ore;

import com.riintouge.strata.Strata;
import com.riintouge.strata.block.StoneStrength;
import net.minecraft.util.ResourceLocation;

public enum MediumOreInfo implements IOreInfo
{
    BARITE( "oreBarium" ),
    BASTNASITE( null ),
    CHALCOPYRITE( "oreCopper" ),
    GARNIERITE( "oreNickel" ),
    LEPIDOLITE( "oreLithium" ),
    MAGNESITE( null ),
    PENTLANDITE( null ),
    SCHEELITE( "oreTungsten" ),
    SPHALERITE( "oreZinc" ),
    WOLFRAMITE( "oreTungsten" ),
    ALUNITE( "oreBauxite" ), // oreBauxite because "aluminium" vs. "aluminum"
    CELESTINE( "oreStrontium" ),
    DOLOMITE( "oreMagnesium" ),
    FLUORITE( null ),
    WOLLASTONITE( null ),
    ZEOLITE( null );

    private String oreDictionaryName;

    MediumOreInfo( String oreDictionaryName )
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
        return StoneStrength.MEDIUM;
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
