package com.riintouge.strata.block;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.registries.IForgeRegistry;

public interface IForgeRegistrable
{
    default void registerBlocks( IForgeRegistry< Block > blockRegistry )
    {
        // Dummy
    }

    default void registerItems( IForgeRegistry< Item > itemRegistry )
    {
        // Dummy
    }

    default void registerRecipes( IForgeRegistry< IRecipe > recipeRegistry )
    {
        // Dummy
    }

    default void registerModels( ModelRegistryEvent event )
    {
        // Dummy
    }

    @SideOnly( Side.CLIENT )
    default void stitchTextures( TextureMap textureMap )
    {
        // Dummy
    }
}
