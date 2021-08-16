package com.riintouge.strata.block.geo;

import com.riintouge.strata.block.IForgeRegistrable;
import com.riintouge.strata.block.MetaResourceLocation;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class HostRegistry
{
    public static final HostRegistry INSTANCE = new HostRegistry();
    public static final int DefaultParticleColor = -16777216; // Taken from BlockFalling

    private Map< ResourceLocation , IHostInfo[] > hostInfos = new HashMap<>();

    private HostRegistry()
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

    // Statics

    public static int getParticleFallingColor( IHostInfo info )
    {
        // It would be fancy to check if the host block is BlockFalling, but all Strata rocks are BlockFalling
        if( info.material() != Material.SAND )
            return DefaultParticleColor;

        ResourceLocation textureResourceLocation = info.facingTextureMap().getOrDefault( EnumFacing.DOWN );
        TextureAtlasSprite texture = Minecraft.getMinecraft()
            .getTextureMapBlocks()
            .getTextureExtry( textureResourceLocation.toString() );

        if( texture != null )
        {
            // Use the first pixel of the smallest mipmap as the average color
            int[][] frameData = texture.getFrameTextureData( 0 );
            return frameData[ frameData.length - 1 ][ 0 ];
        }

        return DefaultParticleColor;
    }

    @SubscribeEvent( priority = EventPriority.LOWEST )
    public static void stitchTextures( TextureStitchEvent.Pre event )
    {
        System.out.println( "HostRegistry::stitchTextures()" );

        TextureMap textureMap = event.getMap();
        for( IHostInfo[] hostInfos : INSTANCE.hostInfos.values() )
            for( IHostInfo hostInfo : hostInfos )
                if( hostInfo instanceof IForgeRegistrable )
                    ( (IForgeRegistrable)hostInfo ).stitchTextures( textureMap );
    }
}
