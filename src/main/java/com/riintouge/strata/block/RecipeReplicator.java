package com.riintouge.strata.block;

import com.riintouge.strata.Strata;
import com.riintouge.strata.util.ReflectionUtil;
import com.riintouge.strata.util.Util;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.CompoundIngredient;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.IngredientNBT;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import net.minecraftforge.registries.IForgeRegistry;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.*;
import java.lang.reflect.Field;
import java.util.*;
import java.util.regex.Pattern;

public class RecipeReplicator
{
    public static final RecipeReplicator INSTANCE = new RecipeReplicator();
    private static Set< Pattern > RecipeBlacklist = new HashSet<>();

    private final Map< Class , IReplicator > recipeReplicatorMap = new HashMap<>();
    private final List< Pair< List< ItemStack > , Ingredient > > ingredientInfos = new ArrayList<>();

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
                shapedRecipe.getRecipeWidth(),
                shapedRecipe.getRecipeHeight(),
                substitute( shapedRecipe.getIngredients() ),
                shapedRecipe.getRecipeOutput() );
        } );

        recipeReplicatorMap.put( ShapedOreRecipe.class , recipe ->
        {
            ShapedOreRecipe shapedOreRecipe = (ShapedOreRecipe)recipe;
            CraftingHelper.ShapedPrimer primer = new CraftingHelper.ShapedPrimer();
            primer.width = shapedOreRecipe.getRecipeWidth();
            primer.height = shapedOreRecipe.getRecipeHeight();
            primer.input = substitute( shapedOreRecipe.getIngredients() );

            try
            {
                Field mirroredField = ReflectionUtil.findFieldByType( ShapedOreRecipe.class , boolean.class , true );
                mirroredField.setAccessible( true );
                primer.mirrored = (boolean)mirroredField.get( shapedOreRecipe );
            }
            catch( Exception ex )
            {
                Strata.LOGGER.warn( "Unable to locate field \"ShapedOreRecipe.mirrored\" by type!" );
                primer.mirrored = false;
            }

            return new ShapedOreRecipe(
                new ResourceLocation( shapedOreRecipe.getGroup() ),
                shapedOreRecipe.getRecipeOutput(),
                primer );
        } );

        recipeReplicatorMap.put( ShapelessRecipes.class , recipe ->
        {
            ShapelessRecipes shapelessRecipe = (ShapelessRecipes)recipe;
            return new ShapelessRecipes(
                shapelessRecipe.getGroup(),
                shapelessRecipe.getRecipeOutput(),
                substitute( shapelessRecipe.getIngredients() ) );
        } );

        recipeReplicatorMap.put( ShapelessOreRecipe.class , recipe ->
        {
            ShapelessOreRecipe shapelessRecipe = (ShapelessOreRecipe)recipe;
            return new ShapelessOreRecipe(
                new ResourceLocation( shapelessRecipe.getGroup() ),
                substitute( shapelessRecipe.getIngredients() ),
                shapelessRecipe.getRecipeOutput() );
        } );
    }

    public void associate( ItemStack original , ItemStack alternative )
    {
        for( Pair< List< ItemStack > , Ingredient > ingredientInfo : ingredientInfos )
        {
            List< ItemStack > matchingItemStacks = ingredientInfo.getLeft();
            for( ItemStack itemStack : matchingItemStacks )
            {
                // Transitivity is very important so we don't end up with incomplete recipes!
                boolean originalIsKnown = ItemStack.areItemStacksEqual( original , itemStack );
                boolean alternativeIsKnown = ItemStack.areItemStacksEqual( alternative , itemStack );

                if( originalIsKnown || alternativeIsKnown )
                {
                    if( !originalIsKnown )
                    {
                        matchingItemStacks.add( original );
                        ingredientInfo.setValue( null );
                    }

                    if( !alternativeIsKnown )
                    {
                        matchingItemStacks.add( alternative );
                        ingredientInfo.setValue( null );
                    }

                    return;
                }
            }
        }

        List< ItemStack > matchingItemStacks = new ArrayList<>();
        // Keeping the original item is important so our recipe can mix and match.
        // The original recipe will become a subset of ours but we get the same result so it doesn't matter.
        // We don't remove the original recipe because there's no telling how it may be referenced.
        matchingItemStacks.add( original );
        matchingItemStacks.add( alternative );
        ingredientInfos.add( new MutablePair<>( matchingItemStacks , null ) );
    }

    public boolean canReplicateIngredient( Ingredient ing )
    {
        return ing != Ingredient.EMPTY && !( ing instanceof IngredientNBT );
    }

    public boolean shouldReplicate( IRecipe recipe )
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
        {
            if( !canReplicateIngredient( ing ) )
                continue;

            for( Pair< List< ItemStack > , Ingredient > ingredientInfo : ingredientInfos )
            {
                boolean anyMatchInCurrentList = false , allMatchInCurrentList = true;
                List< ItemStack > matchingItemStacks = ingredientInfo.getLeft();

                for( ItemStack itemStack : matchingItemStacks )
                {
                    if( ing.apply( itemStack ) )
                    {
                        anyMatchInCurrentList = true;
                        continue;
                    }

                    allMatchInCurrentList = false;
                    if( anyMatchInCurrentList )
                        break;
                }

                // We only have a meaningful replication if any of the original ingredients
                // accept some, but not all, of the items we consider associated.
                // Otherwise, that means we have no replacement for the original ingredient
                // or we are redundant with the original ingredient, respectively.
                if( anyMatchInCurrentList && !allMatchInCurrentList )
                    return true;
            }
        }

        return false;
    }

    @Nullable
    public IRecipe replicate( IRecipe recipe )
    {
        if( !shouldReplicate( recipe ) )
            return null;

        IReplicator replicator = recipeReplicatorMap.get( recipe.getClass() );
        if( replicator == null )
        {
            Strata.LOGGER.warn(
                String.format(
                    "No recipe replicator for \"%s\" of type \"%s\"!",
                    recipe.getRegistryName().toString(),
                    recipe.getClass().toString() ) );

            return null;
        }

        return replicator.replicate( recipe ).setRegistryName( replicatedResourceLocation( recipe.getRegistryName() ) );
    }

    @Nonnull
    public ResourceLocation replicatedResourceLocation( ResourceLocation resourceLocation )
    {
        if( resourceLocation.getResourceDomain().equals( Strata.modid ) )
            throw new IllegalArgumentException( "resourceLocation" );

        return Strata.resource( String.format( "%s_%s" , resourceLocation.getResourceDomain() , resourceLocation.getResourcePath() ) );
    }

    protected Ingredient getCompleteIngredientOrOriginal( Ingredient original )
    {
        if( !canReplicateIngredient( original ) )
            return original;

        List< Ingredient > matchingIngredients = new ArrayList<>();
        for( Pair< List< ItemStack > , Ingredient > ingredientInfo : ingredientInfos )
        {
            List< ItemStack > matchingItemStacks = ingredientInfo.getLeft();
            for( ItemStack itemStack : matchingItemStacks )
            {
                if( original.apply( itemStack ) )
                {
                    Ingredient ing = ingredientInfo.getRight();
                    if( ing == null )
                    {
                        ing = Ingredient.fromStacks( matchingItemStacks.toArray( new ItemStack[ 0 ] ) );
                        ingredientInfo.setValue( ing );
                    }

                    matchingIngredients.add( ing );
                    break;
                }
            }
        }

        switch( matchingIngredients.size() )
        {
            case 0:
                return original;
            case 1:
                return matchingIngredients.get( 0 );
            default:
                return new ReplicatedCompoundIngredient( matchingIngredients );
        }
    }

    @Nonnull
    protected NonNullList< Ingredient > substitute( NonNullList< Ingredient > ings )
    {
        NonNullList< Ingredient > replacedIngredients = NonNullList.create();

        for( Ingredient ing : ings )
            replacedIngredients.add( getCompleteIngredientOrOriginal( ing ) );

        return replacedIngredients;
    }

    // Statics

    public static void replicateAndRegister()
    {
        Strata.LOGGER.trace( "RecipeReplicator::replicateAndRegister()" );

        IForgeRegistry< IRecipe > recipeRegistry = ForgeRegistries.RECIPES;
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

    public static void processRecipeFile( String absFilePath ) throws IOException
    {
        processRecipeStream( new FileInputStream( absFilePath ) );
    }

    public static void processRecipeStream( InputStream stream ) throws IOException
    {
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
