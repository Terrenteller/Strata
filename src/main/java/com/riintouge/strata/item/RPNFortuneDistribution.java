package com.riintouge.strata.item;

import com.riintouge.strata.util.Util;
import org.apache.commons.lang3.tuple.ImmutablePair;

import javax.annotation.Nullable;
import java.util.Random;

public class RPNFortuneDistribution implements IFortuneDistribution
{
    public final int baseAmount;
    public final String expr;
    protected final int[] precomputedValues = new int[ 4 ];

    public RPNFortuneDistribution( int baseAmount , @Nullable String expr )
    {
        this.baseAmount = baseAmount;
        this.expr = expr;
        for( int index = 0 ; index < precomputedValues.length ; index++ )
        {
            double rpnResult = Util.evaluateRPN( expr , new ImmutablePair<>( "f" , (double)index ) );
            precomputedValues[ index ] = (int)Math.round( rpnResult );
        }
    }

    public int getAmount( Random random , int fortuneLevel )
    {
        int maxExtra = 0;
        if( fortuneLevel < precomputedValues.length )
            maxExtra = precomputedValues[ fortuneLevel ];

        return baseAmount + random.nextInt( maxExtra + 1 ); // +1 to offset exclusion
    }
}
