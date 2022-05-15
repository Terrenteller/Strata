package com.riintouge.strata.item;

import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;

import java.util.Random;

public interface IDropFormula
{
    int getAmount( Random random , ItemStack harvestTool , BlockPos pos );
}
