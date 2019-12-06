package com.riintouge.strata.block;

import com.riintouge.strata.item.GenericStoneBlockItem;

public class GenericBlockItemPair
{
    private GenericStoneBlock block;
    private GenericStoneBlockItem itemBlock;

    public GenericBlockItemPair( GenericStoneBlock block , GenericStoneBlockItem itemBlock )
    {
        this.block = block;
        this.itemBlock = itemBlock;
    }

    public GenericStoneBlock getBlock()
    {
        return block;
    }

    public GenericStoneBlockItem getItem()
    {
        return itemBlock;
    }
}
