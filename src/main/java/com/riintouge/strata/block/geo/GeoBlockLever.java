package com.riintouge.strata.block.geo;

import com.riintouge.strata.Strata;
import net.minecraft.block.BlockLever;
import net.minecraft.util.ResourceLocation;

public class GeoBlockLever extends BlockLever
{
    public GeoBlockLever( IGeoTileInfo info )
    {
        ResourceLocation registryName = info.registryName();
        setRegistryName( registryName );
        setUnlocalizedName( registryName.toString() );
        setCreativeTab( Strata.MISC_BLOCK_TAB );

        setHarvestLevel( info.harvestTool() , 0 );
        setSoundType( info.soundType() );
        setHardness( info.hardness() );
        setResistance( info.explosionResistance() );
    }
}
