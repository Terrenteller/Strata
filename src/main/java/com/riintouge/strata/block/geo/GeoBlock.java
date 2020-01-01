package com.riintouge.strata.block.geo;

import com.riintouge.strata.Strata;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;

import java.util.Random;

public class GeoBlock extends Block
{
    protected IGeoTileInfo info;

    public GeoBlock( IGeoTileInfo info )
    {
        super( info.material() );
        this.info = info;

        ResourceLocation registryName = info.registryName();
        setRegistryName( registryName );
        setUnlocalizedName( registryName.toString() );
        setCreativeTab( Strata.BLOCK_TAB );

        setHarvestLevel( info.harvestTool() , info.harvestLevel() );
        setSoundType( info.soundType() );
        setHardness( info.hardness() );
        setResistance( info.explosionResistance() );
    }

    // Block overrides

    @Override
    public Item getItemDropped( IBlockState state , Random rand , int fortune )
    {
        try
        {
            switch( info.type() )
            {
                case STONE:
                    return Item.REGISTRY.getObject( TileType.COBBLE.registryName( info.registryName() ) );
                case CLAY:
                    // TODO: Drop clay globs
                default: { }
            }
        }
        catch( NullPointerException e )
        {
            // No special drop
        }

        return super.getItemDropped( state , rand , fortune );
    }
}
