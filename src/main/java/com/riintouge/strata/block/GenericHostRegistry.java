package com.riintouge.strata.block;

import com.riintouge.strata.block.geo.IGenericBlockProperties;
import net.minecraft.util.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

public class GenericHostRegistry
{
    public static final GenericHostRegistry INSTANCE = new GenericHostRegistry();

    private Map< ResourceLocation , IGenericBlockProperties[] > hostProps = new HashMap<>();

    private GenericHostRegistry()
    {
    }

    public void register( ResourceLocation registryName , int meta , IGenericBlockProperties props )
    {
        IGenericBlockProperties[] metaProps = hostProps.getOrDefault( registryName , null );

        if( metaProps == null )
            hostProps.put( registryName , metaProps = new IGenericBlockProperties[ 16 ] );

        metaProps[ meta ] = props;
    }

    public IGenericBlockProperties find( ResourceLocation registryName , int meta )
    {
        IGenericBlockProperties[] metaProps = hostProps.getOrDefault( registryName , null );
        return metaProps != null ? metaProps[ meta ] : null;
    }

    public IGenericBlockProperties find( MetaResourceLocation registryNameVariant )
    {
        IGenericBlockProperties[] metaProps = hostProps.getOrDefault( registryNameVariant.resourceLocation , null );
        return metaProps != null ? metaProps[ registryNameVariant.meta ] : null;
    }
}
