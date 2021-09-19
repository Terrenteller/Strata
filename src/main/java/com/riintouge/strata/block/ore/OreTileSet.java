package com.riintouge.strata.block.ore;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;

import javax.annotation.Nonnull;

public class OreTileSet implements IOreTileSet
{
    protected IOreInfo oreInfo;
    protected Block block;
    protected ItemBlock itemBlock;
    protected Item item;

    public OreTileSet( IOreInfo oreInfo )
    {
        this.oreInfo = oreInfo;

        block = "oreRedstone".equals( oreInfo.blockOreDictionaryName() )
            ? new RedstoneOreBlock( oreInfo )
            : new OreBlock( oreInfo );
        itemBlock = new OreItemBlock( oreInfo , block );
        // Ores with proxies should never drop this item (rather the drops of the proxy ore), but it should
        // always be created so if an ore becomes a proxy it doesn't lead to missing registry entries on world load.
        item = new OreItem( oreInfo );
    }

    // IOreTileSet overrides

    @Nonnull
    @Override
    public IOreInfo getInfo()
    {
        return oreInfo;
    }

    @Nonnull
    @Override
    public Block getBlock()
    {
        return block;
    }

    @Nonnull
    @Override
    public ItemBlock getItemBlock()
    {
        return itemBlock;
    }

    @Nonnull
    @Override
    public Item getItem()
    {
        return item;
    }
}
