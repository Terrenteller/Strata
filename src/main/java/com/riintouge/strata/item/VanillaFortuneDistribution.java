package com.riintouge.strata.item;

import java.util.Random;

public class VanillaFortuneDistribution implements IFortuneDistribution
{
    public final int minimum;
    public final int maximum;

    public VanillaFortuneDistribution( int minimum , int maximum )
    {
        this.minimum = minimum;
        this.maximum = maximum;
    }

    public int getAmount( Random random , int fortuneLevel )
    {
        float dropAmount = minimum + random.nextInt( ( maximum - minimum ) + 1 ); // +1 to offset exclusion
        float multiplier = 1.0f;
        if( fortuneLevel > 0 && random.nextFloat() > ( 2.0f / ( (float)fortuneLevel + 2.0f ) ) )
            multiplier += 1.0f + (float)random.nextInt( fortuneLevel );

        return Math.round( dropAmount * multiplier );
    }
}
