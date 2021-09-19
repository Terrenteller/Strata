package com.riintouge.strata.block.ore;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;

import javax.annotation.Nonnull;

public interface IOreTileSet
{
    @Nonnull
    IOreInfo getInfo();

    @Nonnull
    Block getBlock();

    @Nonnull
    ItemBlock getItemBlock();

    @Nonnull
    Item getItem();
}
