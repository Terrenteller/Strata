package com.riintouge.strata.block;

import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import java.util.Collection;

public interface IResourceLocationMap
{
    @Nonnull
    ResourceLocation get( ResourceLocation resourceLocation );

    ResourceLocation getOrDefault( ResourceLocation resourceLocation , ResourceLocation defaultValue );

    @Nonnull
    Collection< ResourceLocation > getAll();
}
