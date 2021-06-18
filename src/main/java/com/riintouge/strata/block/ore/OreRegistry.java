package com.riintouge.strata.block.ore;

import com.riintouge.strata.Strata;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.texture.TextureMap;
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
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.HashMap;
import java.util.Map;

public class OreRegistry
{
    public static final OreRegistry INSTANCE = new OreRegistry();

    private Map< String , IOreTileSet > oreTileSetMap = new HashMap<>();

    private OreRegistry()
    {
        ModelLoaderRegistry.registerLoader( new OreBlockModelLoader() );
        ModelLoaderRegistry.registerLoader( new OreItemModelLoader() );
    }

    public void register( IOreTileSet tileSet )
    {
        oreTileSetMap.put( tileSet.getInfo().oreName() , tileSet );
    }

    public IOreTileSet find( String oreName )
    {
        // TODO: toLower here and elsewhere? ResourceLocation overload?
        return oreTileSetMap.getOrDefault( oreName , null );
    }

    public boolean contains( String oreName )
    {
        return oreTileSetMap.getOrDefault( oreName , null ) != null;
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
            ItemStack equivalentItem = tileSet.getInfo().equivalentItem();
            if( equivalentItem != null )
            {
                GameRegistry.addShapelessRecipe(
                    new ResourceLocation( Strata.modid , oreInfo.oreName() + "_equivalent" ),
                    null,
                    equivalentItem,
                    Ingredient.fromItem( tileSet.getItem() ) );
            }
        }
    }

    @SubscribeEvent( priority = EventPriority.LOWEST )
    public static void registerModels( ModelRegistryEvent event )
    {
        System.out.println( "OreRegistry::registerModels()" );

        // TODO: Adjust this code to use either an item or a block for the model like so:
        //new ModelResourceLocation( "minecraft:stone" , "inventory" )
        //new ModelResourceLocation( String.format( "%s:%s%s" , Strata.modid , ResourceUtil.ModelResourceBasePath , "ore_cinnabar" ) , "inventory" )
        //new ModelResourceLocation( tileSet.block.getRegistryName() , null )

        // The process of replacing auto-generated block models with OreBlockModel
        // during ModelBakeEvent trashes the would-be generated item models. Instead,
        // load them from yet another auto-generated resource.
        for( IOreTileSet tileSet : INSTANCE.oreTileSetMap.values() )
        {
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

    @SubscribeEvent( priority = EventPriority.LOWEST )
    public static void stitchTextures( TextureStitchEvent.Pre event )
    {
        System.out.println( "OreRegistry::stitchTextures()" );

        // TODO: Move logic out of OreBlockTextureManager?
        TextureMap textureMap = event.getMap();
        for( IOreTileSet tileSet : INSTANCE.oreTileSetMap.values() )
            tileSet.getInfo().stitchTextures( textureMap );
    }

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
                modelRegistry.putObject( modelVariantResource , new OreBlockModel( existingModel ) );
        }
    }
}
