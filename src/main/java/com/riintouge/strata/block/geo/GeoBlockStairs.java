package com.riintouge.strata.block.geo;

import com.riintouge.strata.Strata;
import net.minecraft.block.BlockStairs;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.ResourceLocation;

public class GeoBlockStairs extends BlockStairs
{
    public GeoBlockStairs( IGeoTileInfo info , IBlockState blockState )
    {
        super( blockState );
        // This is what Forge does for BlockStairs in Block.registerBlocks()
        this.useNeighborBrightness = true;

        ResourceLocation registryName = info.type().stairType().registryName( info.tileSetName() );
        setRegistryName( registryName );
        setUnlocalizedName( registryName.toString() );
        // TODO: Move to new tab
        setCreativeTab( Strata.BLOCK_TAB );
    }
}
