package com.riintouge.strata.block.ore;

import net.minecraftforge.common.property.IUnlistedProperty;

public class UnlistedPropertyActiveState implements IUnlistedProperty< Boolean >
{
    public static UnlistedPropertyActiveState PROPERTY = new UnlistedPropertyActiveState();
    public static boolean DEFAULT = false;

    // IUnlistedProperty overrides

    @Override
    public String getName()
    {
        return "UnlistedPropertyActiveState";
    }

    @Override
    public boolean isValid( Boolean value )
    {
        return true;
    }

    @Override
    public Class< Boolean > getType()
    {
        return Boolean.class;
    }

    @Override
    public String valueToString( Boolean value )
    {
        return value.toString();
    }
}
