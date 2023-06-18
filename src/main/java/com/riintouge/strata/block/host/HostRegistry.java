package com.riintouge.strata.block.host;

import com.riintouge.strata.Strata;
import com.riintouge.strata.misc.IForgeRegistrable;
import com.riintouge.strata.misc.MetaResourceLocation;
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
    private boolean someHostTicksRandomly = false;
    private final Map< ResourceLocation , IHostInfo[] > hostMap = new HashMap<>();

    private HostRegistry()
    {
        // Nothing to do
    }

    public void register( IHostInfo hostInfo ) throws IllegalStateException
    {
        ResourceLocation registryName = hostInfo.registryName();
        int meta = hostInfo.meta();
        if( find( registryName , meta ) != null )
            throw new IllegalStateException( String.format( "Host '%s:%d' already registered!" , registryName , meta ) );

        IHostInfo[] metaInfos = hostMap.computeIfAbsent( registryName , x -> new IHostInfo[ 16 ] );
        metaInfos[ meta ] = hostInfo;

        if( !someHostTicksRandomly && hostInfo.ticksRandomly() )
            someHostTicksRandomly = true;
    }

    public boolean doesAnyHostTickRandomly()
    {
        return someHostTicksRandomly;
    }

    @Nullable
    public IHostInfo find( ResourceLocation registryName , int meta )
    {
        IHostInfo[] metaInfos = hostMap.getOrDefault( registryName , null );
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
        return Collections.unmodifiableMap( hostMap );
    }

    @Nonnull
    public Set< MetaResourceLocation > allHostResources()
    {
        Set< MetaResourceLocation > hostResources = new HashSet<>();

        for( Map.Entry< ResourceLocation , IHostInfo[] > entry : hostMap.entrySet() )
            for( int index = 0 ; index < entry.getValue().length ; index++ )
                if( entry.getValue()[ index ] != null )
                    hostResources.add( new MetaResourceLocation( entry.getKey() , index ) );

        return hostResources;
    }

    // Statics

    @SideOnly( Side.CLIENT )
    @SubscribeEvent( priority = EventPriority.LOWEST )
    public static void stitchTexturesPre( TextureStitchEvent.Pre event )
    {
        Strata.LOGGER.trace( "HostRegistry::stitchTexturesPre()" );

        stitchTextures( event.getMap() , true );
    }

    @SideOnly( Side.CLIENT )
    @SubscribeEvent( priority = EventPriority.LOWEST )
    public static void stitchTexturesPost( TextureStitchEvent.Post event )
    {
        Strata.LOGGER.trace( "HostRegistry::stitchTexturesPost()" );

        stitchTextures( event.getMap() , false );
    }

    @SideOnly( Side.CLIENT )
    private static void stitchTextures( TextureMap textureMap , boolean pre )
    {
        for( IHostInfo[] hostInfos : INSTANCE.hostMap.values() )
            for( IHostInfo hostInfo : hostInfos )
                if( hostInfo instanceof ImmutableTile ) // FIXME: We're not supposed to know about tiles here
                    break;
                else if( hostInfo instanceof IForgeRegistrable )
                    ( (IForgeRegistrable)hostInfo ).stitchTextures( textureMap , pre );
    }
}
