package com.riintouge.strata.block;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;

public interface IMultiFacingResourceMap< T extends Enum >
{
    @Nonnull
    T getActualMultiFacing( T multiFacing );

    @Nonnull
    T getActualMultiFacing( EnumFacing facing );

    @Nonnull
    ResourceLocation get( T multiFacing );

    ResourceLocation getOrDefault( T multiFacing , ResourceLocation defaultValue );
}
