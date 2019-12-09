package com.riintouge.strata;

import com.riintouge.strata.block.DynamicOreHostManager;
import com.riintouge.strata.block.DynamicOreHostModel;
import com.riintouge.strata.block.ore.GenericOreBlockModelLoader;
import com.riintouge.strata.block.ore.IOreTileSet;
import com.riintouge.strata.item.OreItemModelLoader;
import com.riintouge.strata.item.OreItemTextureManager;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.HashMap;
import java.util.Map;

public class GenericOreRegistry
{
    public static final GenericOreRegistry INSTANCE = new GenericOreRegistry();

    private Map< String , IOreTileSet > oreTileSetMap = new HashMap<>();

    private GenericOreRegistry()
    {
    }

    public void register( IOreTileSet tileSet )
    {
        oreTileSetMap.put( tileSet.getInfo().oreName() , tileSet );

        DynamicOreHostManager.INSTANCE.registerOre(
            tileSet.getInfo().oreName(),
            tileSet.getInfo().oreBlockOverlayTextureResource() );
        OreItemTextureManager.INSTANCE.registerOre(
            tileSet.getInfo().oreName(),
            tileSet.getInfo().oreItemTextureResource() );
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

    @SubscribeEvent( priority = EventPriority.LOWEST )
    public static void registerBlocks( RegistryEvent.Register< Block > event )
    {
        System.out.println( "GenericOreRegistry::registerBlocks()" );

        IForgeRegistry< Block > blockRegistry = event.getRegistry();
        for( IOreTileSet tileSet : INSTANCE.oreTileSetMap.values() )
            blockRegistry.register( tileSet.getBlock() );
    }

    @SubscribeEvent( priority = EventPriority.LOWEST )
    public static void registerItems( RegistryEvent.Register< Item > event )
    {
        System.out.println( "GenericOreRegistry::registerItems()" );

        IForgeRegistry< Item > itemRegistry = event.getRegistry();
        for( IOreTileSet tileSet : INSTANCE.oreTileSetMap.values() )
        {
            itemRegistry.register( tileSet.getItem() );

            String oreDictionaryName = tileSet.getInfo().oreDictionaryName();
            if( oreDictionaryName != null )
                OreDictionary.registerOre( oreDictionaryName , tileSet.getItem() );
        }
    }

    @SubscribeEvent( priority = EventPriority.LOWEST )
    public static void registerModels( ModelRegistryEvent event )
    {
        System.out.println( "GenericOreRegistry::registerModels()" );

        ModelLoaderRegistry.registerLoader( new GenericOreBlockModelLoader() );
        ModelLoaderRegistry.registerLoader( new OreItemModelLoader() );

        // TODO: Adjust this code to use either an item or a block for the model like so:
        //new ModelResourceLocation( "minecraft:stone" , "inventory" )
        //new ModelResourceLocation( String.format( "%s:%s%s" , Strata.modid , ResourceUtil.ModelResourceBasePath , "ore_cinnabar" ) , "inventory" )
        //new ModelResourceLocation( tileSet.block.getRegistryName() , null )

        // The process of replacing auto-generated blocks models with DynamicOreHostModel
        // during ModelBakeEvent trashes the would-be generated item models. Instead,
        // load them from yet another auto-generated resource.
        for( IOreTileSet tileSet : INSTANCE.oreTileSetMap.values() )
        {
            ModelLoader.setCustomModelResourceLocation(
                tileSet.getItem(),
                0,
                new ModelResourceLocation( String.format( "%s:%sore_%s" , Strata.modid , OreItemModelLoader.ModelResourceBasePath , tileSet.getInfo().oreName() ) , "inventory" ) );
        }
    }

    @SubscribeEvent( priority = EventPriority.LOWEST )
    public static void stitchTextures( TextureStitchEvent.Pre event )
    {
        System.out.println( "GenericOreRegistry::stitchTextures()" );

        // TODO: Move logic out of DynamicOreHostManager
    }

    @SubscribeEvent( priority = EventPriority.LOWEST )
    public static void bakeModels( ModelBakeEvent event )
    {
        for( IOreTileSet tileSet : INSTANCE.oreTileSetMap.values() )
        {
            ResourceLocation modelResource = new ResourceLocation( Strata.modid , tileSet.getInfo().oreName() );
            ModelResourceLocation modelVariantResource = new ModelResourceLocation( modelResource , null );
            IBakedModel existingModel = event.getModelRegistry().getObject( modelVariantResource );

            if( existingModel != null )
            {
                // FIXME: Replacing the model here wrecks the ItemBlock
                DynamicOreHostModel customModel = new DynamicOreHostModel( existingModel );
                event.getModelRegistry().putObject( modelVariantResource , customModel );
            }
        }
    }
}
