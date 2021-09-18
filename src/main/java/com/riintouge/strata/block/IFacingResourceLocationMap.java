package com.riintouge.strata.block;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;

public interface IFacingResourceLocationMap
{
    @Nonnull
    ResourceLocation get( EnumFacing facing );

    ResourceLocation getOrDefault( EnumFacing facing , ResourceLocation defaultValue );
}
