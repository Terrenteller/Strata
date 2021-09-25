package com.riintouge.strata.block;

import com.riintouge.strata.resource.ConfigDir;
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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.regex.Pattern;

public class RecipeReplicator
{
    public static final RecipeReplicator INSTANCE = new RecipeReplicator();
    private static boolean ConfigurationLoaded = false;
    private static Set< Pattern > RecipeBlacklist = new HashSet<>();

    private final Map< Class , IReplicator > recipeReplicatorMap = new HashMap<>();
    private final Map< ItemStack , Pair< List< ItemStack > , Ingredient > > megaMap = new HashMap<>();

    private interface IReplicator
    {
        IRecipe replicate( IRecipe recipe );
    }

    private class ReplicatedCompoundIngredient extends CompoundIngredient
    {
        public ReplicatedCompoundIngredient( Collection< Ingredient > children )
        {
            super( children );
        }
    }

    private RecipeReplicator()
    {
        recipeReplicatorMap.put( ShapedRecipes.class , recipe ->
        {
            ShapedRecipes shapedRecipe = (ShapedRecipes)recipe;
            return new ShapedRecipes(
                shapedRecipe.getGroup(),
                shapedRecipe.getWidth(),
                shapedRecipe.getHeight(),
                substitute( shapedRecipe.getIngredients() ),
                shapedRecipe.getRecipeOutput() );
        } );
        recipeReplicatorMap.put( ShapelessRecipes.class , recipe ->
        {
            ShapelessRecipes shapelessRecipe = (ShapelessRecipes)recipe;
            return new ShapelessRecipes(
                shapelessRecipe.getGroup(),
                shapelessRecipe.getRecipeOutput(),
                substitute( shapelessRecipe.getIngredients() ) );
        } );
    }

    public void register( ItemStack original , ItemStack alternative )
    {
        List< ItemStack > matchingItemStacks;
        Pair< List< ItemStack > , Ingredient > targetPair = megaMap.getOrDefault( original , null );
        if( targetPair == null )
        {
            matchingItemStacks = new ArrayList<>();
            // Start off with the original item to allow mix'n'match.
            // Otherwise, our recipe will exclusively use our items.
            matchingItemStacks.add( original );
            targetPair = new MutablePair<>( matchingItemStacks , null );
            megaMap.put( original , targetPair );
        }
        else
            matchingItemStacks = targetPair.getKey();

        matchingItemStacks.add( alternative );
        targetPair.setValue( null );
    }

    public boolean canReplicate( IRecipe recipe )
    {
        ResourceLocation recipeResourceLocation = recipe.getRegistryName();
        if( recipeResourceLocation.getResourceDomain().equals( Strata.modid ) )
            return false;

        // Input recipe blacklisting
        String recipeResourceLocationString = recipeResourceLocation.toString();
        if( RecipeBlacklist.stream().anyMatch( x -> x.matcher( recipeResourceLocationString ).matches() ) )
            return false;

        // Output recipe blacklisting
        String replicatedResourceLocationString = replicatedResourceLocation( recipeResourceLocation ).toString();
        if( RecipeBlacklist.stream().anyMatch( x -> x.matcher( replicatedResourceLocationString ).matches() ) )
            return false;

        for( Ingredient ing : recipe.getIngredients() )
            for( ItemStack replaceableItemStack : megaMap.keySet() )
                if( ing.apply( replaceableItemStack ) )
                    return true;

        return false;
    }

    @Nullable
    public IRecipe replicate( IRecipe recipe )
    {
        if( !canReplicate( recipe ) )
            return null;

        IReplicator replicator = recipeReplicatorMap.get( recipe.getClass() );
        if( replicator == null )
            return null; // TODO: warn

        return replicator.replicate( recipe ).setRegistryName( replicatedResourceLocation( recipe.getRegistryName() ) );
    }

    @Nonnull
    public ResourceLocation replicatedResourceLocation( ResourceLocation resourceLocation )
    {
        if( resourceLocation.getResourceDomain().equals( Strata.modid ) )
            throw new IllegalArgumentException( "resourceLocation" );

        return Strata.resource( String.format( "%s_%s" , resourceLocation.getResourceDomain() , resourceLocation.getResourcePath() ) );
    }

    @Nonnull
    protected Ingredient getTargetIngredient( ItemStack original )
    {
        Pair< List< ItemStack > , Ingredient > targetPair = megaMap.get( original );
        Ingredient ing = targetPair.getValue();
        if( ing == null )
        {
            List< ItemStack > itemStacks = targetPair.getKey();
            ing = Ingredient.fromStacks( itemStacks.toArray( new ItemStack[ 0 ] ) );
            targetPair.setValue( ing );
        }

        return ing;
    }

    @Nonnull
    protected NonNullList< Ingredient > substitute( NonNullList< Ingredient > ings )
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
                    replacedIngredients.add( new ReplicatedCompoundIngredient( matchingIngredients ) );
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
        Strata.LOGGER.trace( "RecipeReplicator::registerRecipes()" );

        loadConfiguration();

        IForgeRegistry< IRecipe > recipeRegistry = event.getRegistry();
        List< IRecipe > copies = new ArrayList<>();
        for( Map.Entry< ResourceLocation , IRecipe > recipe : recipeRegistry.getEntries() )
        {
            IRecipe copy = RecipeReplicator.INSTANCE.replicate( recipe.getValue() );
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
            for( String path : ConfigDir.INSTANCE.allIn( Strata.modid + "/recipe/replication" , false ) )
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
                            RecipeBlacklist.add( Pattern.compile( kv[ 1 ] ) );
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
