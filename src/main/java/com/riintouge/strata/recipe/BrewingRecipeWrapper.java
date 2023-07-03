package com.riintouge.strata.recipe;

import net.minecraft.item.ItemStack;
import net.minecraftforge.common.brewing.IBrewingRecipe;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class BrewingRecipeWrapper implements IBrewingRecipe
{
    private final IBrewingRecipe originalRecipe;

    public BrewingRecipeWrapper( IBrewingRecipe originalRecipe )
    {
        this.originalRecipe = originalRecipe;
    }

    @Nullable
    public ItemStack getOriginalIngredient( @Nonnull ItemStack input )
    {
        if( originalRecipe.isIngredient( input ) )
            return input;

        for( ItemStack itemStack : CraftingRecipeReplicator.INSTANCE.getAssociatedItems( input ) )
            if( itemStack != input && originalRecipe.isIngredient( itemStack ) )
                return itemStack;

        return null;
    }

    // IBrewingRecipe overrides

    @Override
    @Nonnull
    public ItemStack getOutput( @Nonnull ItemStack input , @Nonnull ItemStack ingredient )
    {
        ItemStack original = getOriginalIngredient( ingredient );
        return original != null ? originalRecipe.getOutput( input , original ) : ItemStack.EMPTY;
    }

    @Override
    public boolean isIngredient( @Nonnull ItemStack stack )
    {
        return getOriginalIngredient( stack ) != null;
    }

    @Override
    public boolean isInput( @Nonnull ItemStack input )
    {
        return originalRecipe.isIngredient( input );
    }
}
