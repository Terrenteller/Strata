package com.riintouge.strata.block.geo;

import com.riintouge.strata.block.IForgeRegistrable;
import com.riintouge.strata.block.MetaResourceLocation;
import com.riintouge.strata.block.loader.ImmutableTile;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelManager;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
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

public final class HostRegistry
{
    public static final HostRegistry INSTANCE = new HostRegistry();
    public static final int DefaultParticleColor = -16777216; // Taken from BlockFalling

    private Map< ResourceLocation , IHostInfo[] > hostInfos = new HashMap<>();
    private Map< MetaResourceLocation , IBakedModel > hostBakedModelMap = new HashMap<>();

    private HostRegistry()
    {
        // Nothing to do
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

    public IBakedModel getBakedModel( MetaResourceLocation metaResourceLocation )
    {
        // This method is meant for hosts, but works with anything. Oh well.
        // TODO: Have a special cache for this kind of stuff that resets on resource reload events.

        IBakedModel hostModel = hostBakedModelMap.getOrDefault( metaResourceLocation , null );
        if( hostModel == null )
        {
            ModelManager modelManager = Minecraft.getMinecraft()
                .getBlockRendererDispatcher()
                .getBlockModelShapes()
                .getModelManager();

            Block hostBlock = Block.REGISTRY.getObject( metaResourceLocation.resourceLocation );
            Map< IBlockState, ModelResourceLocation > variants = modelManager.getBlockModelShapes()
                .getBlockStateMapper()
                .getVariants( hostBlock );

            ModelResourceLocation hostModelResource = variants.get( hostBlock.getStateFromMeta( metaResourceLocation.meta ) );
            hostModel = modelManager.getModel( hostModelResource );
            hostBakedModelMap.put( metaResourceLocation , hostModel );
        }

        return hostModel;
    }

    // Statics

    public static int getParticleFallingColor( IHostInfo info )
    {
        ResourceLocation textureResourceLocation = info.modelTextureMap().get( EnumFacing.DOWN );
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
                if( hostInfo instanceof ImmutableTile ) // FIXME: We're not supposed to know about tiles here
                    break;
                else if( hostInfo instanceof IForgeRegistrable )
                    ( (IForgeRegistrable)hostInfo ).stitchTextures( textureMap );
    }
}
