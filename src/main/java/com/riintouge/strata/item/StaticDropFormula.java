package com.riintouge.strata.item;

import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Random;

public class StaticDropFormula implements IDropFormula
{
    public final int amount;

    public StaticDropFormula( int amount )
    {
        this.amount = amount;
    }

    // IDropFormula overrides

    @Override
    public int getAmount( @Nonnull Random random , @Nullable ItemStack harvestTool , @Nullable BlockPos pos )
    {
        return Math.max( 0 , amount );
    }
}
