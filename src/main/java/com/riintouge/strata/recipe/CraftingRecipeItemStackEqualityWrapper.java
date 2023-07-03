package com.riintouge.strata.recipe;

import net.minecraft.item.ItemStack;

public class CraftingRecipeItemStackEqualityWrapper
{
    public ItemStack itemStack;

    public CraftingRecipeItemStackEqualityWrapper( ItemStack itemStack )
    {
        // We want to compare everything except stack sizes
        if( itemStack.getCount() != 1 )
        {
            this.itemStack = itemStack.copy();
            this.itemStack.setCount( 1 );
        }
        else
            this.itemStack = itemStack;
    }

    // Object overrides

    @Override
    public boolean equals( Object other )
    {
        return other instanceof CraftingRecipeItemStackEqualityWrapper
            && ItemStack.areItemStacksEqual( itemStack , ( (CraftingRecipeItemStackEqualityWrapper)other ).itemStack );
    }

    @Override
    public int hashCode()
    {
        return itemStack.hashCode();
    }
}
