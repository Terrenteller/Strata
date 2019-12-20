package com.riintouge.strata.block.geo.tileset;

import com.riintouge.strata.Strata;
import com.riintouge.strata.block.geo.info.IGenericTileSetInfo;
import net.minecraft.block.Block;
import net.minecraft.util.ResourceLocation;

public class GenericGroundBlock extends Block
{
    protected IGenericTileSetInfo tileSetInfo;

    public GenericGroundBlock( IGenericTileSetInfo tileSetInfo )
    {
        super( tileSetInfo.material() );
        this.tileSetInfo = tileSetInfo;

        ResourceLocation registryName = tileSetInfo.registryName();
        setRegistryName( registryName );
        setUnlocalizedName( registryName.toString() );

        setHarvestLevel( tileSetInfo.harvestTool() , tileSetInfo.harvestLevel() );
        setSoundType( tileSetInfo.soundType() );
        setHardness( tileSetInfo.hardness() );
        setResistance( tileSetInfo.explosionResistance() );

        setCreativeTab( Strata.BLOCK_TAB );
    }
}
