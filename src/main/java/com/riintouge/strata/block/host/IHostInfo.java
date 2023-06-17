package com.riintouge.strata.block.host;

import com.riintouge.strata.misc.MetaResourceLocation;
import com.riintouge.strata.block.ProtoBlockTextureMap;
import com.riintouge.strata.block.ICommonBlockProperties;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface IHostInfo extends ICommonBlockProperties
{
    @Nonnull
    ResourceLocation registryName();

    int meta();

    @Nullable
    Float slipperiness();

    @Nullable
    Integer meltsAt();

    @Nullable
    MetaResourceLocation meltsInto();

    @Nullable
    Integer sublimatesAt();

    @Nullable
    MetaResourceLocation sublimatesInto();

    boolean ticksRandomly();

    @SideOnly( Side.CLIENT )
    ProtoBlockTextureMap modelTextureMap();

    @SideOnly( Side.CLIENT )
    int particleFallingColor();
}
