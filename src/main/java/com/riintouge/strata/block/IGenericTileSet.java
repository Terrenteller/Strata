package com.riintouge.strata.block;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.item.Item;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.registries.IForgeRegistry;

public interface IGenericTileSet
{
    IGenericTileSetInfo tileSetInfo();

    void registerBlocks( IForgeRegistry< Block > blockRegistry );

    void registerItems( IForgeRegistry< Item > itemRegistry );

    void registerModels( ModelRegistryEvent event );

    void stitchTextures( TextureMap textureMap );
}
