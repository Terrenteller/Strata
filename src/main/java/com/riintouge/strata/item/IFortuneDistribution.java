package com.riintouge.strata.item;

import java.util.Random;

public interface IFortuneDistribution
{
    int getAmount( Random random , int fortuneLevel );
}
