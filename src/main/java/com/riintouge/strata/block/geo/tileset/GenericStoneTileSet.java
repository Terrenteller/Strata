package com.riintouge.strata.block.geo.tileset;

import com.riintouge.strata.Strata;
import com.riintouge.strata.block.geo.GenericBlockItemPair;
import com.riintouge.strata.block.geo.TileType;
import com.riintouge.strata.block.geo.info.IGenericStoneTileSetInfo;
import com.riintouge.strata.block.geo.info.IGenericTileSetInfo;
import com.riintouge.strata.image.LayeredTexture;
import com.riintouge.strata.image.LayeredTextureLayer;
import com.riintouge.strata.block.geo.GenericItemBlock;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.HashMap;
import java.util.Map;

import static com.riintouge.strata.block.geo.info.StrongStoneTileSetInfo.ANDESITE;
import static com.riintouge.strata.block.geo.info.StrongStoneTileSetInfo.GRANITE;
import static com.riintouge.strata.block.geo.info.VeryStrongStoneTileSetInfo.DIORITE;

public class GenericStoneTileSet implements IGenericTileSet
{
    // TODO: Add getters
    private IGenericStoneTileSetInfo tileSetInfo;
    public Map< TileType, GenericBlockItemPair > tiles = new HashMap<>();

    public GenericStoneTileSet( IGenericStoneTileSetInfo tileSetInfo )
    {
        this.tileSetInfo = tileSetInfo;

        GenericStoneBlock stoneBlock = new GenericStoneBlock( tileSetInfo , TileType.STONE );
        tiles.put( TileType.STONE , new GenericBlockItemPair( stoneBlock , new GenericItemBlock( stoneBlock ) ) );

        GenericStoneBlock cobbleBlock = new GenericStoneBlock( tileSetInfo , TileType.COBBLE );
        tiles.put( TileType.COBBLE , new GenericBlockItemPair( cobbleBlock , new GenericItemBlock( cobbleBlock ) ) );

        GenericStoneBlock brickBlock = new GenericStoneBlock( tileSetInfo , TileType.BRICK );
        tiles.put( TileType.BRICK , new GenericBlockItemPair( brickBlock , new GenericItemBlock( brickBlock ) ) );
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
        for( GenericBlockItemPair pair : tiles.values() )
            blockRegistry.register( pair.getBlock() );
    }

    @Override
    public void registerItems( IForgeRegistry< Item > itemRegistry )
    {
        for( TileType type : tiles.keySet() )
        {
            Item item = tiles.get( type ).getItem();
            itemRegistry.register( item );
            ResourceLocation registryName = item.getRegistryName();
            ItemStack vanillaItem = null;

            switch( type )
            {
                case STONE:
                    // TODO: This is pretty terrible
                    if( tileSetInfo.equals( GRANITE ) )
                        vanillaItem = new ItemStack( Blocks.STONE , 1 , 1 );
                    else if( tileSetInfo.equals( DIORITE ) )
                        vanillaItem = new ItemStack( Blocks.STONE , 1 , 3 );
                    else if( tileSetInfo.equals( ANDESITE ) )
                        vanillaItem = new ItemStack( Blocks.STONE , 1 , 5 );
                    else
                        vanillaItem = new ItemStack( Blocks.STONE );

                    GameRegistry.addShapedRecipe(
                        new ResourceLocation( registryName.getResourceDomain() , registryName.getResourcePath() + "_brick" ),
                        null,
                        new ItemStack( tiles.get( TileType.BRICK ).getItem() ),
                        "SS" , "SS" , 'S' , item );

                    break;
                case COBBLE:
                    vanillaItem = new ItemStack( Blocks.COBBLESTONE );
                    GameRegistry.addSmelting( item , new ItemStack( tiles.get( TileType.STONE ).getItem() ) , 0.1f ); // Vanilla exp
                    break;
                case BRICK:
                    vanillaItem = new ItemStack( Blocks.STONEBRICK );
                    break;
                default: {}
            }

            if( vanillaItem != null )
            {
                GameRegistry.addShapelessRecipe(
                    new ResourceLocation( registryName.getResourceDomain() , registryName.getResourcePath() + "_vanilla" ),
                    null,
                    vanillaItem,
                    Ingredient.fromItem( item ) );
            }
        }
    }

    @Override
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

    @Override
    public void stitchTextures( TextureMap textureMap )
    {
        // TODO: Don't generate a texture if a texture already exists, such as from a texture pack.
        // I think ModelDynBucket has example code for this...

        ResourceLocation baseTextureLocation = tileSetInfo.baseTextureLocation();

        {
            GenericBlockItemPair stone = tiles.getOrDefault( TileType.STONE , null );

            LayeredTextureLayer baseLayer = new LayeredTextureLayer( baseTextureLocation );
            TextureAtlasSprite stoneTexture = new LayeredTexture(
                stone.getBlock().getRegistryName(),
                new LayeredTextureLayer[] { baseLayer } );

            textureMap.setTextureEntry( stoneTexture );
        }

        {
            GenericBlockItemPair cobble = tiles.getOrDefault( TileType.COBBLE , null );

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
            GenericBlockItemPair brick = tiles.getOrDefault( TileType.BRICK , null );

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
