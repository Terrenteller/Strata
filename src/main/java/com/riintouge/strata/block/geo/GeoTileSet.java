package com.riintouge.strata.block.geo;

import com.riintouge.strata.block.IForgeRegistrable;
import com.riintouge.strata.block.RecipeReplicator;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
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
    protected Map< TileType , Item > itemMap = new HashMap<>();
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

            TileType stairType = tile.type().stairType();
            if( stairType != null && stairType.parentType == tile.type() )
            {
                GeoBlockStairs stairs = new GeoBlockStairs( tile , block.getDefaultState() );
                stairsMap.put( type , stairs );
                blockRegistry.register( stairs );
            }

            TileType slabType = tile.type().slabType();
            if( slabType != null && tile.type().slabsType() != null && slabType.parentType == tile.type() )
            {
                GeoBlockSlab slab = new GeoBlockSlab( tile );
                slabMap.put( type , slab );
                blockRegistry.register( slab );

                GeoBlockSlab slabs = new GeoBlockSlabs( tile , slab );
                slabsMap.put( type , slabs );
                blockRegistry.register( slabs );
            }

            TileType wallType = tile.type().wallType();
            if( wallType != null && wallType.parentType == tile.type() )
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
        for( TileType type : tiles.keySet() )
        {
            IGeoTileInfo tile = tiles.get( type );
            Block block = Block.REGISTRY.getObject( tile.registryName() );
            Item item = new GeoItemBlock( tile , block );
            itemMap.put( type , item );
            itemRegistry.register( item );

            TileType stairType = tile.type().stairType();
            if( stairType != null && stairType.parentType == tile.type() )
            {
                GeoItemBlock stairs = new GeoItemBlock( tile , stairsMap.get( type ) );
                itemMap.put( stairType , stairs );
                itemRegistry.register( stairs );
            }

            TileType slabType = tile.type().slabType();
            if( slabType != null && tile.type().slabsType() != null && slabType.parentType == tile.type() )
            {
                GeoItemBlockSlab slab = new GeoItemBlockSlab( slabMap.get( type ) , slabsMap.get( type ) );
                itemMap.put( slabType , slab );
                itemRegistry.register( slab );
            }

            TileType wallType = tile.type().wallType();
            if( wallType != null && wallType.parentType == tile.type() )
            {
                GeoItemBlock wall = new GeoItemBlock( tile , wallMap.get( type ) );
                itemMap.put( wallType , wall );
                itemRegistry.register( wall );
            }
        }
    }

    @Override
    public void registerRecipes( IForgeRegistry< IRecipe > recipeRegistry )
    {
        for( TileType type : tiles.keySet() )
        {
            IGeoTileInfo tile = tiles.get( type );
            ResourceLocation registryName = tile.registryName();
            Item item = itemMap.get( type );

            ItemStack vanillaItemStack = type.vanillaItemStack();
            if( vanillaItemStack != null )
                RecipeReplicator.INSTANCE.register( vanillaItemStack , new ItemStack( item ) );

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
                            new ResourceLocation( registryName.getResourceDomain() , registryName.getResourcePath() + "_stonebrick" ),
                            null,
                            new ItemStack( stoneBrickItem ),
                            "XX",
                            "XX",
                            'X' , item );
                    }
                    break;
                case STONEBRICK:
                    Item stoneBrickMossyItem = itemMap.getOrDefault( TileType.STONEBRICKMOSSY , null );
                    if( stoneBrickMossyItem != null )
                    {
                        GameRegistry.addShapelessRecipe(
                            new ResourceLocation( registryName.getResourceDomain() , registryName.getResourcePath() + "_stonebrickmossy" ),
                            null,
                            new ItemStack( stoneBrickMossyItem ),
                            Ingredient.fromItem( item ),
                            Ingredient.fromItem( Item.getItemFromBlock( Blocks.VINE ) ) );
                    }
                    break;
                case COBBLE:
                    Item stoneItem = itemMap.getOrDefault( TileType.STONE , null );
                    if( stoneItem != null )
                        GameRegistry.addSmelting( item , new ItemStack( stoneItem ) , 0.1f ); // Vanilla exp
                    Item cobbleMossyItem = itemMap.getOrDefault( TileType.COBBLEMOSSY , null );
                    if( cobbleMossyItem != null )
                    {
                        GameRegistry.addShapelessRecipe(
                            new ResourceLocation( registryName.getResourceDomain() , registryName.getResourcePath() + "_cobblemossy" ),
                            null,
                            new ItemStack( cobbleMossyItem ),
                            Ingredient.fromItem( item ),
                            Ingredient.fromItem( Item.getItemFromBlock( Blocks.VINE ) ) );
                    }
                    break;
            }

            ItemStack vanillaItem = tile.vanillaEquivalent(); // Effectively an override
            if( vanillaItem == null )
                vanillaItem = type.vanillaItemStack();
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

                registerVanillaItem( stairItem.getRegistryName() , stairItem , stairType.vanillaItemStack() );
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

                registerVanillaItem( slabItem.getRegistryName() , slabItem , slabType.vanillaItemStack() );
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

                registerVanillaItem( wallItem.getRegistryName() , wallItem , wallType.vanillaItemStack() );
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
