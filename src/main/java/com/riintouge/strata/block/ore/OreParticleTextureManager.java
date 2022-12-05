package com.riintouge.strata.block.ore;

import com.riintouge.strata.Strata;
import com.riintouge.strata.StrataConfig;
import com.riintouge.strata.block.ProtoBlockTextureMap;
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
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

@SideOnly( Side.CLIENT )
public final class OreParticleTextureManager
{
    public static final OreParticleTextureManager INSTANCE = new OreParticleTextureManager();

    private boolean isInitialized = false;
    private Map< String , TextureAtlasSprite > generatedTextureMap = new HashMap<>();

    private OreParticleTextureManager()
    {
        // Nothing to do
    }

    public boolean isActive()
    {
        return StrataConfig.usePrecomputedOreParticles && isInitialized();
    }

    public boolean isInitialized()
    {
        return isInitialized;
    }

    @Nullable
    public TextureAtlasSprite findTextureOrNull( String orePath , String hostDomain , String hostPath , int hostMeta , EnumFacing facing )
    {
        String resourcePath = getGeneratedResourceLocation( orePath , hostDomain , hostPath , hostMeta , facing.getName2() ).getResourcePath();
        return generatedTextureMap.getOrDefault( resourcePath , null );
    }

    @Nonnull
    public TextureAtlasSprite findTextureOrMissing( String orePath , String hostDomain , String hostPath , int hostMeta , EnumFacing facing )
    {
        String resourcePath = getGeneratedResourceLocation( orePath , hostDomain , hostPath , hostMeta , facing.getName2() ).getResourcePath();
        TextureAtlasSprite texture = generatedTextureMap.getOrDefault( resourcePath , null );
        if( texture != null )
            return texture;

        Strata.LOGGER.warn( String.format( "No texture was generated for \"%s\"!" , resourcePath ) );
        return Minecraft.getMinecraft().getTextureMapBlocks().getMissingSprite();
    }

    @SubscribeEvent( priority = EventPriority.LOWEST )
    public static void stitchTextures( TextureStitchEvent.Pre event )
    {
        Strata.LOGGER.trace( "OreParticleTextureManager::stitchTextures()" );

        if( !StrataConfig.usePrecomputedOreParticles )
            return;

        TextureMap textureMap = event.getMap();
        Map< ResourceLocation , IHostInfo[] > hostInfoMap = HostRegistry.INSTANCE.allHosts();

        if( hostInfoMap.size() == 0 )
            return;

        for( IOreTileSet oreTileSet : OreRegistry.INSTANCE.all() )
        {
            IOreInfo oreInfo = oreTileSet.getInfo();
            ResourceLocation ore = Strata.resource( oreInfo.oreName() );

            for( ResourceLocation host : hostInfoMap.keySet() )
            {
                IHostInfo[] hostMetaInfos = hostInfoMap.get( host );

                for( int hostMeta = 0 ; hostMeta < hostMetaInfos.length ; hostMeta++ )
                {
                    IHostInfo hostInfo = hostMetaInfos[ hostMeta ];
                    if( hostInfo == null )
                        continue;

                    ProtoBlockTextureMap oreTextureMap = oreInfo.modelTextureMap();
                    ProtoBlockTextureMap hostTextureMap = hostInfo.modelTextureMap();

                    if( oreTextureMap == null || hostTextureMap == null )
                        continue;

                    for( ProtoBlockTextureMap.Layer layer : ProtoBlockTextureMap.Layer.values() )
                    {
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
                        Strata.LOGGER.trace( "Stitching " + facingResource.toString() );

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
