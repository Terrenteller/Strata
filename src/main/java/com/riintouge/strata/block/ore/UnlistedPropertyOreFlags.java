package com.riintouge.strata.block.ore;

import net.minecraftforge.common.property.IUnlistedProperty;

public class UnlistedPropertyOreFlags implements IUnlistedProperty< Byte >
{
    public static UnlistedPropertyOreFlags PROPERTY = new UnlistedPropertyOreFlags();
    public static final byte DEFAULT = 0;
    public static final byte ACTIVE  = 1 << 0;

    // IUnlistedProperty overrides

    @Override
    public String getName()
    {
        return "UnlistedPropertyOreFlags";
    }

    @Override
    public boolean isValid( Byte value )
    {
        return true;
    }

    @Override
    public Class< Byte > getType()
    {
        return Byte.class;
    }

    @Override
    public String valueToString( Byte value )
    {
        return value.toString();
    }
}
