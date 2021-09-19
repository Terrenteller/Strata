package com.riintouge.strata.block;

import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;

public interface IResourceLocationMap
{
    @Nonnull
    ResourceLocation get( ResourceLocation resourceLocation );

    @Nullable
    ResourceLocation getOrDefault( ResourceLocation resourceLocation , ResourceLocation defaultValue );

    @Nonnull
    Collection< ResourceLocation > getAll();
}
