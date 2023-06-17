package com.riintouge.strata.misc;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface IMultiFacingResourceMap< T extends Enum >
{
    @Nonnull
    T getActualMultiFacing( T multiFacing );

    @Nonnull
    T getActualMultiFacing( EnumFacing facing );

    @Nonnull
    ResourceLocation get( T multiFacing );

    @Nullable
    ResourceLocation getOrDefault( T multiFacing , ResourceLocation defaultValue );
}
