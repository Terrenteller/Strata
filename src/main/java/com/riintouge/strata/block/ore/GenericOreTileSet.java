package com.riintouge.strata.block.ore;

import com.riintouge.strata.item.ore.GenericStoneOreItemBlock;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;

public class GenericOreTileSet implements IOreTileSet
{
    public IOreInfo oreInfo;
    public Block block;
    public Item item;

    public GenericOreTileSet( IOreInfo oreInfo )
    {
        this.oreInfo = oreInfo;

        // Material values aren't constant so can't use switch
        if( oreInfo.material() == Material.SAND )
            block = new GenericSandOreBlock( oreInfo );
        else
            block = new GenericStoneOreBlock( oreInfo );

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
