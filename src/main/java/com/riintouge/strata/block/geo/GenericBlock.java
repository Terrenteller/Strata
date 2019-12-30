package com.riintouge.strata.block.geo;

import com.riintouge.strata.Strata;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;

import java.util.Random;

public class GenericBlock extends Block
{
    protected IGenericTile tile;

    public GenericBlock( IGenericTile tile )
    {
        super( tile.material() );
        this.tile = tile;

        ResourceLocation registryName = tile.registryName();
        setRegistryName( registryName );
        setUnlocalizedName( registryName.toString() );
        setCreativeTab( Strata.BLOCK_TAB );

        setHarvestLevel( tile.harvestTool() , tile.harvestLevel() );
        setSoundType( tile.soundType() );
        setHardness( tile.hardness() );
        setResistance( tile.explosionResistance() );
    }

    // Block overrides

    @Override
    public Item getItemDropped( IBlockState state , Random rand , int fortune )
    {
        try
        {
            switch( tile.type() )
            {
                case STONE:
                    return Item.REGISTRY.getObject( TileType.COBBLE.registryName( tile.registryName() ) );
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
