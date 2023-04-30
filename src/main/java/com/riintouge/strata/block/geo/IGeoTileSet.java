package com.riintouge.strata.block.geo;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;

import javax.annotation.Nullable;

public interface IGeoTileSet
{
    @Nullable
    TileType getPrimaryType();

    @Nullable
    IGeoTileInfo getInfo( @Nullable TileType tileType );

    @Nullable
    Block getBlock( @Nullable TileType tileType );

    @Nullable
    ItemBlock getItemBlock( @Nullable TileType tileType );

    @Nullable
    Item getFragmentItem();

    @Nullable
    Block getSampleBlock();

    @Nullable
    ItemBlock getSampleItemBlock();
}
