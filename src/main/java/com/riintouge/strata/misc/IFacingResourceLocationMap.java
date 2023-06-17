package com.riintouge.strata.misc;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface IFacingResourceLocationMap
{
    @Nonnull
    ResourceLocation get( EnumFacing facing );

    @Nullable
    ResourceLocation getOrDefault( EnumFacing facing , ResourceLocation defaultValue );
}
