package com.riintouge.strata.block.geo.tileset;

import com.riintouge.strata.block.GenericTileSetRegistry;
import com.riintouge.strata.Strata;
import com.riintouge.strata.block.geo.info.IGenericTileSetInfo;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;

import java.util.Random;

public class GenericClayBlock extends Block
{
    protected IGenericTileSetInfo tileSetInfo;

    public GenericClayBlock( IGenericTileSetInfo tileSetInfo )
    {
        super( tileSetInfo.material() );
        this.tileSetInfo = tileSetInfo;

        String blockName = tileSetInfo.stoneName();
        setRegistryName( Strata.modid + ":" + blockName );
        setUnlocalizedName( Strata.modid + ":" + blockName );

        setHarvestLevel( tileSetInfo.harvestTool() , tileSetInfo.harvestLevel() );
        setSoundType( tileSetInfo.soundType() );
        setHardness( tileSetInfo.hardness() );
        setResistance( tileSetInfo.explosionResistance() );

        setCreativeTab( Strata.BLOCK_TAB );
    }

    // Block overrides

    @Override
    public Item getItemDropped( IBlockState state , Random rand , int fortune )
    {
        try
        {
            String tileSetName = getRegistryName().getResourcePath();
            GenericClayTileSet tileSet = GenericTileSetRegistry.INSTANCE.find( tileSetName , GenericClayTileSet.class );
            return tileSet.getClayItemBlock();
        }
        catch( NullPointerException e )
        {
            return super.getItemDropped( state , rand , fortune );
        }
    }
}
