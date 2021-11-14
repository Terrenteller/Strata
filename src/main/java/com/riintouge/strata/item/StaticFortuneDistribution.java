package com.riintouge.strata.item;

import java.util.Random;

public class StaticFortuneDistribution implements IFortuneDistribution
{
    public final int amount;

    public StaticFortuneDistribution( int amount )
    {
        this.amount = amount;
    }

    public int getAmount( Random random , int fortuneLevel )
    {
        return amount;
    }
}
