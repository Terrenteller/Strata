package com.riintouge.strata.block.geo;

import net.minecraft.util.ResourceLocation;

public enum TileType
{
    CLAY( "%s:%s" ),
    //BRICK( "%s:%s_brick" ),
    GROUND( "%s:%s" ),
    STONE( "%s:%s" ),
    COBBLE( "%s:%s_cobble" ),
    STONEBRICK( "%s:%s_stonebrick" );

    private String resourceLocationFormat;

    TileType( String resourceLocationFormat )
    {
        this.resourceLocationFormat = resourceLocationFormat;
    }

    public ResourceLocation registryName( ResourceLocation registryName )
    {
        return new ResourceLocation( String.format( resourceLocationFormat , registryName.getResourceDomain() , registryName.getResourcePath() ) );
    }

    // TODO: slabs (cobble, stone, brick), walls (cobble, stone, brick), stairs (cobble, stone, brick), etc.
}
