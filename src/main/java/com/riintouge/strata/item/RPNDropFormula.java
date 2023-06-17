package com.riintouge.strata.item;

import com.riintouge.strata.misc.RPNExpression;
import com.riintouge.strata.util.StringUtil;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.init.Enchantments;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Random;
import java.util.function.Function;

public class RPNDropFormula implements IDropFormula
{
    public final RPNExpression baseExpr;
    public final RPNExpression bonusExpr;

    public RPNDropFormula( @Nullable String baseExpr , @Nullable String bonusExpr )
    {
        String cleanBaseExpr = baseExpr != null ? baseExpr.trim() : null;
        cleanBaseExpr = !StringUtil.isNullOrEmpty( cleanBaseExpr ) ? cleanBaseExpr : "0";
        this.baseExpr = new RPNExpression( cleanBaseExpr );

        String cleanBonusExpr = bonusExpr != null ? bonusExpr.trim() : null;
        cleanBonusExpr = !StringUtil.isNullOrEmpty( cleanBonusExpr ) ? cleanBonusExpr : "0";
        this.bonusExpr = new RPNExpression( cleanBonusExpr );
    }

    // IDropFormula overrides

    @Override
    public int getAmount( @Nonnull Random random , @Nullable ItemStack harvestTool , @Nullable BlockPos pos )
    {
        Function< String , Double > variableGetter = variable ->
        {
            switch( variable )
            {
                case "f":
                    return harvestTool != null
                        ? (double)EnchantmentHelper.getEnchantmentLevel( Enchantments.FORTUNE , harvestTool )
                        : 0.0;
                case "x":
                    return pos != null ? (double)pos.getX() : 0.0;
                case "y":
                    return pos != null ? (double)pos.getY() : 0.0;
                case "z":
                    return pos != null ? (double)pos.getZ() : 0.0;
            }

            Enchantment enchantment = Enchantment.REGISTRY.getObject( new ResourceLocation( variable ) );
            if( enchantment != null )
            {
                if( harvestTool != null )
                {
                    NBTTagList enchantmentTags = harvestTool.getEnchantmentTagList();
                    for( int index = 0 ; index < enchantmentTags.tagCount() ; index++ )
                    {
                        NBTTagCompound enchantmentTag = enchantmentTags.getCompoundTagAt( index );
                        if( Enchantment.REGISTRY.getIDForObject( enchantment ) == enchantmentTag.getShort( "id" ) )
                            return (double)enchantmentTag.getShort( "lvl" );
                    }
                }

                return 0.0;
            }

            return null;
        };

        int base = Math.max( 0 , (int)Math.round( baseExpr.evaluate( variableGetter ) ) );
        if( bonusExpr == null )
            return base;

        int maxBonus = Math.max( 0 , (int)Math.round( bonusExpr.evaluate( variableGetter ) ) );
        int bonus = random.nextInt( maxBonus + 1 ); // +1 to offset exclusion
        return base + bonus;
    }
}
