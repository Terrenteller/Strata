package com.riintouge.strata.block.ore.info;

import com.riintouge.strata.Strata;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;

public enum VanillaOreInfo implements IProxyOreInfo
{
    COAL( Blocks.COAL_ORE , "oreCoal" ),
    DIAMOND( Blocks.DIAMOND_ORE , "oreDiamond" ),
    EMERALD( Blocks.EMERALD_ORE , "oreEmerald" ),
    GOLD( Blocks.GOLD_ORE , "oreGold" ),
    IRON( Blocks.IRON_ORE , "oreIron" ),
    LAPIS( Blocks.LAPIS_ORE , "oreLapis" ),
    REDSTONE( Blocks.REDSTONE_ORE , "oreRedstone" ); // TODO: Make glowy-glowy. See BlockRedstoneOre for details.

    private Block vanillaBlock;
    private String oreDictionaryName;

    VanillaOreInfo( Block vanillaBlock , String oreDictionaryName )
    {
        this.vanillaBlock = vanillaBlock;
        this.oreDictionaryName = oreDictionaryName;
    }

    // IProxyOreInfo overrides

    @Override
    public Block getProxyBlock()
    {
        return vanillaBlock;
    }

    // IOreInfo overrides

    @Override
    public String oreName()
    {
        return toString().toLowerCase();
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
        return vanillaBlock.getHarvestLevel( vanillaBlock.getDefaultState() );
    }

    @Override
    public float hardness()
    {
        return 3.0f; // Vanilla ore
    }

    @Override
    public float explosionResistance()
    {
        return 5.0f; // Vanilla ore
    }

    @Override
    public ResourceLocation oreBlockOverlayTextureResource()
    {
        return new ResourceLocation(
            Strata.modid,
            String.format( "blocks/ore/vanilla/%s" , oreName() ) );
    }

    @Override
    public ResourceLocation oreItemTextureResource()
    {
        return oreBlockOverlayTextureResource();
    }
}
