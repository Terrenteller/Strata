package com.riintouge.strata.recipe;

import com.riintouge.strata.Strata;
import com.riintouge.strata.item.ItemHelper;
import com.riintouge.strata.misc.MetaResourceLocation;
import com.riintouge.strata.misc.ObjectAssociator;
import com.riintouge.strata.util.CollectionUtil;
import com.riintouge.strata.util.DebugUtil;
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
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import net.minecraftforge.registries.IForgeRegistry;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.*;
import java.lang.reflect.Field;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public final class CraftingRecipeReplicator
{
    public static final CraftingRecipeReplicator INSTANCE = new CraftingRecipeReplicator();

    private final Map< Class , IReplicator > recipeClassReplicatorMap = new HashMap<>();
    private final CraftingRecipeItemStackAssociator globalAssociations = new CraftingRecipeItemStackAssociator();
    private final Map< String , ObjectAssociator< String > > rawRecipeSpecificAssociations = new HashMap<>();
    private Map< Pattern , CraftingRecipeItemStackAssociator > recipeSpecificAssociations = null;
    private final Set< Pattern > blacklistedRecipes = new HashSet<>();
    private final Field shapedOreRecipeMirroredField;

    private CraftingRecipeReplicator()
    {
        Field tempShapedOreRecipeMirroredField;
        try
        {
            tempShapedOreRecipeMirroredField = ReflectionUtil.findFieldByType( ShapedOreRecipe.class , boolean.class , true );
            tempShapedOreRecipeMirroredField.setAccessible( true );
        }
        catch( Exception e )
        {
            Strata.LOGGER.error( DebugUtil.prettyPrintThrowable( e , "Caught %s while acquiring the 'mirrored' field on ShapedOreRecipe!" ) );
            tempShapedOreRecipeMirroredField = null;
        }
        this.shapedOreRecipeMirroredField = tempShapedOreRecipeMirroredField;

        recipeClassReplicatorMap.put( ShapedRecipes.class , recipe ->
        {
            ShapedRecipes shapedRecipe = (ShapedRecipes)recipe;
            return new ShapedRecipes(
                shapedRecipe.getGroup(),
                shapedRecipe.getRecipeWidth(),
                shapedRecipe.getRecipeHeight(),
                substitute( recipe , shapedRecipe.getIngredients() ),
                shapedRecipe.getRecipeOutput() );
        } );

        recipeClassReplicatorMap.put( ShapedOreRecipe.class , recipe ->
        {
            ShapedOreRecipe shapedOreRecipe = (ShapedOreRecipe)recipe;
            CraftingHelper.ShapedPrimer primer = new CraftingHelper.ShapedPrimer();
            primer.width = shapedOreRecipe.getRecipeWidth();
            primer.height = shapedOreRecipe.getRecipeHeight();
            primer.input = substitute( recipe , shapedOreRecipe.getIngredients() );

            if( shapedOreRecipeMirroredField != null )
            {
                try
                {
                    primer.mirrored = (boolean)shapedOreRecipeMirroredField.get( shapedOreRecipe );
                }
                catch( Exception e )
                {
                    Strata.LOGGER.error( DebugUtil.prettyPrintThrowable( e , null ) );
                    primer.mirrored = false;
                }
            }

            return new ShapedOreRecipe(
                new ResourceLocation( shapedOreRecipe.getGroup() ),
                shapedOreRecipe.getRecipeOutput(),
                primer );
        } );

        recipeClassReplicatorMap.put( ShapelessRecipes.class , recipe ->
        {
            ShapelessRecipes shapelessRecipe = (ShapelessRecipes)recipe;
            return new ShapelessRecipes(
                shapelessRecipe.getGroup(),
                shapelessRecipe.getRecipeOutput(),
                substitute( recipe , shapelessRecipe.getIngredients() ) );
        } );

        recipeClassReplicatorMap.put( ShapelessOreRecipe.class , recipe ->
        {
            ShapelessOreRecipe shapelessRecipe = (ShapelessOreRecipe)recipe;
            return new ShapelessOreRecipe(
                new ResourceLocation( shapelessRecipe.getGroup() ),
                substitute( recipe , shapelessRecipe.getIngredients() ),
                shapelessRecipe.getRecipeOutput() );
        } );
    }

    public void associate( ItemStack original , ItemStack alternative )
    {
        globalAssociations.associate( original , alternative );
    }

    public void associateWithIn( String itemRegistryName , String originalItemRegistryName , String recipeRegistryNameRegex )
    {
        ObjectAssociator< String > equivalentItemGroups = rawRecipeSpecificAssociations.computeIfAbsent( recipeRegistryNameRegex , x -> new ObjectAssociator<>() );
        equivalentItemGroups.associate( originalItemRegistryName , itemRegistryName );

        recipeSpecificAssociations = null;
    }

    public Collection< ItemStack > getAssociatedItems( ItemStack itemStack )
    {
        return globalAssociations.getAssociations( itemStack )
            .stream()
            .map( x -> x.itemStack )
            .collect( Collectors.toSet() );
    }

    public void blacklistRecipe( String recipeResourceLocationString )
    {
        blacklistedRecipes.add( Pattern.compile( recipeResourceLocationString ) );
    }

    public boolean isRecipeBlacklisted( String recipeResourceLocationString )
    {
        return blacklistedRecipes.stream().anyMatch( x -> x.matcher( recipeResourceLocationString ).matches() );
    }

    public boolean canReplicateIngredient( Ingredient ing )
    {
        return ing != Ingredient.EMPTY && !( ing instanceof IngredientNBT );
    }

    public boolean shouldReplicate( IRecipe recipe )
    {
        ResourceLocation recipeResourceLocation = recipe.getRegistryName();
        if( recipeResourceLocation.getResourceDomain().equals( Strata.MOD_ID )
            || isRecipeBlacklisted( recipeResourceLocation.toString() )
            || isRecipeBlacklisted( replicatedResourceLocation( recipeResourceLocation ).toString() ) )
        {
            return false;
        }

        rebuildRecipeSpecificAssociationsIfNecessary();
        String recipeRegistryName = recipeResourceLocation.toString();
        List< CraftingRecipeItemStackAssociator > associators = new ArrayList<>();
        associators.add( globalAssociations );

        for( Pair< Pattern , CraftingRecipeItemStackAssociator > pair : CollectionUtil.inPairs( recipeSpecificAssociations ) )
        {
            Matcher matcher = pair.getKey().matcher( recipeRegistryName );
            if( matcher.matches() )
                associators.add( pair.getValue() );
        }

        for( Ingredient ing : recipe.getIngredients() )
            if( canReplicateIngredient( ing ) )
                for( CraftingRecipeItemStackAssociator associator : associators )
                    if( associator.hasExtendedAssociations( ing ) )
                        return true;

        return false;
    }

    @Nullable
    public IRecipe replicate( IRecipe recipe )
    {
        if( !shouldReplicate( recipe ) )
            return null;

        IReplicator replicator = recipeClassReplicatorMap.get( recipe.getClass() );
        if( replicator == null )
        {
            Strata.LOGGER.error(
                String.format(
                    "No recipe replicator for '%s' of type '%s'!",
                    recipe.getRegistryName().toString(),
                    recipe.getClass().toString() ) );

            return null;
        }

        return replicator.replicate( recipe ).setRegistryName( replicatedResourceLocation( recipe.getRegistryName() ) );
    }

    @Nonnull
    public ResourceLocation replicatedResourceLocation( ResourceLocation resourceLocation )
    {
        if( resourceLocation.getResourceDomain().equals( Strata.MOD_ID ) )
            throw new IllegalArgumentException( "resourceLocation" );

        return Strata.resource( String.format( "%s_%s" , resourceLocation.getResourceDomain() , resourceLocation.getResourcePath() ) );
    }

    protected void rebuildRecipeSpecificAssociationsIfNecessary()
    {
        if( recipeSpecificAssociations != null )
            return;

        recipeSpecificAssociations = new HashMap<>();

        for( Pair< String , ObjectAssociator< String > > pair : CollectionUtil.inPairs( rawRecipeSpecificAssociations ) )
        {
            CraftingRecipeItemStackAssociator associator = new CraftingRecipeItemStackAssociator();

            for( Collection< String > equivalentItemGroup : pair.getValue().allAssociations() )
            {
                ItemStack firstItemStack = null;

                for( String metaResourceLocationString : equivalentItemGroup )
                {
                    MetaResourceLocation metaResourceLocation = new MetaResourceLocation( metaResourceLocationString );
                    ItemStack itemStack = ItemHelper.metaResourceLocationToItemStack( metaResourceLocation );
                    if( itemStack == null )
                    {
                        for( ModContainer mod : Loader.instance().getActiveModList() )
                        {
                            if( metaResourceLocation.resourceLocation.getResourceDomain().equalsIgnoreCase( mod.getModId() ) )
                            {
                                Strata.LOGGER.error(
                                    String.format(
                                        "Failed to resolve '%s' for recipe replication!",
                                        metaResourceLocationString ) );

                                break;
                            }
                        }

                        continue;
                    }

                    if( firstItemStack == null )
                        firstItemStack = itemStack;
                    else
                        associator.associate( firstItemStack , itemStack );
                }
            }

            if( associator.groups() > 0 )
                recipeSpecificAssociations.put( Pattern.compile( pair.getKey() ) , associator );
        }
    }

    protected Ingredient getReplicatedIngredientOrOriginal( IRecipe recipe , Ingredient original )
    {
        if( !canReplicateIngredient( original ) )
            return original;

        List< Ingredient > matchingIngredients = new ArrayList<>();
        matchingIngredients.add( original );
        matchingIngredients.addAll( globalAssociations.getExtendingIngredients( original ) );

        String recipeRegistryNameString = recipe.getRegistryName().toString();
        rebuildRecipeSpecificAssociationsIfNecessary();

        for( Pair< Pattern , CraftingRecipeItemStackAssociator > pair : CollectionUtil.inPairs( recipeSpecificAssociations ) )
        {
            Matcher matcher = pair.getKey().matcher( recipeRegistryNameString );
            if( matcher.matches() )
                matchingIngredients.addAll( pair.getValue().getExtendingIngredients( original ) );
        }

        return matchingIngredients.size() == 1
            ? matchingIngredients.get( 0 )
            : new ReplicatedCompoundIngredient( matchingIngredients );
    }

    @Nonnull
    protected NonNullList< Ingredient > substitute( IRecipe recipe , NonNullList< Ingredient > ings )
    {
        NonNullList< Ingredient > replacedIngredients = NonNullList.create();
        for( Ingredient ing : ings )
            replacedIngredients.add( getReplicatedIngredientOrOriginal( recipe , ing ) );

        return replacedIngredients;
    }

    // Statics

    public static void replicateAndRegister()
    {
        IForgeRegistry< IRecipe > recipeRegistry = ForgeRegistries.RECIPES;
        List< IRecipe > copies = new ArrayList<>();

        for( Map.Entry< ResourceLocation , IRecipe > recipe : recipeRegistry.getEntries() )
        {
            IRecipe copy = CraftingRecipeReplicator.INSTANCE.replicate( recipe.getValue() );
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
                    INSTANCE.blacklistRecipe( kv[ 1 ] );
                    break;
            }
        }

        buffer.close();
    }

    // Interfaces

    private interface IReplicator
    {
        IRecipe replicate( IRecipe recipe );
    }

    // Nested classes

    private class ReplicatedCompoundIngredient extends CompoundIngredient
    {
        public ReplicatedCompoundIngredient( Collection< Ingredient > children )
        {
            // CompoundIngredient's constructor is protected for no good reason
            super( children );
        }
    }
}
