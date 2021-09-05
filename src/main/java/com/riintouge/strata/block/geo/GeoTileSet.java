package com.riintouge.strata.block.geo;

import com.riintouge.strata.Strata;
import com.riintouge.strata.block.GenericCubeTextureMap;
import com.riintouge.strata.block.IForgeRegistrable;
import com.riintouge.strata.block.RecipeReplicator;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.*;

import static com.riintouge.strata.block.geo.TileType.*;

public class GeoTileSet implements IForgeRegistrable
{
    protected IGeoTileInfo[] tileInfos;
    protected Block[] blocks;
    protected ItemBlock[] itemBlocks;
    protected GeoItemFragment[] fragmentItems;

    public GeoTileSet()
    {
        int numberOfTileTypes = TileType.values().length;
        tileInfos = new IGeoTileInfo[ numberOfTileTypes ];
        blocks = new Block[ numberOfTileTypes ];
        itemBlocks = new ItemBlock[ numberOfTileTypes ];
        fragmentItems = new GeoItemFragment[ numberOfTileTypes ];
    }

    public void addTile( IGeoTileInfo tileInfo )
    {
        tileInfos[ tileInfo.type().ordinal() ] = tileInfo;
    }

    public IGeoTileInfo find( TileType type )
    {
        return tileInfos[ type.ordinal() ];
    }

    protected void createEquivalentItemConversionRecipe( ResourceLocation registryName , Item item , ItemStack equivalentItem )
    {
        if( equivalentItem != null )
        {
            GameRegistry.addShapelessRecipe(
                new ResourceLocation( registryName.toString() + "_equivalent" ),
                null,
                equivalentItem,
                Ingredient.fromItem( item ) );
        }
    }

    // IForgeRegistrable overrides

    @Override
    public void registerBlocks( IForgeRegistry< Block > blockRegistry )
    {
        for( TileType type : TileType.values() )
        {
            int typeIndex = type.ordinal();
            IGeoTileInfo tileInfo = tileInfos[ typeIndex ];
            if( tileInfo == null )
                continue;

            Block block;
            switch( type )
            {
                case COBBLESTAIRS:
                case STONESTAIRS:
                case STONEBRICKSTAIRS:
                    block = new GeoBlockStairs( tileInfo , blocks[ type.parentType.ordinal() ].getDefaultState() );
                    break;
                case COBBLESLAB:
                case STONESLAB:
                case STONEBRICKSLAB:
                    block = new GeoBlockSlab( tileInfo );
                    break;
                case COBBLESLABS:
                case STONESLABS:
                case STONEBRICKSLABS:
                    block = new GeoBlockSlabs( tileInfo , (GeoBlockSlab)blocks[ typeIndex - 1 ] );
                    break;
                case COBBLEWALL:
                case COBBLEWALLMOSSY:
                case STONEWALL:
                case STONEBRICKWALL:
                case STONEBRICKWALLMOSSY:
                    block = new GeoBlockWall( tileInfo , blocks[ type.parentType.ordinal() ] );
                    break;
                case BUTTON:
                    block = new GeoBlockButton( tileInfo );
                    break;
                case LEVER:
                    block = new GeoBlockLever( tileInfo );
                    break;
                case PRESSUREPLATE:
                    block = new GeoBlockPressurePlate( tileInfo );
                    break;
                default:
                    block = new GeoBlock( tileInfo );
            }

            blocks[ typeIndex ] = block;
            blockRegistry.register( block );
        }
    }

    @Override
    public void registerItems( IForgeRegistry< Item > itemRegistry )
    {
        for( TileType type : TileType.values() )
        {
            int typeIndex = type.ordinal();
            IGeoTileInfo tileInfo = tileInfos[ typeIndex ];
            if( tileInfo == null )
                continue;

            if( tileInfo.hasFragment() )
            {
                GeoItemFragment fragment = new GeoItemFragment( tileInfo );
                fragmentItems[ typeIndex ] = fragment;
                itemRegistry.register( fragment );
            }

            ItemBlock itemBlock;
            switch( type )
            {
                case COBBLESLAB:
                case STONESLAB:
                case STONEBRICKSLAB:
                    itemBlock = new GeoItemBlockSlab(
                        tileInfo,
                        (GeoBlockSlab)blocks[ type.ordinal() ],
                        (GeoBlockSlab)blocks[ type.ordinal() + 1 ] );
                    break;
                case COBBLESLABS:
                case STONESLABS:
                case STONEBRICKSLABS:
                    continue; // Double slabs do not have an item
                default:
                    itemBlock = new GeoItemBlock( tileInfo , blocks[ typeIndex ] );
            }

            itemBlocks[ typeIndex ] = itemBlock;
            itemRegistry.register( itemBlock );
        }
    }

    @Override
    public void registerRecipes( IForgeRegistry< IRecipe > recipeRegistry )
    {
        for( TileType type : TileType.values() )
        {
            int typeIndex = type.ordinal();
            IGeoTileInfo tileInfo = tileInfos[ typeIndex ];
            ItemBlock itemBlock = itemBlocks[ type.ordinal() ];
            if( tileInfo == null || itemBlock == null )
                continue;

            ResourceLocation registryName = tileInfo.registryName();
            ItemBlock parentItemBlock = type.parentType != null ? itemBlocks[ type.parentType.ordinal() ] : null;
            GeoItemFragment fragment = fragmentItems[ type.ordinal() ];

            if( fragment != null )
            {
                GameRegistry.addShapedRecipe(
                    new ResourceLocation( registryName.toString() + "_block" ),
                    null,
                    new ItemStack( itemBlock ),
                    "XX",
                    "XX",
                    'X' , fragment );

                ItemStack equivalentFragmentItem = tileInfo.equivalentFragmentItemStack();
                if( equivalentFragmentItem != null && !equivalentFragmentItem.isEmpty() )
                    createEquivalentItemConversionRecipe( GeoItemFragment.getResourceLocation( tileInfo ) , fragment , equivalentFragmentItem );
            }

            switch( type )
            {
                case STONE:
                {
                    ItemBlock cobbleItemBlock = itemBlocks[ TileType.COBBLE.ordinal() ];
                    if( cobbleItemBlock != null )
                        GameRegistry.addSmelting( cobbleItemBlock , new ItemStack( itemBlock ) , 0.1f ); // Vanilla exp

                    break;
                }
                case COBBLEMOSSY:
                {
                    ItemBlock cobbleItemBlock = itemBlocks[ TileType.COBBLE.ordinal() ];
                    if( cobbleItemBlock != null )
                    {
                        GameRegistry.addShapelessRecipe(
                            registryName,
                            null,
                            new ItemStack( itemBlock ),
                            Ingredient.fromItem( cobbleItemBlock ),
                            Ingredient.fromItem( Item.getItemFromBlock( Blocks.VINE ) ) );
                    }

                    break;
                }
                case STONEBRICK:
                {
                    ItemBlock stoneItemBlock = itemBlocks[ TileType.STONE.ordinal() ];
                    if( stoneItemBlock != null )
                    {
                        GameRegistry.addShapedRecipe(
                            registryName,
                            null,
                            new ItemStack( stoneItemBlock , 4 ),
                            "XX ",
                            " XX",
                            'X' , itemBlock );
                    }

                    break;
                }
                case STONEBRICKMOSSY:
                {
                    ItemBlock stoneBrickItemBlock = itemBlocks[ TileType.STONEBRICK.ordinal() ];
                    if( stoneBrickItemBlock != null )
                    {
                        GameRegistry.addShapelessRecipe(
                            registryName,
                            null,
                            new ItemStack( stoneBrickItemBlock ),
                            Ingredient.fromItem( itemBlock ),
                            Ingredient.fromItem( Item.getItemFromBlock( Blocks.VINE ) ) );
                    }

                    break;
                }
                case STONEPOLISHED:
                {
                    ItemBlock stoneItemBlock = itemBlocks[ TileType.STONE.ordinal() ];
                    if( stoneItemBlock != null )
                    {
                        GameRegistry.addShapedRecipe(
                            registryName,
                            null,
                            new ItemStack( stoneItemBlock , 4 ),
                            "XX",
                            "XX",
                            'X' , itemBlock );
                    }

                    break;
                }
                case COBBLESTAIRS:
                case STONESTAIRS:
                case STONEBRICKSTAIRS:
                {
                    if( parentItemBlock != null )
                    {
                        GameRegistry.addShapedRecipe(
                            registryName,
                            null,
                            new ItemStack( itemBlock , 4 ),
                            "X  ",
                            "XX ",
                            "XXX",
                            'X' , parentItemBlock );
                    }

                    break;
                }
                case COBBLESLAB:
                case STONESLAB:
                case STONEBRICKSLAB:
                {
                    if( parentItemBlock != null )
                    {
                        GameRegistry.addShapedRecipe(
                            registryName,
                            null,
                            new ItemStack( itemBlock , 6 ),
                            "XXX",
                            'X' , parentItemBlock );
                    }

                    break;
                }
                case COBBLEWALL:
                case COBBLEWALLMOSSY:
                case STONEWALL:
                case STONEBRICKWALL:
                case STONEBRICKWALLMOSSY:
                {
                    if( parentItemBlock != null )
                    {
                        GameRegistry.addShapedRecipe(
                            registryName,
                            null,
                            new ItemStack( itemBlock , 6 ),
                            "XXX",
                            "XXX",
                            'X' , parentItemBlock );
                    }

                    ItemBlock mossyWall = null;
                    if( type == COBBLEWALL )
                        mossyWall = itemBlocks[ COBBLEWALLMOSSY.ordinal() ];
                    else if( type == STONEBRICKWALL )
                        mossyWall = itemBlocks[ STONEBRICKWALLMOSSY.ordinal() ];

                    if( mossyWall != null )
                    {
                        GameRegistry.addShapelessRecipe(
                            Strata.resource( registryName.getResourcePath() + "_mossy" ),
                            null,
                            new ItemStack( mossyWall ),
                            Ingredient.fromItem( itemBlock ),
                            Ingredient.fromItem( Item.getItemFromBlock( Blocks.VINE ) ) );
                    }

                    break;
                }
                case BUTTON:
                {
                    if( parentItemBlock != null )
                    {
                        GameRegistry.addShapedRecipe(
                            registryName,
                            null,
                            new ItemStack( itemBlock , 2 ), // Two buttons since the original recipe is 1:1
                            "X",
                            "X",
                            'X' , parentItemBlock );
                    }

                    break;
                }
                case LEVER:
                {
                    if( parentItemBlock != null )
                    {
                        GameRegistry.addShapedRecipe(
                            registryName,
                            null,
                            new ItemStack( itemBlock ),
                            "X",
                            "Y",
                            'X' , Items.STICK , 'Y' , parentItemBlock );
                    }

                    break;
                }
                case PRESSUREPLATE:
                {
                    if( parentItemBlock != null )
                    {
                        GameRegistry.addShapedRecipe(
                            registryName,
                            null,
                            new ItemStack( itemBlock ),
                            "XX",
                            'X' , parentItemBlock );
                    }

                    break;
                }
            }

            ItemStack equivalentItem = tileInfo.equivalentItemStack();
            if( equivalentItem != null && !equivalentItem.isEmpty() )
                RecipeReplicator.INSTANCE.register( equivalentItem , new ItemStack( itemBlock ) );

            ItemStack vanillaItemStack = type.vanillaItemStack();
            if( vanillaItemStack != null && !vanillaItemStack.isEmpty() )
                RecipeReplicator.INSTANCE.register( vanillaItemStack , new ItemStack( itemBlock ) );

            createEquivalentItemConversionRecipe( registryName , itemBlock , equivalentItem != null ? equivalentItem : vanillaItemStack );
        }
    }

    @Override
    public void registerModels( ModelRegistryEvent event )
    {
        for( TileType type : TileType.values() )
        {
            int typeIndex = type.ordinal();
            IGeoTileInfo tileInfo = tileInfos[ typeIndex ];
            if( tileInfo == null )
                continue;

            GeoItemFragment fragment = fragmentItems[ typeIndex ];
            if( fragment != null )
            {
                // The null variant Doesn't do anything because items ignore it
                ModelLoader.setCustomModelResourceLocation(
                    fragment,
                    tileInfo.meta(),
                    new ModelResourceLocation( fragment.getRegistryName() , null ) );
            }

            ItemBlock itemBlock = itemBlocks[ typeIndex ];
            if( itemBlock != null )
            {
                ModelLoader.setCustomModelResourceLocation(
                    itemBlock,
                    tileInfo.meta(),
                    new ModelResourceLocation( itemBlock.getRegistryName() , type.defaultModelVariant ) );
            }
        }
    }

    @Override
    public void stitchTextures( TextureMap textureMap )
    {
        // TODO: Don't generate a texture if a texture already exists, such as from a texture pack.
        // I think ModelDynBucket has example code for this...

        // Stitching the same texture multiple times leads to weird lookup failures.
        // No exceptions make it back to us, but we'll end up with (sometimes malformed) water and lava particles.
        Set< GenericCubeTextureMap > textureMaps = new HashSet<>();

        for( IGeoTileInfo tileInfo : tileInfos )
            if( tileInfo != null )
                textureMaps.add( tileInfo.modelTextureMap() );

        for( GenericCubeTextureMap tileTextureMap : textureMaps )
            tileTextureMap.stitchTextures( textureMap );
    }
}
