package com.riintouge.strata.item;

import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Random;

public interface IDropFormula
{
    int getAmount( @Nonnull Random random , @Nullable ItemStack harvestTool , @Nullable BlockPos pos );
}
