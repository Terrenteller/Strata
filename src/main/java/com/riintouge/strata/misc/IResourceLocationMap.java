package com.riintouge.strata.misc;

import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;

public interface IResourceLocationMap
{
    @Nullable
    ResourceLocation get( @Nullable ResourceLocation originalResource );

    @Nullable
    ResourceLocation getOrDefault( @Nullable ResourceLocation originalResource , @Nullable ResourceLocation defaultValue );

    @Nonnull
    Collection< ResourceLocation > getAll();
}
