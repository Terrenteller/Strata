package com.riintouge.strata.property;

import net.minecraft.block.properties.PropertyEnum;

public interface IPropertyEnumProvider
{
    PropertyEnum property();

    IPropertyEnumValue[] propertyValues();
}
