package com.riintouge.strata.block.ore;

import com.riintouge.strata.block.MetaResourceLocation;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.property.IUnlistedProperty;

public class UnlistedPropertyHostRock implements IUnlistedProperty< MetaResourceLocation >
{
    public static UnlistedPropertyHostRock PROPERTY = new UnlistedPropertyHostRock();
    public static final MetaResourceLocation FALLBACK = new MetaResourceLocation( new ResourceLocation( "minecraft" , "stone" ) , 0 );

    // IUnlistedProperty overrides

    @Override
    public String getName()
    {
        return "UnlistedPropertyHostRock";
    }

    @Override
    public boolean isValid( MetaResourceLocation value )
    {
        return true;
    }

    @Override
    public Class< MetaResourceLocation > getType()
    {
        return MetaResourceLocation.class;
    }

    @Override
    public String valueToString( MetaResourceLocation value )
    {
        return value.toString();
    }
}
