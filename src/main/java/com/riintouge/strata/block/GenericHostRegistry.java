package com.riintouge.strata.block;

import com.riintouge.strata.block.geo.IHostInfo;
import net.minecraft.util.ResourceLocation;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class GenericHostRegistry
{
    public static final GenericHostRegistry INSTANCE = new GenericHostRegistry();

    private Map< ResourceLocation , IHostInfo[] > hostInfos = new HashMap<>();

    private GenericHostRegistry()
    {
    }

    public void register( ResourceLocation registryName , int meta , IHostInfo info )
    {
        IHostInfo[] metaInfos = hostInfos.getOrDefault( registryName , null );

        if( metaInfos == null )
            hostInfos.put( registryName , metaInfos = new IHostInfo[ 16 ] );

        metaInfos[ meta ] = info;
    }

    public IHostInfo find( ResourceLocation registryName , int meta )
    {
        IHostInfo[] metaInfos = hostInfos.getOrDefault( registryName , null );
        return metaInfos != null ? metaInfos[ meta ] : null;
    }

    public IHostInfo find( MetaResourceLocation registryNameMeta )
    {
        IHostInfo[] metaInfos = hostInfos.getOrDefault( registryNameMeta.resourceLocation , null );
        return metaInfos != null ? metaInfos[ registryNameMeta.meta ] : null;
    }

    public Map< ResourceLocation , IHostInfo[] > allHosts()
    {
        return Collections.unmodifiableMap( hostInfos );
    }
}
