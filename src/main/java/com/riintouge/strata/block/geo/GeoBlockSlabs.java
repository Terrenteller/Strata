package com.riintouge.strata.block.geo;

public class GeoBlockSlabs extends GeoBlockSlab
{
    public GeoBlockSlabs( IGeoTileInfo tileInfo , GeoBlockSlab singleSlab )
    {
        super( tileInfo , singleSlab );
    }

    // BlockSlab overrides

    @Override
    public boolean isDouble()
    {
        return true;
    }
}
