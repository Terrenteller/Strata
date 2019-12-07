package com.riintouge.strata.block.ore;

import com.riintouge.strata.item.ore.GenericStoneOreItemBlock;

public class GenericOreTileSet
{
    public IOreInfo oreInfo;
    public GenericStoneOreBlock block;
    public GenericStoneOreItemBlock blockItem;

    public GenericOreTileSet( IOreInfo oreInfo )
    {
        this.oreInfo = oreInfo;

        block = new GenericStoneOreBlock( oreInfo );
        blockItem = new GenericStoneOreItemBlock( block );
    }
}
