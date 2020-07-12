package com.riintouge.strata.block.ore;

import com.riintouge.strata.Strata;
import com.riintouge.strata.block.geo.HostRegistry;
import com.riintouge.strata.block.geo.IHostInfo;
import com.riintouge.strata.image.LayeredTexture;
import com.riintouge.strata.image.LayeredTextureLayer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
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

    public TextureAtlasSprite findTexture( ResourceLocation ore , int oreMeta , ResourceLocation host , int hostMeta )
    {
        String resourcePath = getGeneratedResourceLocation( ore , oreMeta , host , hostMeta ).getResourcePath();
        TextureAtlasSprite texture = generatedTextureMap.getOrDefault( resourcePath , null );
        if( texture != null )
            return texture;

        System.out.println( String.format( "No texture was generated for \"%s\"!" , resourcePath ) );

        // FIXME: Where on Notch's green, flat earth is the missing texture resource?
        // Until this is fixed, good luck with the null...
        return Minecraft
            .getMinecraft()
            .getTextureMapBlocks()
            .getTextureExtry( "" ); // Yup, that's a typo in the Forge API
    }

    @SubscribeEvent( priority = EventPriority.LOWEST )
    public static void stitchTextures( TextureStitchEvent.Pre event )
    {
        System.out.println( "OreBlockTextureManager::onEvent( TextureStitchEvent.Pre )" );

        TextureMap textureMap = event.getMap();
        int generatedTextureCount = 0 , oreCount = 0;
        Map< ResourceLocation , IHostInfo[] > hostInfoMap = HostRegistry.INSTANCE.allHosts();

        if( hostInfoMap.size() == 0 )
            return;

        long startTime = System.nanoTime();

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
                    else
                        oreCount++;

                    for( int hostMeta = 0 ; hostMeta < hostMetaInfos.length ; hostMeta++ )
                    {
                        IHostInfo hostInfo = hostMetaInfos[ hostMeta ];
                        if( hostInfo == null )
                            continue;

                        ResourceLocation oreTextureResource = oreInfo.oreBlockOverlayTextureResource();
                        ResourceLocation hostTextureResource = hostInfo.baseTextureLocation();
                        ResourceLocation generatedResource = getGeneratedResourceLocation( ore , oreMeta , host , hostMeta );
                        //System.out.println( "Generating " + generatedResource.toString() );

                        LayeredTextureLayer oreLayer = new LayeredTextureLayer( oreTextureResource );
                        LayeredTextureLayer hostLayer = new LayeredTextureLayer( hostTextureResource );
                        TextureAtlasSprite generatedTexture = new LayeredTexture(
                            generatedResource,
                            new LayeredTextureLayer[] { oreLayer , hostLayer } );

                        textureMap.setTextureEntry( generatedTexture );
                        INSTANCE.generatedTextureMap.put( generatedResource.getResourcePath() , generatedTexture );
                        generatedTextureCount++;
                    }
                }
            }
        }

        if( oreCount == 0 )
            return;

        long endTime = System.nanoTime();
        oreCount /= hostInfoMap.keySet().size();
        LOGGER.info( String.format(
            "Generated %d texture(s) from %d hosts and %d ores in %d millisecond(s)",
            generatedTextureCount,
            generatedTextureCount / oreCount,
            oreCount,
            ( endTime - startTime ) / 1000000 ) );

        INSTANCE.alreadyInitializedOnce = true;
    }

    private static ResourceLocation getGeneratedResourceLocation(
        ResourceLocation ore,
        int oreMeta,
        ResourceLocation host,
        int hostMeta )
    {
        // Ex: ore_strata_cinnabar_0_host_minecraft_stone_3
        // TODO: How to handle GenericCubeTextureMap like ore_strata_cinnabar_0_caps_host_minecraft_stone_3_caps?
        String texturePath = String.format(
            "ore_%s_%s_%d_host_%s_%s_%d",
            ore.getResourceDomain(),
            ore.getResourcePath(),
            oreMeta,
            host.getResourceDomain(),
            host.getResourcePath(),
            hostMeta );
        return new ResourceLocation( Strata.modid , texturePath );
    }
}
