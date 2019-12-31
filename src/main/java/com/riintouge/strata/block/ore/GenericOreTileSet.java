package com.riintouge.strata.block.ore;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;

public class GenericOreTileSet implements IOreTileSet
{
    protected IOreInfo oreInfo;
    protected Block block;
    protected Item item;

    public GenericOreTileSet( IOreInfo oreInfo )
    {
        this.oreInfo = oreInfo;

        // Material values aren't constant so can't use switch
        // TODO: If GenericOreBlock takes on a host affected by gravity, it should replace itself
        if( oreInfo.material() == Material.SAND )
            block = new GenericSandOreBlock( oreInfo );
        else
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
