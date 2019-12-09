package com.riintouge.strata.block.ore;

import net.minecraft.block.Block;
import net.minecraft.item.Item;

public interface IOreTileSet
{
    IOreInfo getInfo();

    Block getBlock();

    Item getItem();
}
