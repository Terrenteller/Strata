package com.riintouge.strata.block.geo;

import com.riintouge.strata.Config;
import com.riintouge.strata.Strata;
import com.riintouge.strata.util.Util;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.CompoundIngredient;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistry;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.io.*;
import java.util.*;

public class GeoRecipeHelper
{
    public static final GeoRecipeHelper INSTANCE = new GeoRecipeHelper();
    private static boolean ConfigurationLoaded = false;
    private static Set< ResourceLocation > RecipeBlacklist = new HashSet<>();

    private final Map< Class , IngredientReplacer > recipeReplacerMap = new HashMap<>();
    private final Map< ItemStack , Pair< List< ItemStack > , Ingredient > > megaMap = new HashMap<>();

    private interface IngredientReplacer
    {
        IRecipe replace( IRecipe recipe );
    }

    private class GeoCompoundIngredient extends CompoundIngredient
    {
        public GeoCompoundIngredient( Collection< Ingredient > children )
        {
            super( children );
        }
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

    // TODO: Can we re-work registries so we no longer need to register here, but instead process what is in another?
    public void register( ItemStack input , ItemStack alternative )
    {
        Pair< List< ItemStack > , Ingredient > targetPair = megaMap.getOrDefault( input , null );
        if( targetPair == null )
        {
            targetPair = new MutablePair<>( new ArrayList<>() , null );
            megaMap.put( input , targetPair );
        }

        targetPair.getKey().add( alternative );
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
        NonNullList< Ingredient > replacedIngredients = NonNullList.create();
        Set< ItemStack > replaceableItemStacks = megaMap.keySet();
        List< ItemStack > matchingItemStacks = new ArrayList<>();

        for( Ingredient ing : ings )
        {
            for( ItemStack replaceableItemStack : replaceableItemStacks )
                if( ing.apply( replaceableItemStack ) )
                    matchingItemStacks.add( replaceableItemStack );

            switch( matchingItemStacks.size() )
            {
                case 0:
                    replacedIngredients.add( ing );
                    break;
                case 1:
                    replacedIngredients.add( getTargetIngredient( matchingItemStacks.get( 0 ) ) );
                    break;
                default:
                {
                    List< Ingredient > matchingIngredients = new ArrayList<>();
                    for( ItemStack itemStack : matchingItemStacks )
                        matchingIngredients.add( getTargetIngredient( itemStack ) );
                    replacedIngredients.add( new GeoCompoundIngredient( matchingIngredients ) );
                }
            }

            matchingItemStacks.clear();
        }

        return replacedIngredients;
    }

    // Statics

    @SubscribeEvent( priority = EventPriority.LOWEST )
    public static void registerRecipes( RegistryEvent.Register< IRecipe > event )
    {
        System.out.println( "GeoRecipeHelper::registerRecipes()" );

        loadConfiguration();

        IForgeRegistry< IRecipe > recipeRegistry = event.getRegistry();
        List< IRecipe > copies = new ArrayList<>();
        for( Map.Entry< ResourceLocation , IRecipe > recipe : recipeRegistry.getEntries() )
        {
            if( RecipeBlacklist.contains( recipe.getKey() ) )
                continue;

            IRecipe copy = GeoRecipeHelper.INSTANCE.copy( recipe.getValue() );
            if( copy != null )
                copies.add( copy );
        }

        // Register afterwards to not invalidate the iterator
        for( IRecipe recipe : copies )
            recipeRegistry.register( recipe );
    }

    private static void loadConfiguration()
    {
        if( ConfigurationLoaded )
            return;

        try
        {
            for( String path : Config.INSTANCE.allIn( "recipe/replication" , false ) )
            {
                InputStream stream = new FileInputStream( path );
                BufferedReader buffer = new BufferedReader( new InputStreamReader( stream , "UTF-8" ) );
                while( buffer.ready() )
                {
                    String line = buffer.readLine().trim();
                    if( line.isEmpty() || line.charAt( 0 ) == '#' )
                        continue;

                    String[] kv = Util.splitKV( line );
                    switch( kv[ 0 ] )
                    {
                        case "blacklist":
                            RecipeBlacklist.add( new ResourceLocation( kv[ 1 ] ) );
                            break;
                    }
                }

                buffer.close();
            }
        }
        catch( java.io.IOException ex )
        {
            // Ignore
        }

        ConfigurationLoaded = true;
    }
}
