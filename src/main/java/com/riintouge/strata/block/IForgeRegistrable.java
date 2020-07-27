package com.riintouge.strata.block;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.item.Item;
import net.minecraftforge.client.event.ModelRegistryEvent;
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

    default void registerModels( ModelRegistryEvent event )
    {
        // Dummy
    }

    default void stitchTextures( TextureMap textureMap )
    {
        // Dummy
    }
}