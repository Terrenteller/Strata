package com.riintouge.strata.block.ore;

import com.riintouge.strata.Strata;
import com.riintouge.strata.block.FurnaceRecipeReplicator;
import com.riintouge.strata.block.RecipeReplicator;
import com.riintouge.strata.block.geo.BakedModelCache;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.IRegistry;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.registries.IForgeRegistry;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public final class OreRegistry
{
    public static final OreRegistry INSTANCE = new OreRegistry();

    private Map< String , IOreTileSet > oreTileSetMap = new HashMap<>();

    private OreRegistry()
    {
        // Nothing to do
    }

    public void register( IOreTileSet tileSet )
    {
        oreTileSetMap.put( tileSet.getInfo().oreName() , tileSet );
    }

    @Nullable
    public IOreTileSet find( @Nullable String oreName )
    {
        // TODO: toLower here and elsewhere? ResourceLocation overload?
        return oreTileSetMap.getOrDefault( oreName , null );
    }

    public boolean contains( @Nullable String oreName )
    {
        return oreTileSetMap.getOrDefault( oreName , null ) != null;
    }

    @Nonnull
    public Collection< IOreTileSet > all()
    {
        return oreTileSetMap.values();
    }

    // Statics

    @SubscribeEvent( priority = EventPriority.LOWEST )
    public static void registerBlocks( RegistryEvent.Register< Block > event )
    {
        System.out.println( "OreRegistry::registerBlocks()" );

        IForgeRegistry< Block > blockRegistry = event.getRegistry();
        for( IOreTileSet tileSet : INSTANCE.oreTileSetMap.values() )
            blockRegistry.register( tileSet.getBlock() );
    }

    @SubscribeEvent( priority = EventPriority.LOWEST )
    public static void registerItems( RegistryEvent.Register< Item > event )
    {
        System.out.println( "OreRegistry::registerItems()" );

        IForgeRegistry< Item > itemRegistry = event.getRegistry();
        for( IOreTileSet tileSet : INSTANCE.oreTileSetMap.values() )
        {
            itemRegistry.register( tileSet.getItemBlock() );
            String blockOreDictionaryName = tileSet.getInfo().blockOreDictionaryName();
            if( blockOreDictionaryName != null )
                OreDictionary.registerOre( blockOreDictionaryName , tileSet.getItemBlock() );

            itemRegistry.register( tileSet.getItem() );
            String itemOreDictionaryName = tileSet.getInfo().itemOreDictionaryName();
            if( itemOreDictionaryName != null )
                OreDictionary.registerOre( itemOreDictionaryName , tileSet.getItem() );
        }
    }

    @SubscribeEvent( priority = EventPriority.LOWEST )
    public static void registerRecipes( RegistryEvent.Register< IRecipe > event )
    {
        System.out.println( "OreRegistry::registerRecipes()" );

        for( IOreTileSet tileSet : INSTANCE.oreTileSetMap.values() )
        {
            IOreInfo oreInfo = tileSet.getInfo();
            IBlockState proxyBlockState = oreInfo.proxyBlockState();
            ItemStack blockTargetItemStack = proxyBlockState != null
                ? new ItemStack( proxyBlockState.getBlock() )
                : new ItemStack( tileSet.getItem() );

            GameRegistry.addShapelessRecipe(
                new ResourceLocation( Strata.modid , oreInfo.oreName() + "_item" ),
                null,
                blockTargetItemStack,
                Ingredient.fromItem( tileSet.getItemBlock() ) );

            // Although the item block should be creative only, we'll add a recipe for convenience
            FurnaceRecipeReplicator.replicateTargetRecipeOrCreateNew( tileSet.getItemBlock() , oreInfo.furnaceResult() , oreInfo.furnaceExp() );
            FurnaceRecipeReplicator.replicateTargetRecipeOrCreateNew( tileSet.getItem() , oreInfo.furnaceResult() , oreInfo.furnaceExp() );

            ItemStack equivalentItem = tileSet.getInfo().equivalentItemStack();
            if( ( equivalentItem == null || equivalentItem.isEmpty() ) && proxyBlockState != null )
            {
                Item proxyBlockDroppedItem = proxyBlockState.getBlock().getItemDropped( proxyBlockState , null , 0 );
                if( proxyBlockDroppedItem != null && !proxyBlockDroppedItem.equals( Items.AIR ) )
                    equivalentItem = new ItemStack( proxyBlockDroppedItem );
            }

            if( equivalentItem != null && !equivalentItem.isEmpty() )
            {
                RecipeReplicator.INSTANCE.register( equivalentItem , new ItemStack( tileSet.getItem() ) );
                GameRegistry.addShapelessRecipe(
                    new ResourceLocation( Strata.modid , oreInfo.oreName() + "_equivalent" ),
                    null,
                    equivalentItem,
                    Ingredient.fromItem( tileSet.getItem() ) );
            }
        }
    }

    @SideOnly( Side.CLIENT )
    @SubscribeEvent( priority = EventPriority.LOWEST )
    public static void registerModels( ModelRegistryEvent event )
    {
        System.out.println( "OreRegistry::registerModels()" );

        for( IOreTileSet tileSet : INSTANCE.oreTileSetMap.values() )
        {
            ModelLoader.setCustomStateMapper( tileSet.getBlock() , new StateMapperBase()
            {
                @Override
                protected ModelResourceLocation getModelResourceLocation( IBlockState state )
                {
                    return new ModelResourceLocation( state.getBlock().getRegistryName() , "normal" );
                }
            } );

            // The process of replacing auto-generated block models with OreBlockModel
            // during ModelBakeEvent trashes the would-be generated item models. Instead,
            // load them from yet another auto-generated resource.

            ModelLoader.setCustomModelResourceLocation(
                tileSet.getItemBlock(),
                0,
                new ModelResourceLocation( Strata.resource( tileSet.getInfo().oreName() + OreBlock.RegistryNameSuffix ) , "inventory" ) );

            ModelLoader.setCustomModelResourceLocation(
                tileSet.getItem(),
                0,
                new ModelResourceLocation( Strata.resource( tileSet.getInfo().oreName() ) , "inventory" ) );
        }
    }

    @SideOnly( Side.CLIENT )
    @SubscribeEvent( priority = EventPriority.LOWEST )
    public static void stitchTextures( TextureStitchEvent.Pre event )
    {
        System.out.println( "OreRegistry::stitchTextures()" );

        TextureMap textureMap = event.getMap();
        for( IOreTileSet tileSet : INSTANCE.oreTileSetMap.values() )
            tileSet.getInfo().stitchTextures( textureMap );
    }

    @SideOnly( Side.CLIENT )
    @SubscribeEvent( priority = EventPriority.LOWEST )
    public static void bakeModels( ModelBakeEvent event )
    {
        IRegistry< ModelResourceLocation , IBakedModel > modelRegistry = event.getModelRegistry();
        for( IOreTileSet tileSet : INSTANCE.oreTileSetMap.values() )
        {
            ResourceLocation modelResource = Strata.resource( tileSet.getInfo().oreName() + OreBlock.RegistryNameSuffix );
            ModelResourceLocation modelVariantResource = new ModelResourceLocation( modelResource , null );
            IBakedModel existingModel = modelRegistry.getObject( modelVariantResource );

            if( existingModel != null )
            {
                IBakedModel bakedOreModel = new OreBlockModel( tileSet , existingModel );
                BakedModelCache.INSTANCE.registerBakedOreModel( tileSet.getInfo().oreName() , bakedOreModel );
                modelRegistry.putObject( modelVariantResource , bakedOreModel );
            }
        }
    }
}
