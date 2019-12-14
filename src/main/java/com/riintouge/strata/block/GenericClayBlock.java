package com.riintouge.strata.block;

import com.riintouge.strata.GenericTileSetRegistry;
import com.riintouge.strata.Strata;
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
        setHardness( 3f );
        setResistance( 5f );

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
