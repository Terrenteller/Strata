package com.riintouge.strata.item;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.init.Enchantments;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
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

    // IDropFormula overrides

    @Override
    public int getAmount( @Nonnull Random random , @Nullable ItemStack harvestTool , @Nullable BlockPos pos )
    {
        float dropAmount = minimum + random.nextInt( ( maximum - minimum ) + 1 ); // +1 to offset exclusion
        float multiplier = 1.0f;

        if( harvestTool != null )
        {
            int fortuneLevel = EnchantmentHelper.getEnchantmentLevel( Enchantments.FORTUNE , harvestTool );
            if( fortuneLevel > 0 && random.nextFloat() > ( 2.0f / ( (float)fortuneLevel + 2.0f ) ) )
                multiplier += 1.0f + (float)random.nextInt( fortuneLevel );
        }

        return Math.max( 0 , Math.round( dropAmount * multiplier ) );
    }
}
