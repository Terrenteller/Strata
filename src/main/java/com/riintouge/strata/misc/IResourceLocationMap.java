package com.riintouge.strata.misc;

import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;

public interface IResourceLocationMap
{
    @Nonnull
    ResourceLocation get( ResourceLocation originalResource );

    @Nullable
    ResourceLocation getOrDefault( ResourceLocation originalResource , ResourceLocation defaultValue );

    @Nonnull
    Collection< ResourceLocation > getAll();
}
