package com.riintouge.strata.block;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;

public interface IFacingTextureMap
{
    @Nonnull
    ResourceLocation getOrDefault( EnumFacing facing );
}
