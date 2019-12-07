package com.riintouge.strata;

import com.riintouge.strata.block.DynamicOreHostModel;
import com.riintouge.strata.block.ResourceUtil;
import com.riintouge.strata.block.ore.GenericOreTileSet;
import com.riintouge.strata.block.ore.IOreInfo;
import com.riintouge.strata.block.ore.WeakOreInfo;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class GenericOreRegistry
{
    public static final GenericOreRegistry INSTANCE = new GenericOreRegistry();

    private Map< String , GenericOreTileSet > oreTileSetMap = new HashMap<>();

    private GenericOreRegistry()
    {
    }

    public void register( GenericOreTileSet tileSet )
    {
        oreTileSetMap.put( tileSet.oreInfo.oreName() , tileSet );
    }

    public GenericOreTileSet find( String oreName )
    {
        // TODO: toLower here and elsewhere? ResourceLocation overload?
        return oreTileSetMap.getOrDefault( oreName , null );
    }

    public boolean contains( String oreName )
    {
        return oreTileSetMap.getOrDefault( oreName , null ) != null;
    }

    public Collection< GenericOreTileSet > getTileSets()
    {
        // TODO: return collection of interfaces
        return oreTileSetMap.values();
    }

    public void registerBlocks( RegistryEvent.Register< Block > event )
    {
        System.out.println( "GenericOreRegistry::registerBlocks()" );

        IForgeRegistry< Block > blockRegistry = event.getRegistry();
        for( GenericOreTileSet tileSet : oreTileSetMap.values() )
            blockRegistry.register( tileSet.block );
    }

    public void registerItems( RegistryEvent.Register< Item > event )
    {
        System.out.println( "GenericOreRegistry::registerItems()" );

        IForgeRegistry< Item > itemRegistry = event.getRegistry();
        for( GenericOreTileSet tileSet : oreTileSetMap.values() )
        {
            itemRegistry.register( tileSet.blockItem );

            String oreDictionaryName = tileSet.oreInfo.oreDictionaryName();
            if( oreDictionaryName != null )
                OreDictionary.registerOre( oreDictionaryName , tileSet.blockItem );
        }
    }

    public void registerModels( ModelRegistryEvent event )
    {
        System.out.println( "GenericOreRegistry::registerModels()" );

        // TODO: Adjust this code to use either an item or a block for the model like so:
        //new ModelResourceLocation( "minecraft:stone" , "inventory" )
        //new ModelResourceLocation( String.format( "%s:%s%s" , Strata.modid , ResourceUtil.ModelResourceBasePath , "ore_cinnabar" ) , "inventory" )
        //new ModelResourceLocation( tileSet.block.getRegistryName() , null )

        // The process of replacing auto-generated blocks models with DynamicOreHostModel
        // during ModelBakeEvent trashes the would-be generated item models. Instead,
        // load them from yet another auto-generated resource.
        for( GenericOreTileSet tileSet : oreTileSetMap.values() )
        {
            ModelLoader.setCustomModelResourceLocation(
                tileSet.blockItem,
                0,
                new ModelResourceLocation( String.format( "%s:%sore_%s" , Strata.modid , ResourceUtil.ModelResourceBasePath , tileSet.oreInfo.oreName() ) , "inventory" ) );
        }
    }

    public void stitchTextures( TextureStitchEvent.Pre event )
    {
        System.out.println( "GenericOreRegistry::stitchTextures()" );

        // TODO: Move logic out of DynamicOreHostManager
    }

    public void bakeModels( ModelBakeEvent event )
    {
        for( GenericOreTileSet tileSet : oreTileSetMap.values() )
        {
            ResourceLocation asdf = new ResourceLocation( Strata.modid , tileSet.oreInfo.oreName() );
            ModelResourceLocation modelResource = new ModelResourceLocation( asdf , null );
            IBakedModel existingModel = event.getModelRegistry().getObject( modelResource );
            if( existingModel != null )
            {
                // FIXME: Replacing the model here wrecks the ItemBlock
                DynamicOreHostModel customModel = new DynamicOreHostModel( existingModel );
                event.getModelRegistry().putObject( modelResource , customModel );
            }
        }
    }
}
