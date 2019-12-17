package com.riintouge.strata.block.ore.tileset;

import com.riintouge.strata.block.ore.info.IOreInfo;
import com.riintouge.strata.block.ore.info.IProxyOreInfo;
import com.riintouge.strata.block.ore.GenericStoneOreItemBlock;
import net.minecraft.block.Block;
import net.minecraft.item.Item;

public class VanillaOreTileSet implements IOreTileSet
{
    protected IProxyOreInfo oreInfo;
    protected Block block;
    protected Item item;

    public VanillaOreTileSet( IProxyOreInfo oreInfo )
    {
        this.oreInfo = oreInfo;

        block = new GenericOreBlock( oreInfo );
        item = new GenericStoneOreItemBlock( block );
    }

    // IOreTileSet overrides

    @Override
    public IOreInfo getInfo()
    {
        return oreInfo;
    }

    @Override
    public Block getBlock()
    {
        return block;
    }

    @Override
    public Item getItem()
    {
        return item;
    }
}
