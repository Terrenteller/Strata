package com.riintouge.strata.block.geo.tileset;

import com.riintouge.strata.block.geo.GenericBlockItemPair;
import com.riintouge.strata.block.geo.info.IGenericTileSetInfo;
import com.riintouge.strata.image.LayeredTexture;
import com.riintouge.strata.image.LayeredTextureLayer;
import com.riintouge.strata.block.geo.GenericItemBlock;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.registries.IForgeRegistry;

public class GenericGroundTileSet implements IGenericTileSet
{
    // TODO: Add getters
    private IGenericTileSetInfo tileSetInfo;
    public GenericBlockItemPair blockItemPair;

    public GenericGroundTileSet( IGenericTileSetInfo tileSetInfo )
    {
        this.tileSetInfo = tileSetInfo;

        Block groundBlock = new GenericGroundBlock( tileSetInfo );
        blockItemPair = new GenericBlockItemPair( groundBlock , new GenericItemBlock( groundBlock ) );
    }

    // IGenericTileSet overrides

    @Override
    public IGenericTileSetInfo tileSetInfo()
    {
        return tileSetInfo;
    }

    @Override
    public void registerBlocks( IForgeRegistry< Block > blockRegistry )
    {
        blockRegistry.register( blockItemPair.getBlock() );
    }

    @Override
    public void registerItems( IForgeRegistry< Item > itemRegistry )
    {
        itemRegistry.register( blockItemPair.getItem() );
    }

    @Override
    public void registerModels( ModelRegistryEvent event )
    {
        ModelLoader.setCustomModelResourceLocation(
            blockItemPair.getItem(),
            0,
            new ModelResourceLocation( blockItemPair.getBlock().getRegistryName() , null ) );
    }

    @Override
    public void stitchTextures( TextureMap textureMap )
    {
        // TODO: Don't generate a texture if a texture already exists, such as from a texture pack.
        // I think ModelDynBucket has example code for this...

        ResourceLocation baseTextureLocation = tileSetInfo.baseTextureLocation();

        {
            LayeredTextureLayer baseLayer = new LayeredTextureLayer( baseTextureLocation );
            TextureAtlasSprite stoneTexture = new LayeredTexture(
                blockItemPair.getBlock().getRegistryName(),
                new LayeredTextureLayer[] { baseLayer } );

            textureMap.setTextureEntry( stoneTexture );
        }
    }
}
