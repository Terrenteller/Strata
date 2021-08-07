package com.riintouge.strata.block.geo;

import com.riintouge.strata.Strata;
import net.minecraft.block.BlockLever;
import net.minecraft.util.ResourceLocation;

public class GeoBlockLever extends BlockLever
{
    public GeoBlockLever( IGeoTileInfo info )
    {
        ResourceLocation registryName = info.type().leverType().registryName( info.tileSetName() );
        setRegistryName( registryName );
        setUnlocalizedName( registryName.toString() );
        setCreativeTab( Strata.MISC_ITEM_TAB );

        setHarvestLevel( info.harvestTool() , 0 );
        setSoundType( info.soundType() );
        setHardness( info.hardness() );
        setResistance( info.explosionResistance() );
    }
}