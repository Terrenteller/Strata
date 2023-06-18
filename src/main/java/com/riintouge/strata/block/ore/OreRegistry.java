package com.riintouge.strata.block.ore;

import com.riintouge.strata.Strata;
import com.riintouge.strata.block.SampleBlock;
import com.riintouge.strata.recipe.CraftingRecipeReplicator;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
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

    private final Map< String , IOreTileSet > oreMap = new HashMap<>();

    private OreRegistry()
    {
        // Nothing to do
    }

    public void register( IOreTileSet tileSet ) throws IllegalStateException
    {
        String oreName = tileSet.getInfo().oreName();
        if( find( oreName ) != null )
            throw new IllegalStateException( String.format( "Ore '%s' already registered!" , oreName ) );

        oreMap.put( tileSet.getInfo().oreName() , tileSet );
    }

    @Nullable
    public IOreTileSet find( @Nullable String oreName )
    {
        // TODO: toLower here and elsewhere? ResourceLocation overload?
        return oreMap.getOrDefault( oreName , null );
    }

    public boolean contains( @Nullable String oreName )
    {
        return oreMap.getOrDefault( oreName , null ) != null;
    }

    @Nonnull
    public Collection< IOreTileSet > all()
    {
        return oreMap.values();
    }

    // Statics

    @SubscribeEvent( priority = EventPriority.LOWEST )
    public static void registerBlocks( RegistryEvent.Register< Block > event )
    {
        Strata.LOGGER.trace( "OreRegistry::registerBlocks()" );

        IForgeRegistry< Block > blockRegistry = event.getRegistry();
        for( IOreTileSet tileSet : INSTANCE.oreMap.values() )
        {
            blockRegistry.register( tileSet.getBlock() );
            blockRegistry.register( tileSet.getSampleBlock() );
        }

        GameRegistry.registerTileEntity( OreBlockTileEntity.class , OreBlockTileEntity.REGISTRY_NAME );
    }

    @SubscribeEvent( priority = EventPriority.LOWEST )
    public static void registerItems( RegistryEvent.Register< Item > event )
    {
        Strata.LOGGER.trace( "OreRegistry::registerItems()" );

        IForgeRegistry< Item > itemRegistry = event.getRegistry();
        for( IOreTileSet tileSet : INSTANCE.oreMap.values() )
        {
            itemRegistry.register( tileSet.getItemBlock() );
            String blockOreDictionaryName = tileSet.getInfo().blockOreDictionaryName();
            if( blockOreDictionaryName != null )
                OreDictionary.registerOre( blockOreDictionaryName , tileSet.getItemBlock() );

            itemRegistry.register( tileSet.getItem() );
            String itemOreDictionaryName = tileSet.getInfo().itemOreDictionaryName();
            if( itemOreDictionaryName != null )
                OreDictionary.registerOre( itemOreDictionaryName , tileSet.getItem() );

            itemRegistry.register( tileSet.getSampleItemBlock() );
        }
    }

    @SubscribeEvent( priority = EventPriority.LOWEST )
    public static void registerRecipes( RegistryEvent.Register< IRecipe > event )
    {
        Strata.LOGGER.trace( "OreRegistry::registerRecipes()" );

        for( IOreTileSet tileSet : INSTANCE.oreMap.values() )
        {
            IOreInfo oreInfo = tileSet.getInfo();
            IBlockState proxyBlockState = oreInfo.proxyBlockState();
            ItemStack oreBlockConversionTarget = proxyBlockState != null
                ? new ItemStack( proxyBlockState.getBlock() )
                : new ItemStack( tileSet.getItem() );

            GameRegistry.addShapelessRecipe(
                Strata.resource( tileSet.getBlock().getRegistryName().getResourcePath() + "_equivalent" ),
                null,
                oreBlockConversionTarget,
                Ingredient.fromItem( tileSet.getItemBlock() ) );

            ItemStack furnaceResult = oreInfo.furnaceResult();
            if( furnaceResult != null && !furnaceResult.isEmpty() )
            {
                // The item block is creative only but we'll add a recipe for convenience
                float furnaceExperience = oreInfo.furnaceExperience();
                GameRegistry.addSmelting( tileSet.getItemBlock() , furnaceResult , furnaceExperience );
                GameRegistry.addSmelting( tileSet.getItem() , furnaceResult , furnaceExperience );
                GameRegistry.addSmelting( tileSet.getSampleItemBlock() , furnaceResult , furnaceExperience );
            }

            ItemStack equivalentItem = oreInfo.equivalentItemStack();
            ItemStack sampleEquivalentItem = oreBlockConversionTarget;

            if( equivalentItem != null && !equivalentItem.isEmpty() )
            {
                CraftingRecipeReplicator.INSTANCE.associate( equivalentItem , new ItemStack( tileSet.getItem() ) );
                GameRegistry.addShapelessRecipe(
                    Strata.resource( oreInfo.oreName() + "_equivalent" ),
                    null,
                    equivalentItem,
                    Ingredient.fromItem( tileSet.getItem() ) );

                // Prioritize converting to a specific, equivalent item instead of the full ore
                // because a sample is meant to drop a single item whereas the full ore may drop many things
                sampleEquivalentItem = equivalentItem;
            }

            GameRegistry.addShapelessRecipe(
                Strata.resource( tileSet.getSampleBlock().getRegistryName().getResourcePath() + "_equivalent" ),
                null,
                sampleEquivalentItem,
                Ingredient.fromItem( tileSet.getSampleItemBlock() ) );
        }
    }

    @SideOnly( Side.CLIENT )
    @SubscribeEvent( priority = EventPriority.LOWEST )
    public static void registerModels( ModelRegistryEvent event )
    {
        Strata.LOGGER.trace( "OreRegistry::registerModels()" );

        for( IOreTileSet tileSet : INSTANCE.oreMap.values() )
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
                new ModelResourceLocation( Strata.resource( tileSet.getInfo().oreName() + OreBlock.REGISTRY_NAME_SUFFIX ) , "inventory" ) );

            ModelLoader.setCustomModelResourceLocation(
                tileSet.getItem(),
                0,
                new ModelResourceLocation( Strata.resource( tileSet.getInfo().oreName() ) , "inventory" ) );

            ModelLoader.setCustomModelResourceLocation(
                tileSet.getSampleItemBlock(),
                0,
                new ModelResourceLocation( Strata.resource( tileSet.getInfo().oreName() + SampleBlock.REGISTRY_NAME_SUFFIX ) , "inventory" ) );
        }
    }

    @SideOnly( Side.CLIENT )
    @SubscribeEvent( priority = EventPriority.LOWEST )
    public static void stitchTexturesPre( TextureStitchEvent.Pre event )
    {
        Strata.LOGGER.trace( "OreRegistry::stitchTexturesPre()" );

        TextureMap textureMap = event.getMap();
        for( IOreTileSet tileSet : INSTANCE.oreMap.values() )
            tileSet.getInfo().stitchTextures( textureMap , true );
    }

    @SideOnly( Side.CLIENT )
    @SubscribeEvent( priority = EventPriority.LOWEST )
    public static void stitchTexturesPost( TextureStitchEvent.Post event )
    {
        Strata.LOGGER.trace( "OreRegistry::stitchTexturesPost()" );

        TextureMap textureMap = event.getMap();
        for( IOreTileSet tileSet : INSTANCE.oreMap.values() )
            tileSet.getInfo().stitchTextures( textureMap , false );
    }

    @SideOnly( Side.CLIENT )
    @SubscribeEvent( priority = EventPriority.LOWEST )
    public static void bakeModels( ModelBakeEvent event )
    {
        Strata.LOGGER.trace( "OreRegistry::bakeModels()" );

        IRegistry< ModelResourceLocation , IBakedModel > modelRegistry = event.getModelRegistry();
        for( IOreTileSet tileSet : INSTANCE.oreMap.values() )
        {
            ResourceLocation modelResource = Strata.resource( tileSet.getInfo().oreName() + OreBlock.REGISTRY_NAME_SUFFIX );
            ModelResourceLocation modelVariantResource = new ModelResourceLocation( modelResource , null );
            IBakedModel existingModel = modelRegistry.getObject( modelVariantResource );

            if( existingModel != null )
            {
                IBakedModel bakedOreModel = new OreBlockModel( tileSet , existingModel );
                modelRegistry.putObject( modelVariantResource , bakedOreModel );
            }
        }
    }
}
