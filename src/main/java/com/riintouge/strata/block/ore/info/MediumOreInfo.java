package com.riintouge.strata.block.ore.info;

import com.riintouge.strata.Strata;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.util.ResourceLocation;

public enum MediumOreInfo implements IOreInfo
{
    BARITE( "oreBarium" ),
    BASTNASITE( null ),
    CHALCOPYRITE( "oreCopper" ), // COG XML mentions malachite
    GARNIERITE( "oreNickel" ),
    LEPIDOLITE( "oreLithium" ),
    MAGNESITE( "oreMagnesium" ),
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
    public String harvestTool()
    {
        return "pickaxe";
    }

    @Override
    public int harvestLevel()
    {
        return 0;
    }

    @Override
    public float hardness()
    {
        return 3.0f; // 2x medium stone
    }

    @Override
    public ResourceLocation oreBlockOverlayTextureResource()
    {
        return new ResourceLocation(
            Strata.modid,
            String.format( "blocks/ore/medium/%s" , this.toString().toLowerCase() ) );
    }

    @Override
    public ResourceLocation oreItemTextureResource()
    {
        return oreBlockOverlayTextureResource();
    }
}
