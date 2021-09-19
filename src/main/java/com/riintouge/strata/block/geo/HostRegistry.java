package com.riintouge.strata.block.geo;

import com.riintouge.strata.block.IForgeRegistrable;
import com.riintouge.strata.block.MetaResourceLocation;
import com.riintouge.strata.block.loader.ImmutableTile;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public final class HostRegistry
{
    public static final HostRegistry INSTANCE = new HostRegistry();

    private Map< ResourceLocation , IHostInfo[] > hostInfos = new HashMap<>();

    private HostRegistry()
    {
        // Nothing to do
    }

    public void register( ResourceLocation registryName , int meta , IHostInfo hostInfo )
    {
        IHostInfo[] metaInfos = hostInfos.getOrDefault( registryName , null );

        if( metaInfos == null )
            hostInfos.put( registryName , metaInfos = new IHostInfo[ 16 ] );

        metaInfos[ meta ] = hostInfo;
    }

    @Nullable
    public IHostInfo find( ResourceLocation registryName , int meta )
    {
        IHostInfo[] metaInfos = hostInfos.getOrDefault( registryName , null );
        return metaInfos != null ? metaInfos[ meta ] : null;
    }

    @Nullable
    public IHostInfo find( @Nullable MetaResourceLocation registryNameMeta )
    {
        return registryNameMeta != null ? find( registryNameMeta.resourceLocation , registryNameMeta.meta ) : null;
    }

    @Nonnull
    public Map< ResourceLocation , IHostInfo[] > allHosts()
    {
        return Collections.unmodifiableMap( hostInfos );
    }

    // Statics

    @SideOnly( Side.CLIENT )
    @SubscribeEvent( priority = EventPriority.LOWEST )
    public static void stitchTextures( TextureStitchEvent.Pre event )
    {
        System.out.println( "HostRegistry::stitchTextures()" );

        TextureMap textureMap = event.getMap();
        for( IHostInfo[] hostInfos : INSTANCE.hostInfos.values() )
            for( IHostInfo hostInfo : hostInfos )
                if( hostInfo instanceof ImmutableTile ) // FIXME: We're not supposed to know about tiles here
                    break;
                else if( hostInfo instanceof IForgeRegistrable )
                    ( (IForgeRegistrable)hostInfo ).stitchTextures( textureMap );
    }
}
