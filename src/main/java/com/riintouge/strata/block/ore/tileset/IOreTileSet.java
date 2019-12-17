package com.riintouge.strata.block.ore.tileset;

import com.riintouge.strata.block.ore.info.IOreInfo;
import net.minecraft.block.Block;
import net.minecraft.item.Item;

public interface IOreTileSet
{
    IOreInfo getInfo();

    Block getBlock();

    Item getItem();
}
