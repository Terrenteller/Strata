package com.riintouge.strata;

import com.riintouge.strata.block.GenericTileSetRegistry;
import com.riintouge.strata.block.geo.tileset.GenericStoneTileSet;
import com.riintouge.strata.block.geo.StoneBlockType;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;

public class StrataBlockTab extends CreativeTabs
{
    public StrataBlockTab()
    {
        super( "strataBlocksTab" );
    }

    @Override
    public ItemStack getTabIconItem()
    {
        GenericStoneTileSet tileSet = GenericTileSetRegistry.INSTANCE.find( "schist" , GenericStoneTileSet.class );
        return new ItemStack( tileSet.tiles.get( StoneBlockType.STONE ).getBlock() );
    }
}
