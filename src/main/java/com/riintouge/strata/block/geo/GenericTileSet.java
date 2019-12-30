package com.riintouge.strata.block.geo;

import com.riintouge.strata.block.IForgeRegistrable;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
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

public class GenericTileSet implements IForgeRegistrable
{
    protected Map< TileType , IGenericTile > tiles = new HashMap<>();

    public GenericTileSet()
    {
        // Nothing to do
    }

    public void addTile( IGenericTile tile )
    {
        tiles.put( tile.type() , tile );
    }

    // IForgeRegistrable overrides

    @Override
    public void registerBlocks( IForgeRegistry< Block > blockRegistry )
    {
        for( TileType type : tiles.keySet() )
            blockRegistry.register( new GenericBlock( tiles.get( type ) ) );
    }

    @Override
    public void registerItems( IForgeRegistry< Item > itemRegistry )
    {
        Map< TileType , Item > itemMap = new HashMap<>();

        // Create the items...
        for( TileType type : tiles.keySet() )
        {
            IGenericTile tile = tiles.get( type );
            Block block = Block.REGISTRY.getObject( tile.registryName() );
            Item item = new GenericItemBlock( block );

            itemMap.put( type , item );
            itemRegistry.register( item );
        }

        // ...and then the recipes
        for( TileType type : tiles.keySet() )
        {
            IGenericTile tile = tiles.get( type );
            ResourceLocation registryName = tile.registryName();
            Item item = itemMap.get( type );
            ItemStack vanillaItem = null;

            switch( type )
            {
                case CLAY:
                    // TODO: Clay ball to block
                    break;
                case STONE:
                    if( itemMap.containsKey( TileType.STONEBRICK ) )
                    {
                        GameRegistry.addShapedRecipe(
                            new ResourceLocation( registryName.getResourceDomain() , registryName.getResourcePath() + "_brick" ),
                            null,
                            new ItemStack( itemMap.get( TileType.STONEBRICK ) ),
                            "SS" , "SS" , 'S' , item );
                    }

                    vanillaItem = new ItemStack( Blocks.STONE );
                    break;
                case COBBLE:
                    if( itemMap.containsKey( TileType.STONE ) )
                        GameRegistry.addSmelting( item , new ItemStack( itemMap.get( TileType.STONE ) ) , 0.1f ); // Vanilla exp

                    vanillaItem = new ItemStack( Blocks.COBBLESTONE );
                    break;
                case STONEBRICK:
                    vanillaItem = new ItemStack( Blocks.STONEBRICK );
                    break;
                default: {}
            }

            if( tile.vanillaEquivalent() != null )
                vanillaItem = tile.vanillaEquivalent();

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
        for( IGenericTile tile : tiles.values() )
        {
            ModelLoader.setCustomModelResourceLocation(
                Item.REGISTRY.getObject( tile.registryName() ),
                tile.meta(),
                new ModelResourceLocation( tile.registryName() , null ) );
        }
    }

    @Override
    public void stitchTextures( TextureMap textureMap )
    {
        // TODO: Don't generate a texture if a texture already exists, such as from a texture pack.
        // I think ModelDynBucket has example code for this...

        for( IGenericTile tile : tiles.values() )
            tile.stitchTextures( textureMap );
    }
}
