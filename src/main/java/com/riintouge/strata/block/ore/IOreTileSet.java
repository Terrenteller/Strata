package com.riintouge.strata.block.ore;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;

public interface IOreTileSet
{
    IOreInfo getInfo();

    Block getBlock();

    ItemBlock getItemBlock();

    Item getItem();
}
