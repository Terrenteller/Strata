package com.riintouge.strata.item;

import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;

import java.util.Random;

public class StaticDropFormula implements IDropFormula
{
    public final int amount;

    public StaticDropFormula( int amount )
    {
        this.amount = amount;
    }

    public int getAmount( Random random , ItemStack harvestTool , BlockPos pos )
    {
        return amount;
    }
}
