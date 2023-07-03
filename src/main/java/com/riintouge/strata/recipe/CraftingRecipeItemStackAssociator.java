package com.riintouge.strata.recipe;

import com.riintouge.strata.misc.ObjectAssociator;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class CraftingRecipeItemStackAssociator extends ObjectAssociator< CraftingRecipeItemStackEqualityWrapper >
{
    public CraftingRecipeItemStackAssociator()
    {
        // Nothing to do
    }

    public void associate( ItemStack a , ItemStack b )
    {
        associate( new CraftingRecipeItemStackEqualityWrapper( a ) , new CraftingRecipeItemStackEqualityWrapper( b ) );
    }

    public Collection< CraftingRecipeItemStackEqualityWrapper > getAssociations( ItemStack value )
    {
        return super.getAssociations( new CraftingRecipeItemStackEqualityWrapper( value ) );
    }

    public boolean hasExtendedAssociations( Ingredient ing )
    {
        return getExtendingIngredients( ing ).size() > 0;
    }

    public List< Ingredient > getExtendingIngredients( Ingredient ing )
    {
        List< Ingredient > extendingIngredients = new ArrayList<>();

        for( Collection< CraftingRecipeItemStackEqualityWrapper > group : groups )
        {
            boolean anyMatch = false , allMatch = true;

            for( CraftingRecipeItemStackEqualityWrapper wrapper : group )
            {
                if( ing.apply( wrapper.itemStack ) )
                {
                    anyMatch = true;
                    continue;
                }

                allMatch = false;
                if( anyMatch )
                    break;
            }

            // We only have a meaningful extension if the original ingredient accepts some, but not all,
            // of the items we consider associated. Otherwise, we do not extend it or we are redundant.
            if( anyMatch && !allMatch )
                extendingIngredients.add( ( (IngredientCollection)group ).getIngredient() );
        }

        return extendingIngredients;
    }

    // ObjectAssociator overrides

    @Override
    protected Collection< CraftingRecipeItemStackEqualityWrapper > createEmptyCollection()
    {
        return new IngredientCollection();
    }

    @Override
    protected void makeAssociation(
        CraftingRecipeItemStackEqualityWrapper value,
        Collection< CraftingRecipeItemStackEqualityWrapper > collection )
    {
        ( (IngredientCollection)collection ).ingredient = null;
        super.makeAssociation( value , collection );
    }

    @Override
    protected void mergeAssociations(
        Collection< CraftingRecipeItemStackEqualityWrapper > target,
        Collection< CraftingRecipeItemStackEqualityWrapper > extra )
    {
        ( (IngredientCollection)target ).ingredient = null;
        super.mergeAssociations( target , extra );
    }

    // Nested classes

    private class IngredientCollection extends ArrayList< CraftingRecipeItemStackEqualityWrapper >
    {
        protected Ingredient ingredient;

        public Ingredient getIngredient()
        {
            if( ingredient == null )
                ingredient = Ingredient.fromStacks( stream().map( x -> x.itemStack ).toArray( ItemStack[]::new ) );

            return ingredient;
        }
    }
}
