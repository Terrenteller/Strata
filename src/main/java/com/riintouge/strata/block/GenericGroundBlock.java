package com.riintouge.strata.block;

import com.riintouge.strata.Strata;
import net.minecraft.block.Block;

public class GenericGroundBlock extends Block
{
    protected IGenericTileSetInfo tileSetInfo;

    public GenericGroundBlock( IGenericTileSetInfo tileSetInfo )
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
}
