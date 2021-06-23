package com.riintouge.strata.block.ore;

import com.riintouge.strata.Strata;
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
        String resourcePath = getGeneratedResourceLocation( oreDomain , orePath , oreMeta , hostDomain , hostPath , hostMeta , facing ).getResourcePath();
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

                        // TODO: Most hosts and ores are not expected to have distinct textures on every side.
                        // This inner loop leads to unnecessary duplication and is in dire need of optimization.
                        // Even with 16x16 images the memory footprint is outrageous compared to what it could be.
                        // How often does getQuads get called? Can we use something like getDisplayLayer so
                        // non-unique sides point to a more generic texture? Would it be better to put ore blocks
                        // in the cutout render layer and introduce a new block model to let the render engine do
                        // all the work?
                        for( EnumFacing facing : EnumFacing.values() )
                        {
                            ResourceLocation oreTextureResource = oreInfo.modelTextureMap().getOrDefault( facing );
                            ResourceLocation hostTextureResource = hostInfo.facingTextureMap().getOrDefault( facing );
                            ResourceLocation generatedResource = getGeneratedResourceLocation( ore , oreMeta , host , hostMeta , facing );
                            //System.out.println( "Generating " + generatedResource.toString() );

                            LayeredTextureLayer oreLayer = new LayeredTextureLayer( oreTextureResource );
                            LayeredTextureLayer hostLayer = new LayeredTextureLayer( hostTextureResource );
                            TextureAtlasSprite generatedTexture = new LayeredTexture(
                                generatedResource ,
                                new LayeredTextureLayer[]{ oreLayer , hostLayer } );

                            textureMap.setTextureEntry( generatedTexture );
                            INSTANCE.generatedTextureMap.put( generatedResource.getResourcePath() , generatedTexture );
                            generatedTextureCount++;
                        }
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
            generatedTextureCount / ( oreCount * EnumFacing.values().length ),
            oreCount,
            ( endTime - startTime ) / 1000000 ) );

        INSTANCE.alreadyInitializedOnce = true;
    }

    private static ResourceLocation getGeneratedResourceLocation(
        ResourceLocation ore,
        int oreMeta,
        ResourceLocation host,
        int hostMeta,
        EnumFacing facing )
    {
        return getGeneratedResourceLocation(
            ore.getResourceDomain(),
            ore.getResourcePath(),
            oreMeta,
            host.getResourceDomain(),
            host.getResourcePath(),
            hostMeta,
            facing );
    }

    private static ResourceLocation getGeneratedResourceLocation(
        String oreDomain,
        String orePath,
        int oreMeta,
        String hostDomain,
        String hostPath,
        int hostMeta,
        EnumFacing facing )
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
            facing.getName2() );
        return new ResourceLocation( Strata.modid , texturePath );
    }
}
