package com.riintouge.strata.item;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.init.Enchantments;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;

import java.util.Random;

public class VanillaDropFormula implements IDropFormula
{
    public final int minimum;
    public final int maximum;

    public VanillaDropFormula( int minimum , int maximum )
    {
        this.minimum = minimum;
        this.maximum = maximum;
    }

    public int getAmount( Random random , ItemStack harvestTool , BlockPos pos )
    {
        float dropAmount = minimum + random.nextInt( ( maximum - minimum ) + 1 ); // +1 to offset exclusion
        float multiplier = 1.0f;

        int fortuneLevel = EnchantmentHelper.getEnchantmentLevel( Enchantments.SILK_TOUCH , harvestTool );
        if( fortuneLevel > 0 && random.nextFloat() > ( 2.0f / ( (float)fortuneLevel + 2.0f ) ) )
            multiplier += 1.0f + (float)random.nextInt( fortuneLevel );

        return Math.round( dropAmount * multiplier );
    }
}
