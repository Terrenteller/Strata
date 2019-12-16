package com.riintouge.strata;

import com.riintouge.strata.block.IGenericTileSetInfo;
import net.minecraft.util.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

public class GenericHostRegistry
{
    public static final GenericHostRegistry INSTANCE = new GenericHostRegistry();

    private Map< ResourceLocation , IGenericTileSetInfo[] > hostInfos = new HashMap<>();

    private GenericHostRegistry()
    {
    }

    public void register( ResourceLocation registryName , int meta , IGenericTileSetInfo info )
    {
        IGenericTileSetInfo[] infos;

        if( !hostInfos.containsKey( registryName ) )
            hostInfos.put( registryName , infos = new IGenericTileSetInfo[ 16 ] );
        else
            infos = hostInfos.get( registryName );

        infos[ meta ] = info;
    }

    public IGenericTileSetInfo find( ResourceLocation registryName , int meta )
    {
        IGenericTileSetInfo[] infos = hostInfos.getOrDefault( registryName , null );
        return infos != null ? infos[ meta ] : null;
    }

    public IGenericTileSetInfo find( MetaResourceLocation registryNameVariant )
    {
        IGenericTileSetInfo[] infos = hostInfos.getOrDefault( registryNameVariant.resourceLocation , null );
        return infos != null ? infos[ registryNameVariant.meta ] : null;
    }

    public boolean contains( ResourceLocation registryName , int meta )
    {
        return find( registryName , meta ) != null;
    }
}
