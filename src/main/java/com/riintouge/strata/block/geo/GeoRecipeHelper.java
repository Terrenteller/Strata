package com.riintouge.strata.block.geo;

import com.riintouge.strata.Strata;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistry;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;

public class GeoRecipeHelper
{
    public static final GeoRecipeHelper INSTANCE = new GeoRecipeHelper();

    private final Map< Class , IngredientReplacer > recipeReplacerMap = new HashMap<>();
    private final Map< ItemStack , Pair< List< ItemStack > , Ingredient > > megaMap = new HashMap<>();
    // Cache for fast comparison
    private final Map< TileType , ItemStack > tileTypeToVanillaItemStackMap = new HashMap<>();

    private interface IngredientReplacer
    {
        IRecipe replace( IRecipe recipe );
    }

    private GeoRecipeHelper()
    {
        recipeReplacerMap.put( ShapedRecipes.class , recipe ->
        {
            ShapedRecipes shapedRecipe = (ShapedRecipes)recipe;
            return new ShapedRecipes(
                shapedRecipe.getGroup(),
                shapedRecipe.getWidth(),
                shapedRecipe.getHeight(),
                replace( shapedRecipe.getIngredients() ),
                shapedRecipe.getRecipeOutput() );
        } );
        recipeReplacerMap.put( ShapelessRecipes.class , recipe ->
        {
            ShapelessRecipes shapelessRecipe = (ShapelessRecipes)recipe;
            return new ShapelessRecipes(
                shapelessRecipe.getGroup(),
                shapelessRecipe.getRecipeOutput(),
                replace( shapelessRecipe.getIngredients() ) );
        } );
    }

    // TODO: Might need to take IGeoTileInfo to get at vanillaEquivalent.
    // TODO: Re-work registries so we no longer need to register here, but instead process what is in another.
    public void register( TileType type , ItemStack itemStack )
    {
        ItemStack vanillaItemStack = tileTypeToVanillaItemStack( type );
        if( vanillaItemStack == null )
            return;

        Pair< List< ItemStack > , Ingredient > targetPair = megaMap.getOrDefault( vanillaItemStack , null );
        if( targetPair == null )
        {
            targetPair = new MutablePair<>( new ArrayList<>() , null );
            megaMap.put( vanillaItemStack , targetPair );
        }

        targetPair.getKey().add( itemStack );
        targetPair.setValue( null );
    }

    public boolean shouldCopy( IRecipe recipe )
    {
        for( Ingredient ing : recipe.getIngredients() )
            for( ItemStack replaceableItemStack : megaMap.keySet() )
                if( ing.apply( replaceableItemStack ) )
                    return true;

        return false;
    }

    public IRecipe copy( IRecipe recipe )
    {
        if( !shouldCopy( recipe ) )
            return null;

        IngredientReplacer replacer = recipeReplacerMap.get( recipe.getClass() );
        if( replacer == null )
            return null; // TODO: warn

        return replacer.replace( recipe ).setRegistryName( copyResourceLocation( recipe.getRegistryName() ) );
    }

    public ResourceLocation copyResourceLocation( ResourceLocation resourceLocation )
    {
        if( resourceLocation.getResourceDomain().equalsIgnoreCase( Strata.modid ) )
            throw new IllegalArgumentException();

        return Strata.resource( String.format( "%s_%s" , resourceLocation.getResourceDomain() , resourceLocation.getResourcePath() ) );
    }

    // TODO: Add to TileType? This class should not know about this!
    protected ItemStack tileTypeToVanillaItemStack( TileType tileType )
    {
        ItemStack vanillaItemStack = tileTypeToVanillaItemStackMap.getOrDefault( tileType , null );
        if( vanillaItemStack != null )
            return vanillaItemStack;

        switch( tileType )
        {
            case CLAY:
                vanillaItemStack = new ItemStack( Blocks.CLAY );
                break;
            case STONE:
                vanillaItemStack = new ItemStack( Blocks.STONE );
                break;
            case COBBLE:
                vanillaItemStack = new ItemStack( Blocks.COBBLESTONE );
                break;
            case STONEBRICK:
                vanillaItemStack = new ItemStack( Blocks.STONEBRICK );
                break;
            case STONESLAB:
                vanillaItemStack = new ItemStack( Blocks.STONE_SLAB ); // STONE_SLAB2?
                break;
            default:
                return null;
        }

        tileTypeToVanillaItemStackMap.put( tileType , vanillaItemStack );
        return vanillaItemStack;
    }

    protected Ingredient getTargetIngredient( ItemStack itemStack )
    {
        Pair< List< ItemStack > , Ingredient > targetPair = megaMap.get( itemStack );
        Ingredient ing = targetPair.getValue();
        if( ing == null )
        {
            List< ItemStack > itemStacks = targetPair.getKey();
            ing = Ingredient.fromStacks( itemStacks.toArray( new ItemStack[ itemStacks.size() ] ) );
            targetPair.setValue( ing );
        }

        return ing;
    }

    protected NonNullList< Ingredient > replace( NonNullList< Ingredient > ings )
    {
        NonNullList< Ingredient > freshIngredients = NonNullList.create();
        Set< ItemStack > replaceableItemStacks = megaMap.keySet();

        for( Ingredient ing : ings )
        {
            Ingredient replacement = null;

            for( ItemStack replaceableItemStack : replaceableItemStacks )
            {
                if( ing.apply( replaceableItemStack ) )
                {
                    // TODO: Consider an input ingredient accepting multiple ItemStacks, each of which have a unique replacement
                    replacement = getTargetIngredient( replaceableItemStack );
                    break;
                }
            }

            freshIngredients.add( replacement != null ? replacement : ing );
        }

        return freshIngredients;
    }

    // Statics

    @SubscribeEvent( priority = EventPriority.LOWEST )
    public static void registerRecipes( RegistryEvent.Register< IRecipe > event )
    {
        System.out.println( "GeoRecipeHelper::registerRecipes()" );

        IForgeRegistry< IRecipe > recipeRegistry = event.getRegistry();
        List< IRecipe > copies = new ArrayList<>();
        for( Map.Entry< ResourceLocation , IRecipe > recipe : recipeRegistry.getEntries() )
        {
            // TODO: blacklist directory
            switch( recipe.getKey().getResourcePath() )
            {
                // Strata to vanilla equivalent, or conflicts with
                case "andesite":
                case "stone_button":
                case "diorite":
                // Strata provides its own recipes
                case "cobblestone_slab":
                case "cobblestone_wall":
                case "stone_slab":
                case "stone_stairs":
                case "stonebrick":
                    continue;
            }

            IRecipe copy = GeoRecipeHelper.INSTANCE.copy( recipe.getValue() );
            if( copy != null )
                copies.add( copy );
        }

        // Register afterwards to not invalidate the iterator
        for( IRecipe recipe : copies )
            recipeRegistry.register( recipe );
    }
}
