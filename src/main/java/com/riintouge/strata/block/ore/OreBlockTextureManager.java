package com.riintouge.strata.block.ore;

import com.riintouge.strata.Strata;
import com.riintouge.strata.block.GenericCubeTextureMap;
import com.riintouge.strata.block.geo.HostRegistry;
import com.riintouge.strata.block.geo.IHostInfo;
import com.riintouge.strata.image.LayeredTexture;
import com.riintouge.strata.image.LayeredTextureLayer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

public class OreBlockTextureManager
{
    public static final OreBlockTextureManager INSTANCE = new OreBlockTextureManager();

    private static final Logger LOGGER = LogManager.getLogger();
    private boolean alreadyInitializedOnce = false;
    private Map< ResourceLocation , IOreInfo[] > oreInfoMap = new HashMap<>();
    private Map< String , TextureAtlasSprite > generatedTextureMap = new HashMap<>();

    private OreBlockTextureManager()
    {
        // Nothing to do
    }

    public void registerOre( ResourceLocation oreRegistryName , int meta , IOreInfo oreInfo )
    {
        if( alreadyInitializedOnce )
            LOGGER.warn( "register called too late!" );

        IOreInfo[] infos = oreInfoMap.getOrDefault( oreRegistryName , null );
        if( infos == null )
            oreInfoMap.put( oreRegistryName , infos = new IOreInfo[ 16 ] );

        infos[ meta ] = oreInfo;
    }

    public TextureAtlasSprite findTexture(
        String oreDomain,
        String orePath,
        int oreMeta,
        String hostDomain,
        String hostPath,
        int hostMeta,
        EnumFacing facing )
    {
        String resourcePath = getGeneratedResourceLocation( oreDomain , orePath , oreMeta , hostDomain , hostPath , hostMeta , facing.getName2() ).getResourcePath();
        TextureAtlasSprite texture = generatedTextureMap.getOrDefault( resourcePath , null );
        if( texture != null )
            return texture;

        System.out.println( String.format( "No texture was generated for \"%s\"!" , resourcePath ) );
        return Minecraft.getMinecraft().getTextureMapBlocks().getMissingSprite();
    }

    @SubscribeEvent( priority = EventPriority.LOWEST )
    public static void stitchTextures( TextureStitchEvent.Pre event )
    {
        System.out.println( "OreBlockTextureManager::onEvent( TextureStitchEvent.Pre )" );

        TextureMap textureMap = event.getMap();
        Map< ResourceLocation , IHostInfo[] > hostInfoMap = HostRegistry.INSTANCE.allHosts();

        if( hostInfoMap.size() == 0 )
            return;

        for( ResourceLocation ore : INSTANCE.oreInfoMap.keySet() )
        {
            for( ResourceLocation host : hostInfoMap.keySet() )
            {
                IOreInfo[] oreMetaInfos = INSTANCE.oreInfoMap.get( ore );
                IHostInfo[] hostMetaInfos = hostInfoMap.get( host );

                for( int oreMeta = 0 ; oreMeta < oreMetaInfos.length ; oreMeta++ )
                {
                    IOreInfo oreInfo = oreMetaInfos[ oreMeta ];
                    if( oreInfo == null )
                        continue;

                    for( int hostMeta = 0 ; hostMeta < hostMetaInfos.length ; hostMeta++ )
                    {
                        IHostInfo hostInfo = hostMetaInfos[ hostMeta ];
                        if( hostInfo == null )
                            continue;

                        // TODO: Investigate if MultiLayerModel can clean this up once and for all
                        for( GenericCubeTextureMap.Layer layer : GenericCubeTextureMap.Layer.values() )
                        {
                            GenericCubeTextureMap oreTextureMap = oreInfo.modelTextureMap();
                            GenericCubeTextureMap hostTextureMap = hostInfo.modelTextureMap();

                            ResourceLocation oreTextureResource = oreTextureMap.getOrDefault( layer , null );
                            ResourceLocation hostTextureResource = hostTextureMap.getOrDefault( layer , null );

                            if( oreTextureResource == null && hostTextureResource == null )
                                continue;

                            ResourceLocation facingResource = getGeneratedResourceLocation( ore , oreMeta , host , hostMeta , layer.toString().toLowerCase() );
                            System.out.println( "Preparing " + facingResource.toString() );

                            LayeredTextureLayer oreLayer = new LayeredTextureLayer( oreTextureResource != null ? oreTextureResource : oreTextureMap.get( layer ) );
                            LayeredTextureLayer hostLayer = new LayeredTextureLayer( hostTextureResource != null ? hostTextureResource : hostTextureMap.get( layer ) );

                            TextureAtlasSprite generatedTexture = new LayeredTexture(
                                facingResource,
                                new LayeredTextureLayer[] { oreLayer , hostLayer } );
                            textureMap.setTextureEntry( generatedTexture );

                            for( EnumFacing facing : layer.applicableFacings )
                            {
                                ResourceLocation layerResource = getGeneratedResourceLocation( ore , oreMeta , host , hostMeta , facing.getName2() );
                                INSTANCE.generatedTextureMap.put( layerResource.getResourcePath() , generatedTexture );
                            }
                        }
                    }
                }
            }
        }

        INSTANCE.alreadyInitializedOnce = true;
    }

    private static ResourceLocation getGeneratedResourceLocation(
        ResourceLocation ore,
        int oreMeta,
        ResourceLocation host,
        int hostMeta,
        String direction )
    {
        return getGeneratedResourceLocation(
            ore.getResourceDomain(),
            ore.getResourcePath(),
            oreMeta,
            host.getResourceDomain(),
            host.getResourcePath(),
            hostMeta,
            direction );
    }

    private static ResourceLocation getGeneratedResourceLocation(
        String oreDomain,
        String orePath,
        int oreMeta,
        String hostDomain,
        String hostPath,
        int hostMeta,
        String direction )
    {
        // Ex: ore_strata_cinnabar_0_host_minecraft_stone_3_north
        String texturePath = String.format(
            "ore_%s_%s_%d_host_%s_%s_%d_%s",
            oreDomain,
            orePath,
            oreMeta,
            hostDomain,
            hostPath,
            hostMeta,
            direction );
        return new ResourceLocation( Strata.modid , texturePath );
    }
}
