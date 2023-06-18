package com.riintouge.strata.misc;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;

public interface IMultiFacingResourceMap< T extends Enum >
{
    @Nullable
    T getActualMultiFacing( @Nullable T multiFacing );

    @Nullable
    T getActualMultiFacing( @Nullable EnumFacing facing );

    @Nullable
    ResourceLocation get( @Nullable T multiFacing );

    @Nullable
    ResourceLocation getOrDefault( @Nullable T multiFacing , @Nullable ResourceLocation defaultValue );
}
