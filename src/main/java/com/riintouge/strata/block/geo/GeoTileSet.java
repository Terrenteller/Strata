package com.riintouge.strata.block.geo;

import com.riintouge.strata.Strata;
import com.riintouge.strata.misc.IForgeRegistrable;
import com.riintouge.strata.block.ProtoBlockTextureMap;
import com.riintouge.strata.recipe.CraftingRecipeReplicator;
import com.riintouge.strata.block.SpecialBlockPropertyFlags;
import com.riintouge.strata.item.geo.GeoItemBlock;
import com.riintouge.strata.item.geo.GeoItemBlockSlab;
import com.riintouge.strata.item.geo.GeoItemFragment;
import com.riintouge.strata.util.FlagUtil;
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
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.registries.IForgeRegistry;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Set;

import static com.riintouge.strata.block.geo.TileType.*;

public class GeoTileSet implements IGeoTileSet , IForgeRegistrable
{
    protected TileType primaryTileType;
    protected IGeoTileInfo[] tileInfos;
    protected Block[] blocks;
    protected ItemBlock[] itemBlocks;
    protected GeoItemFragment fragmentItem;
    protected Block sampleBlock;
    protected ItemBlock sampleItemBlock;

    public GeoTileSet()
    {
        int numberOfTileTypes = TileType.values().length;
        this.tileInfos = new IGeoTileInfo[ numberOfTileTypes ];
        this.blocks = new Block[ numberOfTileTypes ];
        this.itemBlocks = new ItemBlock[ numberOfTileTypes ];
    }

    public void addTile( IGeoTileInfo tileInfo )
    {
        if( tileInfo.tileType().isPrimary )
        {
            if( primaryTileType == null )
                primaryTileType = tileInfo.tileType();
            else
                throw new IllegalStateException( "GeoTileSet already has a primary tile type!" );
        }

        tileInfos[ tileInfo.tileType().ordinal() ] = tileInfo;
    }

    protected void createEquivalentItemConversionRecipe( ResourceLocation registryName , Item input , ItemStack output )
    {
        if( output != null )
        {
            GameRegistry.addShapelessRecipe(
                new ResourceLocation( registryName.toString() + "_equivalent" ),
                null,
                output,
                Ingredient.fromItem( input ) );
        }
    }

    // IGeoTileSet overrides

    @Nullable
    public TileType getPrimaryType()
    {
        return primaryTileType;
    }

    @Nullable
    public IGeoTileInfo getInfo( @Nullable TileType tileType )
    {
        if( tileType != null )
            return tileInfos[ tileType.ordinal() ];
        else if( primaryTileType != null )
            return tileInfos[ primaryTileType.ordinal() ];

        throw new IllegalStateException( "GeoTileSet does not have a primary IGeoTileInfo!" );
    }

    @Nullable
    public Block getBlock( @Nullable TileType tileType )
    {
        if( tileType != null )
            return blocks[ tileType.ordinal() ];
        else if( primaryTileType != null )
            return blocks[ primaryTileType.ordinal() ];

        throw new IllegalStateException( "GeoTileSet does not have a primary Block!" );
    }

    @Nullable
    public ItemBlock getItemBlock( @Nullable TileType tileType )
    {
        if( tileType != null )
            return itemBlocks[ tileType.ordinal() ];
        else if( primaryTileType != null )
            return itemBlocks[ primaryTileType.ordinal() ];

        throw new IllegalStateException( "GeoTileSet does not have a primary ItemBlock!" );
    }

    @Nullable
    public Item getFragmentItem()
    {
        return fragmentItem;
    }

    @Nullable
    public Block getSampleBlock()
    {
        return sampleBlock;
    }

    @Nullable
    public ItemBlock getSampleItemBlock()
    {
        return sampleItemBlock;
    }

    // IForgeRegistrable overrides

    @Override
    public void registerBlocks( IForgeRegistry< Block > blockRegistry )
    {
        for( TileType tileType : TileType.values() )
        {
            int typeIndex = tileType.ordinal();
            IGeoTileInfo tileInfo = tileInfos[ typeIndex ];
            if( tileInfo == null )
                continue;

            Block block;
            switch( tileType )
            {
                case COBBLESTAIRS:
                case STONESTAIRS:
                case STONEBRICKSTAIRS:
                    block = new GeoBlockStairs( tileInfo , blocks[ tileType.parentType.ordinal() ].getDefaultState() );
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
                    block = new GeoBlockWall( tileInfo , blocks[ tileType.parentType.ordinal() ] );
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

            if( tileType.isPrimary )
            {
                sampleBlock = new GeoSampleBlock( tileInfo , block.getStateFromMeta( tileInfo.meta() ) );
                blockRegistry.register( sampleBlock );
            }
        }
    }

    @Override
    public void registerItems( IForgeRegistry< Item > itemRegistry )
    {
        for( TileType tileType : TileType.values() )
        {
            int typeIndex = tileType.ordinal();
            IGeoTileInfo tileInfo = tileInfos[ typeIndex ];
            if( tileInfo == null )
                continue;

            if( tileType.isPrimary && sampleBlock != null )
            {
                sampleItemBlock = new GeoItemBlock( tileInfo , sampleBlock );
                itemRegistry.register( sampleItemBlock );
            }

            if( tileType.isPrimary && tileInfo.hasFragment() )
            {
                fragmentItem = new GeoItemFragment( tileInfo );
                itemRegistry.register( fragmentItem );

                if( tileInfo.fragmentItemOreDictionaryName() != null )
                    OreDictionary.registerOre( tileInfo.fragmentItemOreDictionaryName() , fragmentItem );
            }

            ItemBlock itemBlock;
            switch( tileType )
            {
                case COBBLESLAB:
                case STONESLAB:
                case STONEBRICKSLAB:
                    itemBlock = new GeoItemBlockSlab(
                        tileInfo,
                        (GeoBlockSlab)blocks[ typeIndex ],
                        (GeoBlockSlab)blocks[ typeIndex + 1 ] );
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

            if( tileInfo.blockOreDictionaryName() != null )
                OreDictionary.registerOre( tileInfo.blockOreDictionaryName() , itemBlock );
        }
    }

    @Override
    public void registerRecipes( IForgeRegistry< IRecipe > recipeRegistry )
    {
        for( TileType tileType : TileType.values() )
        {
            int typeIndex = tileType.ordinal();
            IGeoTileInfo tileInfo = tileInfos[ typeIndex ];
            ItemBlock itemBlock = itemBlocks[ tileType.ordinal() ];
            if( tileInfo == null || itemBlock == null )
                continue;

            ResourceLocation registryName = tileInfo.registryName();
            ItemBlock parentItemBlock = tileType.parentType != null ? itemBlocks[ tileType.parentType.ordinal() ] : null;

            if( tileType.isPrimary && fragmentItem != null )
            {
                if( !FlagUtil.check( tileInfo.specialBlockPropertyFlags() , SpecialBlockPropertyFlags.NOT_RECONSTITUTABLE ) )
                {
                    ItemBlock fragmentReconstruction = itemBlock;
                    ItemBlock cobbleItemBlock = itemBlocks[ TileType.COBBLE.ordinal() ];
                    if( tileType == STONE && cobbleItemBlock != null )
                        fragmentReconstruction = cobbleItemBlock;

                    // It doesn't seem like this should work for glass but Quark and Charset allow it
                    GameRegistry.addShapedRecipe(
                        new ResourceLocation( registryName.toString() + "_block" ),
                        null,
                        new ItemStack( fragmentReconstruction ),
                        "XX",
                        "XX",
                        'X' , fragmentItem );
                }

                ItemStack fragmentFurnaceResult = tileInfo.fragmentFurnaceResult();
                if( fragmentFurnaceResult != null && !fragmentFurnaceResult.isEmpty() )
                    GameRegistry.addSmelting( fragmentItem , fragmentFurnaceResult , tileInfo.fragmentFurnaceExperience() );

                ItemStack equivalentFragmentItem = tileInfo.equivalentFragmentItemStack();
                if( equivalentFragmentItem != null && !equivalentFragmentItem.isEmpty() )
                {
                    createEquivalentItemConversionRecipe(
                        GeoItemFragment.fragmentRegistryName( tileInfo ),
                        fragmentItem,
                        equivalentFragmentItem );
                }
            }

            switch( tileType )
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
                    if( tileType == COBBLEWALL )
                        mossyWall = itemBlocks[ COBBLEWALLMOSSY.ordinal() ];
                    else if( tileType == STONEBRICKWALL )
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

            if( tileType.isPrimary && sampleItemBlock != null )
            {
                if( fragmentItem != null )
                {
                    createEquivalentItemConversionRecipe(
                        sampleItemBlock.getRegistryName(),
                        sampleItemBlock,
                        new ItemStack( fragmentItem ) );
                }

                GameRegistry.addShapedRecipe(
                    Strata.resource( sampleItemBlock.getRegistryName().getResourcePath() + "_block" ),
                    null,
                    new ItemStack( itemBlock ),
                    "XX",
                    "XX",
                    'X' , sampleItemBlock );
            }

            if( tileInfo.tileType().isPrimary )
            {
                ItemStack furnaceResult = tileInfo.furnaceResult();
                if( furnaceResult != null && !furnaceResult.isEmpty() )
                    GameRegistry.addSmelting( itemBlock , furnaceResult , tileInfo.furnaceExperience() );
            }

            ItemStack equivalentItem = tileInfo.equivalentItemStack();
            if( equivalentItem != null && !equivalentItem.isEmpty() )
            {
                CraftingRecipeReplicator.INSTANCE.associate( equivalentItem , new ItemStack( itemBlock ) );
                createEquivalentItemConversionRecipe( registryName , itemBlock , equivalentItem );
            }
        }
    }

    @Override
    public void registerModels( ModelRegistryEvent event )
    {
        for( TileType tileType : TileType.values() )
        {
            int typeIndex = tileType.ordinal();
            IGeoTileInfo tileInfo = tileInfos[ typeIndex ];
            if( tileInfo == null )
                continue;

            if( tileType.isPrimary && sampleItemBlock != null )
            {
                ModelLoader.setCustomModelResourceLocation(
                    sampleItemBlock,
                    tileInfo.meta(),
                    new ModelResourceLocation( sampleItemBlock.getRegistryName() , "inventory" ) );
            }

            if( tileType.isPrimary && fragmentItem != null )
            {
                // The null variant doesn't do anything because items ignore it
                ModelLoader.setCustomModelResourceLocation(
                    fragmentItem,
                    tileInfo.meta(),
                    new ModelResourceLocation( fragmentItem.getRegistryName() , null ) );
            }

            ItemBlock itemBlock = itemBlocks[ typeIndex ];
            if( itemBlock != null )
            {
                ModelLoader.setCustomModelResourceLocation(
                    itemBlock,
                    tileInfo.meta(),
                    new ModelResourceLocation( itemBlock.getRegistryName() , tileType.defaultVariant ) );
            }
        }
    }

    @Override
    @SideOnly( Side.CLIENT )
    public void stitchTextures( TextureMap textureMap )
    {
        // TODO: Don't generate a texture if a texture already exists, such as from a texture pack.
        // I think ModelDynBucket has example code for this...

        // Stitching the same texture multiple times leads to weird lookup failures.
        // No exceptions make it back to us, but we'll end up with (sometimes malformed) water and lava particles.
        Set< ProtoBlockTextureMap > textureMaps = new HashSet<>();

        for( IGeoTileInfo tileInfo : tileInfos )
            if( tileInfo != null )
                textureMaps.add( tileInfo.modelTextureMap() );

        for( ProtoBlockTextureMap tileTextureMap : textureMaps )
            tileTextureMap.stitchTextures( textureMap );
    }
}
