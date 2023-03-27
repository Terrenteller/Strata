package com.riintouge.strata.block.ore;

import com.riintouge.strata.block.SpecialBlockPropertyFlags;
import com.riintouge.strata.util.FlagUtil;
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
    protected Block sampleBlock;
    protected ItemBlock sampleItemBlock;

    public OreTileSet( IOreInfo oreInfo )
    {
        this.oreInfo = oreInfo;

        if( FlagUtil.check( oreInfo.specialBlockPropertyFlags() , SpecialBlockPropertyFlags.ACTIVATABLE ) )
        {
            if( "oreRedstone".equals( oreInfo.blockOreDictionaryName() ) )
                block = new RedstoneOreBlock( oreInfo );
            else
                block = new ActivatableOreBlock( oreInfo );
        }
        else
            block = new OreBlock( oreInfo );

        itemBlock = new OreItemBlock( oreInfo , block );
        sampleBlock = new OreSampleBlock( oreInfo );
        sampleItemBlock = new OreItemBlock( oreInfo , sampleBlock );

        // Ores with proxies should never drop this item (rather the drops of the proxy ore), but it should
        // always be created so if an ore un-proxies it doesn't lead to missing registry entries on world load.
        // Also makes for a dependable fallback.
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
    public Block getSampleBlock()
    {
        return sampleBlock;
    }

    @Nonnull
    @Override
    public ItemBlock getSampleItemBlock()
    {
        return sampleItemBlock;
    }

    @Nonnull
    @Override
    public Item getItem()
    {
        return item;
    }
}
