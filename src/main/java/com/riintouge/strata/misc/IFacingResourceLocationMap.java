package com.riintouge.strata.misc;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;

public interface IFacingResourceLocationMap
{
    @Nullable
    ResourceLocation get( @Nullable EnumFacing facing );

    @Nullable
    ResourceLocation getOrDefault( @Nullable EnumFacing facing , @Nullable ResourceLocation defaultValue );
}
