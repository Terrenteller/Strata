package com.riintouge.strata.block.geo;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;

public class GenericBlockItemPair
{
    private Block block;
    private ItemBlock itemBlock;

    public GenericBlockItemPair( Block block , ItemBlock itemBlock )
    {
        this.block = block;
        this.itemBlock = itemBlock;
    }

    public Block getBlock()
    {
        return block;
    }

    public ItemBlock getItem()
    {
        return itemBlock;
    }
}
