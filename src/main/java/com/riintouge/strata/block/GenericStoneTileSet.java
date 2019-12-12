package com.riintouge.strata.block;

import com.riintouge.strata.Strata;
import com.riintouge.strata.image.LayeredTexture;
import com.riintouge.strata.image.LayeredTextureLayer;
import com.riintouge.strata.item.GenericStoneBlockItem;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.HashMap;
import java.util.Map;

// TODO: Add an interface
public class GenericStoneTileSet
{
    // TODO: Add getters
    public IGenericStoneTileSetInfo tileSetInfo;
    public Map< StoneBlockType , GenericBlockItemPair > tiles;

    public GenericStoneTileSet( IGenericStoneTileSetInfo tileSetInfo )
    {
        this.tileSetInfo = tileSetInfo;

        GenericStoneBlock stoneBlock = new GenericStoneBlock( tileSetInfo , StoneBlockType.STONE );
        GenericBlockItemPair stone = new GenericBlockItemPair( stoneBlock , new GenericStoneBlockItem( stoneBlock ) );

        GenericStoneBlock cobbleBlock = new GenericStoneBlock( tileSetInfo , StoneBlockType.COBBLE );
        GenericBlockItemPair cobble = new GenericBlockItemPair( cobbleBlock , new GenericStoneBlockItem( cobbleBlock ) );

        GenericStoneBlock brickBlock = new GenericStoneBlock( tileSetInfo , StoneBlockType.BRICK );
        GenericBlockItemPair brick = new GenericBlockItemPair( brickBlock , new GenericStoneBlockItem( brickBlock ) );

        tiles = new HashMap<>();
        tiles.put( StoneBlockType.STONE , stone );
        tiles.put( StoneBlockType.COBBLE , cobble );
        tiles.put( StoneBlockType.BRICK , brick );
    }

    public void registerBlocks( IForgeRegistry< Block > blockRegistry )
    {
        for( GenericBlockItemPair pair : tiles.values() )
            blockRegistry.register( pair.getBlock() );
    }

    public void registerItems( IForgeRegistry< Item > itemRegistry )
    {
        for( GenericBlockItemPair pair : tiles.values() )
            itemRegistry.register( pair.getItem() );
    }

    public void registerModels( ModelRegistryEvent event )
    {
        for( GenericBlockItemPair pair : tiles.values() )
        {
            ModelLoader.setCustomModelResourceLocation(
                pair.getItem(),
                0,
                new ModelResourceLocation( pair.getBlock().getRegistryName() , null ) );
        }
    }

    public void stitchTextures( TextureMap textureMap )
    {
        // TODO: Don't generate a texture if a texture already exists, such as from a texture pack.
        // I think ModelDynBucket has example code for this...

        ResourceLocation baseTextureLocation = tileSetInfo.baseTextureLocation();

        {
            GenericBlockItemPair stone = tiles.getOrDefault( StoneBlockType.STONE , null );

            LayeredTextureLayer baseLayer = new LayeredTextureLayer( baseTextureLocation );
            TextureAtlasSprite stoneTexture = new LayeredTexture(
                stone.getBlock().getRegistryName(),
                new LayeredTextureLayer[] { baseLayer } );

            textureMap.setTextureEntry( stoneTexture );
        }

        {
            GenericBlockItemPair cobble = tiles.getOrDefault( StoneBlockType.COBBLE , null );

            LayeredTextureLayer cobbleLayer = new LayeredTextureLayer(
                new ResourceLocation( Strata.modid , "overlays/cobble" ),
                tileSetInfo.cobbleOverlayBlendMode(),
                tileSetInfo.cobbleOverlayOpacity() );
            LayeredTextureLayer baseLayer = new LayeredTextureLayer( baseTextureLocation );
            TextureAtlasSprite cobbleTexture = new LayeredTexture(
                cobble.getBlock().getRegistryName(),
                new LayeredTextureLayer[] { cobbleLayer , baseLayer } );

            textureMap.setTextureEntry( cobbleTexture );
        }

        {
            GenericBlockItemPair brick = tiles.getOrDefault( StoneBlockType.BRICK , null );

            LayeredTextureLayer brickHighlightLayer = new LayeredTextureLayer(
                new ResourceLocation( Strata.modid , "overlays/brick_highlight" ),
                tileSetInfo.brickHighlightBlendMode(),
                tileSetInfo.brickHighlightOpacity() );
            LayeredTextureLayer brickShadowLayer = new LayeredTextureLayer(
                new ResourceLocation( Strata.modid , "overlays/brick_shadow" ),
                tileSetInfo.brickShadowBlendMode(),
                tileSetInfo.brickShadowOpacity() );
            LayeredTextureLayer stoneLayer = new LayeredTextureLayer( baseTextureLocation );
            TextureAtlasSprite brickTexture = new LayeredTexture(
                brick.getBlock().getRegistryName(),
                new LayeredTextureLayer[] { brickHighlightLayer , brickShadowLayer , stoneLayer } );

            textureMap.setTextureEntry( brickTexture );
        }
    }
}