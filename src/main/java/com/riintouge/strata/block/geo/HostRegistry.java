package com.riintouge.strata.block.geo;

import com.riintouge.strata.Strata;
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
import java.util.*;

public final class HostRegistry
{
    public static final HostRegistry INSTANCE = new HostRegistry();

    // This is a minor optimization for ores since randomly ticking them is an all or nothing affair.
    // That is to say, we cannot set an individual ore to tick randomly if and only if it acquires a host which does.
    // Since no host ticks randomly out-of-the-box, no ore needs to either.
    public static boolean ANY_HOST_TICKS = false;

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

        if( !ANY_HOST_TICKS && hostInfo.ticksRandomly() )
            ANY_HOST_TICKS = true;
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

    @Nonnull
    public Set< MetaResourceLocation > allHostResources()
    {
        Set< MetaResourceLocation > hostResources = new HashSet<>();

        for( Map.Entry< ResourceLocation , IHostInfo[] > entry : hostInfos.entrySet() )
            for( int index = 0 ; index < entry.getValue().length ; index++ )
                if( entry.getValue()[ index ] != null )
                    hostResources.add( new MetaResourceLocation( entry.getKey() , index ) );

        return hostResources;
    }

    // Statics

    @SideOnly( Side.CLIENT )
    @SubscribeEvent( priority = EventPriority.LOWEST )
    public static void stitchTextures( TextureStitchEvent.Pre event )
    {
        Strata.LOGGER.trace( "HostRegistry::stitchTextures()" );

        TextureMap textureMap = event.getMap();
        for( IHostInfo[] hostInfos : INSTANCE.hostInfos.values() )
            for( IHostInfo hostInfo : hostInfos )
                if( hostInfo instanceof ImmutableTile ) // FIXME: We're not supposed to know about tiles here
                    break;
                else if( hostInfo instanceof IForgeRegistrable )
                    ( (IForgeRegistrable)hostInfo ).stitchTextures( textureMap );
    }
}
