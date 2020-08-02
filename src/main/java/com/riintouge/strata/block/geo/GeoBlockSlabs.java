package com.riintouge.strata.block.geo;

public class GeoBlockSlabs extends GeoBlockSlab
{
    public GeoBlockSlabs( IGeoTileInfo info , GeoBlockSlab singleSlab )
    {
        super( info , singleSlab );
    }

    // BlockSlab overrides

    @Override
    public boolean isDouble()
    {
        return true;
    }
}
