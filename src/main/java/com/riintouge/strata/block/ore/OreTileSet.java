package com.riintouge.strata.block.ore;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;

public class OreTileSet implements IOreTileSet
{
    protected IOreInfo oreInfo;
    protected Block block;
    protected ItemBlock itemBlock;
    protected Item item;

    public OreTileSet( IOreInfo oreInfo )
    {
        this.oreInfo = oreInfo;

        // TODO: What if OreBlock takes on a host affected by gravity?
        block = oreInfo.material() == Material.SAND ? new OreBlockFalling( oreInfo ) : new OreBlock( oreInfo );
        itemBlock = new OreItemBlock( oreInfo , block );
        item = oreInfo.proxyBlock() != null ? itemBlock : new OreItem( oreInfo );
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
    public ItemBlock getItemBlock()
    {
        return itemBlock;
    }

    @Override
    public Item getItem()
    {
        return item;
    }
}
