package com.riintouge.strata.block.ore;

import com.riintouge.strata.Strata;
import com.riintouge.strata.StrataConfig;
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

public class OreParticleTextureManager
{
    public static final OreParticleTextureManager INSTANCE = new OreParticleTextureManager();

    private static final Logger LOGGER = LogManager.getLogger();
    private boolean isInitialized = false;
    private Map< ResourceLocation , IOreInfo > oreInfoMap = new HashMap<>();
    private Map< String , TextureAtlasSprite > generatedTextureMap = new HashMap<>();

    private OreParticleTextureManager()
    {
        // Nothing to do
    }

    public boolean isInitialized()
    {
        return isInitialized;
    }

    public void registerOre( ResourceLocation oreRegistryName , IOreInfo oreInfo )
    {
        oreInfoMap.put( oreRegistryName , oreInfo );
    }

    public TextureAtlasSprite findTexture(
        String orePath,
        String hostDomain,
        String hostPath,
        int hostMeta,
        EnumFacing facing )
    {
        String resourcePath = getGeneratedResourceLocation( orePath , hostDomain , hostPath , hostMeta , facing.getName2() ).getResourcePath();
        TextureAtlasSprite texture = generatedTextureMap.getOrDefault( resourcePath , null );
        if( texture != null )
            return texture;

        //System.out.println( String.format( "No texture was generated for \"%s\"!" , resourcePath ) );
        return Minecraft.getMinecraft().getTextureMapBlocks().getMissingSprite();
    }

    @SubscribeEvent( priority = EventPriority.LOWEST )
    public static void stitchTextures( TextureStitchEvent.Pre event )
    {
        System.out.println( "OreParticleTextureManager::onEvent( TextureStitchEvent.Pre )" );

        if( !StrataConfig.usePrecomputedOreParticles )
            return;

        TextureMap textureMap = event.getMap();
        Map< ResourceLocation , IHostInfo[] > hostInfoMap = HostRegistry.INSTANCE.allHosts();

        if( hostInfoMap.size() == 0 )
            return;

        for( ResourceLocation ore : INSTANCE.oreInfoMap.keySet() )
        {
            IOreInfo oreInfo = INSTANCE.oreInfoMap.get( ore );

            for( ResourceLocation host : hostInfoMap.keySet() )
            {
                IHostInfo[] hostMetaInfos = hostInfoMap.get( host );

                for( int hostMeta = 0 ; hostMeta < hostMetaInfos.length ; hostMeta++ )
                {
                    IHostInfo hostInfo = hostMetaInfos[ hostMeta ];
                    if( hostInfo == null )
                        continue;

                    for( GenericCubeTextureMap.Layer layer : GenericCubeTextureMap.Layer.values() )
                    {
                        GenericCubeTextureMap oreTextureMap = oreInfo.modelTextureMap();
                        GenericCubeTextureMap hostTextureMap = hostInfo.modelTextureMap();

                        ResourceLocation oreTextureResource = oreTextureMap.getOrDefault( layer , null );
                        ResourceLocation hostTextureResource = hostTextureMap.getOrDefault( layer , null );

                        if( oreTextureResource == null && hostTextureResource == null )
                            continue;

                        ResourceLocation facingResource = getGeneratedResourceLocation(
                            ore.getResourcePath(),
                            host.getResourceDomain(),
                            host.getResourcePath(),
                            hostMeta,
                            layer.toString().toLowerCase() );
                        System.out.println( "Stitching " + facingResource.toString() );

                        LayeredTextureLayer oreLayer = new LayeredTextureLayer( oreTextureResource != null ? oreTextureResource : oreTextureMap.get( layer ) );
                        LayeredTextureLayer hostLayer = new LayeredTextureLayer( hostTextureResource != null ? hostTextureResource : hostTextureMap.get( layer ) );

                        TextureAtlasSprite generatedTexture = new LayeredTexture(
                            facingResource,
                            new LayeredTextureLayer[] { oreLayer , hostLayer } );
                        textureMap.setTextureEntry( generatedTexture );

                        for( EnumFacing facing : layer.applicableFacings )
                        {
                            ResourceLocation layerResource = getGeneratedResourceLocation(
                                ore.getResourcePath(),
                                host.getResourceDomain(),
                                host.getResourcePath(),
                                hostMeta,
                                facing.getName2() );
                            INSTANCE.generatedTextureMap.put( layerResource.getResourcePath() , generatedTexture );
                        }
                    }
                }
            }
        }

        INSTANCE.isInitialized = true;
    }

    private static ResourceLocation getGeneratedResourceLocation(
        String orePath,
        String hostDomain,
        String hostPath,
        int hostMeta,
        String direction )
    {
        // Ex: ore_cinnabar_host_minecraft_stone_3_north
        return Strata.resource( String.format( "ore_%s_host_%s_%s_%d_%s" , orePath , hostDomain , hostPath , hostMeta , direction ) );
    }
}
