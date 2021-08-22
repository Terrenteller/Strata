package com.riintouge.strata.block.geo;

import com.riintouge.strata.block.IForgeRegistrable;
import com.riintouge.strata.block.RecipeReplicator;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
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
    private final String DefaultFragmentVariant = null; // Doesn't actually do anything because items ignore it
    // Should we try to get properties from the default block state instead?
    private final String DefaultStairModelVariant = "facing=east,half=bottom,shape=straight";
    private final String DefaultSlabModelVariant = "half=bottom,variant=default";
    private final String DefaultWallModelVariant = "inventory";
    private final String DefaultButtonModelVariant = "inventory";
    private final String DefaultLeverModelVariant = "facing=up_z,powered=false";
    private final String DefaultPressurePlateModelVariant = "powered=false";

    protected Map< TileType , IGeoTileInfo > tiles = new HashMap<>();
    protected Map< TileType , Item > itemMap = new HashMap<>();
    protected Map< TileType , GeoItemFragment > fragmentMap = new HashMap<>();
    protected Map< TileType , GeoBlockStairs > stairsMap = new HashMap<>();
    protected Map< TileType , GeoBlockSlab > slabMap = new HashMap<>();
    protected Map< TileType , GeoBlockSlab > slabsMap = new HashMap<>();
    protected Map< TileType , GeoBlockWall > wallMap = new HashMap<>();
    protected Map< TileType , GeoBlockButton > buttonMap = new HashMap<>();
    protected Map< TileType , GeoBlockLever > leverMap = new HashMap<>();
    protected Map< TileType , GeoBlockPressurePlate > pressurePlateMap = new HashMap<>();

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

    protected void createEquivalentItemConversionRecipe( ResourceLocation registryName , Item item , ItemStack equivalentItem )
    {
        if( equivalentItem != null )
        {
            GameRegistry.addShapelessRecipe(
                new ResourceLocation( registryName.getResourceDomain() , registryName.getResourcePath() + "_equivalent" ),
                null,
                equivalentItem,
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

            TileType buttonType = tile.type().buttonType();
            if( buttonType != null && buttonType.parentType == tile.type() )
            {
                GeoBlockButton button = new GeoBlockButton( tile );
                buttonMap.put( type , button );
                blockRegistry.register( button );
            }

            TileType leverType = tile.type().leverType();
            if( leverType != null && leverType.parentType == tile.type() )
            {
                GeoBlockLever lever = new GeoBlockLever( tile );
                leverMap.put( type , lever );
                blockRegistry.register( lever );
            }

            TileType pressurePlateType = tile.type().pressurePlateType();
            if( pressurePlateType != null && pressurePlateType.parentType == tile.type() )
            {
                GeoBlockPressurePlate pressurePlate = new GeoBlockPressurePlate( tile );
                pressurePlateMap.put( type , pressurePlate );
                blockRegistry.register( pressurePlate );
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

            if( tile.hasFragment() )
            {
                GeoItemFragment fragment = new GeoItemFragment( tile );
                fragmentMap.put( type , fragment );
                itemRegistry.register( fragment );
            }

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

            TileType buttonType = tile.type().buttonType();
            if( buttonType != null && buttonType.parentType == tile.type() )
            {
                GeoItemBlock button = new GeoItemBlock( tile , buttonMap.get( type ) );
                itemMap.put( buttonType , button );
                itemRegistry.register( button );
            }

            TileType leverType = tile.type().leverType();
            if( leverType != null && leverType.parentType == tile.type() )
            {
                GeoItemBlock lever = new GeoItemBlock( tile , leverMap.get( type ) );
                itemMap.put( leverType , lever );
                itemRegistry.register( lever );
            }

            TileType pressurePlateType = tile.type().pressurePlateType();
            if( pressurePlateType != null && pressurePlateType.parentType == tile.type() )
            {
                GeoItemBlock stonePressurePlate = new GeoItemBlock( tile , pressurePlateMap.get( type ) );
                itemMap.put( pressurePlateType , stonePressurePlate );
                itemRegistry.register( stonePressurePlate );
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

            ItemStack equivalentItem = tile.equivalentItemStack();
            if( equivalentItem != null && !equivalentItem.isEmpty() )
                RecipeReplicator.INSTANCE.register( equivalentItem , new ItemStack( item ) );

            ItemStack vanillaItemStack = type.vanillaItemStack();
            if( vanillaItemStack != null )
                RecipeReplicator.INSTANCE.register( vanillaItemStack , new ItemStack( item ) );

            createEquivalentItemConversionRecipe( registryName , item , equivalentItem != null ? equivalentItem : vanillaItemStack );

            Item fragment = fragmentMap.getOrDefault( type , null );
            if( fragment != null )
            {
                GameRegistry.addShapedRecipe(
                    new ResourceLocation( registryName.getResourceDomain() , registryName.getResourcePath() + "_block" ),
                    null,
                    new ItemStack( item ),
                    "XX",
                    "XX",
                    'X' , fragment );

                ItemStack equivalentFragmentItem = tile.equivalentFragmentItemStack();
                if( equivalentFragmentItem != null && !equivalentFragmentItem.isEmpty() )
                    createEquivalentItemConversionRecipe( GeoItemFragment.getResourceLocation( tile ) , fragment , equivalentFragmentItem );
            }

            switch( type )
            {
                case STONE:
                    Item stoneBrickItem = itemMap.getOrDefault( TileType.STONEBRICK , null );
                    if( stoneBrickItem != null )
                    {
                        GameRegistry.addShapedRecipe(
                            new ResourceLocation( registryName.getResourceDomain() , registryName.getResourcePath() + "_stonebrick" ),
                            null,
                            new ItemStack( stoneBrickItem , 4 ),
                            "XX ",
                            " XX",
                            'X' , item );
                    }
                    Item stonePolishedItem = itemMap.getOrDefault( TileType.STONEPOLISHED , null );
                    if( stonePolishedItem != null )
                    {
                        GameRegistry.addShapedRecipe(
                            new ResourceLocation( registryName.getResourceDomain() , registryName.getResourcePath() + "_polished" ),
                            null,
                            new ItemStack( stonePolishedItem , 4 ),
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

                ItemStack vanillaStairs = stairType.vanillaItemStack();
                if( vanillaStairs != null )
                    RecipeReplicator.INSTANCE.register( vanillaStairs , new ItemStack( stairItem ) );
                createEquivalentItemConversionRecipe( stairItem.getRegistryName() , stairItem , stairType.vanillaItemStack() );
            }

            TileType slabType = tile.type().slabType();
            if( slabType != null )
            {
                Item slabItem = itemMap.getOrDefault( slabType , null );
                GameRegistry.addShapedRecipe(
                    new ResourceLocation( registryName.getResourceDomain() , registryName.getResourcePath() + "_slab" ),
                    null,
                    new ItemStack( slabItem , 6 ),
                    "XXX",
                    'X' , item );

                ItemStack vanillaSlab = slabType.vanillaItemStack();
                if( vanillaSlab != null )
                    RecipeReplicator.INSTANCE.register( vanillaSlab , new ItemStack( slabItem ) );
                createEquivalentItemConversionRecipe( slabItem.getRegistryName() , slabItem , slabType.vanillaItemStack() );
            }

            TileType wallType = tile.type().wallType();
            if( wallType != null )
            {
                Item wallItem = itemMap.getOrDefault( wallType , null );
                GameRegistry.addShapedRecipe(
                    new ResourceLocation( registryName.getResourceDomain() , registryName.getResourcePath() + "_wall" ),
                    null,
                    new ItemStack( wallItem , 6 ),
                    "XXX",
                    "XXX",
                    'X' , item );

                ItemStack vanillaWall = wallType.vanillaItemStack();
                if( vanillaWall != null )
                    RecipeReplicator.INSTANCE.register( vanillaWall , new ItemStack( wallItem ) );
                createEquivalentItemConversionRecipe( wallItem.getRegistryName() , wallItem , wallType.vanillaItemStack() );
            }

            TileType buttonType = tile.type().buttonType();
            if( buttonType != null )
            {
                Item buttonItem = itemMap.getOrDefault( buttonType , null );
                if( buttonItem != null )
                {
                    GameRegistry.addShapedRecipe(
                        new ResourceLocation( registryName.getResourceDomain() , registryName.getResourcePath() + "_button" ),
                        null,
                        new ItemStack( buttonItem , 2 ), // Two buttons since the original recipe is 1:1
                        "X",
                        "X",
                        'X' , item );

                    ItemStack vanillaButton = buttonType.vanillaItemStack();
                    if( vanillaButton != null )
                        RecipeReplicator.INSTANCE.register( vanillaButton , new ItemStack( buttonItem ) );
                    createEquivalentItemConversionRecipe( buttonItem.getRegistryName() , buttonItem , buttonType.vanillaItemStack() );
                }
            }

            TileType leverType = tile.type().leverType();
            if( leverType != null )
            {
                Item leverItem = itemMap.getOrDefault( leverType , null );
                if( leverItem != null )
                {
                    GameRegistry.addShapedRecipe(
                        new ResourceLocation( registryName.getResourceDomain() , registryName.getResourcePath() + "_lever" ),
                        null,
                        new ItemStack( leverItem ),
                        "X",
                        "Y",
                        'X' , Items.STICK , 'Y' , item );

                    ItemStack vanillaLever = leverType.vanillaItemStack();
                    if( vanillaLever != null )
                        RecipeReplicator.INSTANCE.register( vanillaLever , new ItemStack( leverItem ) );
                    createEquivalentItemConversionRecipe( leverItem.getRegistryName() , leverItem , leverType.vanillaItemStack() );
                }
            }

            TileType pressurePlateType = tile.type().pressurePlateType();
            if( pressurePlateType != null )
            {
                Item pressurePlateItem = itemMap.getOrDefault( pressurePlateType , null );
                if( pressurePlateItem != null )
                {
                    GameRegistry.addShapedRecipe(
                        new ResourceLocation( registryName.getResourceDomain() , registryName.getResourcePath() + "_pressureplate" ),
                        null,
                        new ItemStack( pressurePlateItem ),
                        "XX",
                        'X' , item );

                    ItemStack vanillaPressurePlate = pressurePlateType.vanillaItemStack();
                    if( vanillaPressurePlate != null )
                        RecipeReplicator.INSTANCE.register( vanillaPressurePlate , new ItemStack( pressurePlateItem ) );
                    createEquivalentItemConversionRecipe( pressurePlateItem.getRegistryName() , pressurePlateItem , pressurePlateType.vanillaItemStack() );
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

            GeoItemFragment fragment = fragmentMap.getOrDefault( tile.type() , null );
            if( fragment != null )
            {
                ModelLoader.setCustomModelResourceLocation(
                    fragment,
                    tile.meta(),
                    new ModelResourceLocation( fragment.getRegistryName() , DefaultFragmentVariant ) );
            }

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

            TileType buttonType = tile.type().buttonType();
            if( buttonType != null )
            {
                ResourceLocation buttonRegistryName = buttonType.registryName( tile.tileSetName() );
                ModelLoader.setCustomModelResourceLocation(
                    Item.REGISTRY.getObject( buttonRegistryName ),
                    tile.meta(),
                    new ModelResourceLocation( buttonRegistryName , DefaultButtonModelVariant ) );
            }

            TileType leverType = tile.type().leverType();
            if( leverType != null )
            {
                ResourceLocation leverRegistryName = leverType.registryName( tile.tileSetName() );
                ModelLoader.setCustomModelResourceLocation(
                    Item.REGISTRY.getObject( leverRegistryName ),
                    tile.meta(),
                    new ModelResourceLocation( leverRegistryName , DefaultLeverModelVariant ) );
            }

            TileType pressurePlateType = tile.type().pressurePlateType();
            if( pressurePlateType != null )
            {
                ResourceLocation pressurePlateRegistryName = pressurePlateType.registryName( tile.tileSetName() );
                ModelLoader.setCustomModelResourceLocation(
                    Item.REGISTRY.getObject( pressurePlateRegistryName ),
                    tile.meta(),
                    new ModelResourceLocation( pressurePlateRegistryName , DefaultPressurePlateModelVariant ) );
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
