package com.riintouge.strata.block.ore;

import com.riintouge.strata.Strata;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.util.ResourceLocation;

public enum WeakOreInfo implements IOreInfo
{
    BORAX( null ),
    CINNABAR( "oreCinnabar" ),
    GALENA( "oreLead" ),
    MOLYBDENITE( "oreMolybdenum" ), // This was referred to oreMolydenum in the original COG XML
    PYROLUSITE( "oreManganese" ),
    SALT( "oreSalt" ),
    STIBNITE( null ),
    CHRYSOTILE( null ),
    SALITRE( null ),
    REALGAR( "oreArsenic" ),
    GRAPHITE( null ),
    GYPSUM( null ),
    MIRABILITE( null ),
    MICA( null ),
    SOAPSTONE( null ), // oreMagnesium? oreTalc?
    TRONA( null ),
    BOG_IRON( "oreIron" );

    private String oreDictionaryName;

    WeakOreInfo( String oreDictionaryName )
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
    public ResourceLocation oreBlockOverlayTextureResource()
    {
        return new ResourceLocation(
            Strata.modid,
            String.format( "blocks/ore/weak/%s" , oreName() ) );
    }

    @Override
    public ResourceLocation oreItemTextureResource()
    {
        return oreBlockOverlayTextureResource();
    }
}
