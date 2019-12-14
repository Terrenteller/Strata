package com.riintouge.strata.block.ore;

import com.riintouge.strata.Strata;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;

public enum VanillaOreInfo implements IProxyOreInfo
{
    COAL( Blocks.COAL_ORE ),
    DIAMOND( Blocks.DIAMOND_ORE ),
    EMERALD( Blocks.EMERALD_ORE ),
    GOLD( Blocks.GOLD_ORE ),
    IRON( Blocks.IRON_ORE ),
    LAPIS( Blocks.LAPIS_ORE ),
    REDSTONE( Blocks.REDSTONE_ORE ); // TODO: Make glowy-glowy. See BlockRedstoneOre for details.

    private Block vanillaBlock;

    VanillaOreInfo( Block vanillaBlock )
    {
        this.vanillaBlock = vanillaBlock;
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
        return null;
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
