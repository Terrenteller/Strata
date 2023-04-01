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

    public boolean canReplicateIngredientClass( Ingredient ing )
    {
        // Some ingredient classes don't make sense to replicate, like NBT and ores
        return ing instanceof CompoundIngredient || ing.getClass() == Ingredient.class;
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
            if( canReplicateIngredientClass( ing ) )
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
            if( !canReplicateIngredientClass( ing ) )
            {
                replacedIngredients.add( ing );
                continue;
            }

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
