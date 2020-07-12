package com.riintouge.strata.block;

import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import java.util.Collection;

public interface IModelRetexturizerMap
{
    @Nonnull
    ResourceLocation getOrDefault( ResourceLocation modelTextureLocationIn );

    @Nonnull
    Collection< ResourceLocation > getAll();
}
