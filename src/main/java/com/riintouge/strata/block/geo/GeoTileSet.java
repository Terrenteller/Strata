package com.riintouge.strata.block.geo;

import com.riintouge.strata.block.IForgeRegistrable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockStoneSlab;
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

public class GeoTileSet implements IForgeRegistrable
{
    private final String DefaultModelVariant = null;
    // Should we try to get properties from the default block state instead?
    private final String DefaultStairModelVariant = "facing=east,half=bottom,shape=straight";
    private final String DefaultSlabModelVariant = "half=bottom,variant=default";
    private final String DefaultWallModelVariant = "inventory";
    protected Map< TileType , IGeoTileInfo > tiles = new HashMap<>();
    protected Map< TileType , GeoBlockStairs > stairsMap = new HashMap<>();
    protected Map< TileType , GeoBlockSlab > slabMap = new HashMap<>();
    protected Map< TileType , GeoBlockSlab > slabsMap = new HashMap<>();
    protected Map< TileType , GeoBlockWall > wallMap = new HashMap<>();

    public GeoTileSet()
    {
        // Nothing to do
    }

    public void addTile( IGeoTileInfo tile )
    {
        tiles.put( tile.type() , tile );
    }

    public IGeoTileInfo find( TileType type )
    {
        return tiles.getOrDefault( type , null );
    }

    protected void registerVanillaItem( ResourceLocation registryName , Item item , ItemStack vanillaItem )
    {
        if( vanillaItem != null )
        {
            GameRegistry.addShapelessRecipe(
                new ResourceLocation( registryName.getResourceDomain() , registryName.getResourcePath() + "_vanilla" ),
                null,
                vanillaItem,
                Ingredient.fromItem( item ) );
        }
    }

    // IForgeRegistrable overrides

    @Override
    public void registerBlocks( IForgeRegistry< Block > blockRegistry )
    {
        for( TileType type : tiles.keySet() )
        {
            IGeoTileInfo tile = tiles.get( type );
            GeoBlock block = new GeoBlock( tile );
            blockRegistry.register( block );

            if( tile.type().stairType() != null )
            {
                GeoBlockStairs stairs = new GeoBlockStairs( tile , block.getDefaultState() );
                stairsMap.put( type , stairs );
                blockRegistry.register( stairs );
            }

            if( tile.type().slabType() != null && tile.type().slabsType() != null )
            {
                GeoBlockSlab slab = new GeoBlockSlab( tile );
                slabMap.put( type , slab );
                blockRegistry.register( slab );

                GeoBlockSlab slabs = new GeoBlockSlabs( tile , slab );
                slabsMap.put( type , slabs );
                blockRegistry.register( slabs );
            }

            if( tile.type().wallType() != null )
            {
                GeoBlockWall wall = new GeoBlockWall( tile , block );
                wallMap.put( type , wall );
                blockRegistry.register( wall );
            }
        }
    }

    @Override
    public void registerItems( IForgeRegistry< Item > itemRegistry )
    {
        Map< TileType , Item > itemMap = new HashMap<>();

        // Create the items...
        for( TileType type : tiles.keySet() )
        {
            IGeoTileInfo tile = tiles.get( type );
            Block block = Block.REGISTRY.getObject( tile.registryName() );
            Item item = new GeoItemBlock( block );
            itemMap.put( type , item );
            itemRegistry.register( item );

            TileType stairType = tile.type().stairType();
            if( stairType != null )
            {
                GeoItemBlock stairs = new GeoItemBlock( stairsMap.get( type ) );
                itemMap.put( stairType , stairs );
                itemRegistry.register( stairs );
            }

            TileType slabType = tile.type().slabType();
            if( slabType != null && tile.type().slabsType() != null )
            {
                GeoItemBlockSlab slab = new GeoItemBlockSlab( slabMap.get( type ) , slabsMap.get( type ) );
                itemMap.put( slabType , slab );
                itemRegistry.register( slab );
            }

            TileType wallType = tile.type().wallType();
            if( wallType != null )
            {
                GeoItemBlock wall = new GeoItemBlock( wallMap.get( type ) );
                itemMap.put( wallType , wall );
                itemRegistry.register( wall );
            }
        }

        // ...and then the recipes
        for( TileType type : tiles.keySet() )
        {
            IGeoTileInfo tile = tiles.get( type );
            ResourceLocation registryName = tile.registryName();
            Item item = itemMap.get( type );
            ItemStack vanillaItem = null;

            switch( type )
            {
                case CLAY:
                    // TODO: Clay ball to block
                    break;
                case STONE:
                    Item stoneBrickItem = itemMap.getOrDefault( TileType.STONEBRICK , null );
                    if( stoneBrickItem != null )
                    {
                        GameRegistry.addShapedRecipe(
                            new ResourceLocation( registryName.getResourceDomain() , registryName.getResourcePath() + "_brick" ),
                            null,
                            new ItemStack( itemMap.get( TileType.STONEBRICK ) ),
                            "XX",
                            "XX",
                            'X' , item );
                    }

                    vanillaItem = new ItemStack( Blocks.STONE );
                    break;
                case COBBLE:
                    Item stoneItem = itemMap.getOrDefault( TileType.STONE , null );
                    if( stoneItem != null )
                        GameRegistry.addSmelting( item , new ItemStack( stoneItem ) , 0.1f ); // Vanilla exp

                    vanillaItem = new ItemStack( Blocks.COBBLESTONE );
                    break;
                case STONEBRICK:
                    vanillaItem = new ItemStack( Blocks.STONEBRICK );
                    break;
            }

            if( tile.vanillaEquivalent() != null )
                vanillaItem = tile.vanillaEquivalent();
            registerVanillaItem( registryName , item , vanillaItem );

            TileType stairType = tile.type().stairType();
            if( stairType != null )
            {
                Item stairItem = itemMap.getOrDefault( stairType , null );
                GameRegistry.addShapedRecipe(
                    new ResourceLocation( registryName.getResourceDomain() , registryName.getResourcePath() + "_stairs" ),
                    null,
                    new ItemStack( stairItem , 4 ),
                    "X  ",
                    "XX ",
                    "XXX",
                    'X' , item );

                switch( stairType )
                {
                    case COBBLESTAIRS:
                        registerVanillaItem( stairItem.getRegistryName() , stairItem , new ItemStack( Blocks.STONE_STAIRS ) );
                        break;
                    case STONEBRICKSTAIRS:
                        registerVanillaItem( stairItem.getRegistryName() , stairItem , new ItemStack( Blocks.STONE_BRICK_STAIRS ) );
                        break;
                }
            }

            TileType slabType = tile.type().slabType();
            if( slabType != null )
            {
                Item slabItem = itemMap.getOrDefault( slabType , null );
                GameRegistry.addShapedRecipe(
                    new ResourceLocation( registryName.getResourceDomain() , registryName.getResourcePath() + "_slab" ),
                    null,
                    new ItemStack( slabItem , 6 ),
                    "   ",
                    "   ",
                    "XXX",
                    'X' , item );

                switch( slabType )
                {
                    case COBBLESLAB:
                        registerVanillaItem( slabItem.getRegistryName() , slabItem , new ItemStack( Blocks.STONE_SLAB , 1 , BlockStoneSlab.EnumType.COBBLESTONE.getMetadata() ) );
                        break;
                    case STONESLAB:
                        registerVanillaItem( slabItem.getRegistryName() , slabItem , new ItemStack( Blocks.STONE_SLAB , 1 , BlockStoneSlab.EnumType.STONE.getMetadata() ) );
                        break;
                    case STONEBRICKSLAB:
                        registerVanillaItem( slabItem.getRegistryName() , slabItem , new ItemStack( Blocks.STONE_SLAB , 1 , BlockStoneSlab.EnumType.SMOOTHBRICK.getMetadata() ) );
                        break;
                }
            }

            TileType wallType = tile.type().wallType();
            if( wallType != null )
            {
                Item wallItem = itemMap.getOrDefault( wallType , null );
                GameRegistry.addShapedRecipe(
                    new ResourceLocation( registryName.getResourceDomain() , registryName.getResourcePath() + "_wall" ),
                    null,
                    new ItemStack( wallItem , 6 ),
                    "   ",
                    "XXX",
                    "XXX",
                    'X' , item );

                switch( wallType )
                {
                    case COBBLEWALL:
                        registerVanillaItem( wallItem.getRegistryName() , wallItem , new ItemStack( Blocks.COBBLESTONE_WALL ) );
                        break;
                }
            }
        }
    }

    @Override
    public void registerModels( ModelRegistryEvent event )
    {
        for( IGeoTileInfo tile : tiles.values() )
        {
            ModelLoader.setCustomModelResourceLocation(
                Item.REGISTRY.getObject( tile.registryName() ),
                tile.meta(),
                new ModelResourceLocation( tile.registryName() , DefaultModelVariant ) );

            TileType stairType = tile.type().stairType();
            if( stairType != null )
            {
                ResourceLocation stairsRegistryName = stairType.registryName( tile.tileSetName() );
                ModelLoader.setCustomModelResourceLocation(
                    Item.REGISTRY.getObject( stairsRegistryName ),
                    tile.meta(),
                    new ModelResourceLocation( stairsRegistryName , DefaultStairModelVariant ) );
            }

            TileType slabType = tile.type().slabType();
            if( slabType != null && tile.type().slabsType() != null )
            {
                ResourceLocation slabRegistryName = slabType.registryName( tile.tileSetName() );
                ModelLoader.setCustomModelResourceLocation(
                    Item.REGISTRY.getObject( slabRegistryName ),
                    tile.meta(),
                    new ModelResourceLocation( slabRegistryName , DefaultSlabModelVariant ) );
            }

            TileType wallType = tile.type().wallType();
            if( wallType != null )
            {
                ResourceLocation wallRegistryName = wallType.registryName( tile.tileSetName() );
                ModelLoader.setCustomModelResourceLocation(
                    Item.REGISTRY.getObject( wallRegistryName ),
                    tile.meta(),
                    new ModelResourceLocation( wallRegistryName , DefaultWallModelVariant ) );
            }
        }
    }

    @Override
    public void stitchTextures( TextureMap textureMap )
    {
        // TODO: Don't generate a texture if a texture already exists, such as from a texture pack.
        // I think ModelDynBucket has example code for this...

        for( IGeoTileInfo tile : tiles.values() )
            tile.stitchTextures( textureMap );
    }
}
