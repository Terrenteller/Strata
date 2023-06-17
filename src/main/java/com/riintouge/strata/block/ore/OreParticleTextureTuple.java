package com.riintouge.strata.block.ore;

import com.riintouge.strata.misc.MetaResourceLocation;
import com.riintouge.strata.block.ProtoBlockTextureMap;
import com.riintouge.strata.block.BakedModelCache;
import com.riintouge.strata.block.host.HostRegistry;
import com.riintouge.strata.block.host.IHostInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.EnumFacing;

public class OreParticleTextureTuple
{
    public final TextureAtlasSprite combinedTexture;
    public final TextureAtlasSprite hostTexture;
    public final TextureAtlasSprite oreTexture;

    public OreParticleTextureTuple(
        MetaResourceLocation host,
        IOreInfo oreInfo,
        EnumFacing facing,
        boolean fallbackToMissing )
    {
        IHostInfo hostInfo = HostRegistry.INSTANCE.find( host );
        ProtoBlockTextureMap hostModelTextureMap = hostInfo != null ? hostInfo.modelTextureMap() : null;
        TextureAtlasSprite combinedTexture = null , hostTexture = null , oreTexture = null;

        if( hostModelTextureMap != null )
        {
            // We don't pass hostModelTextureMap to OreParticleTextureManager directly, but it's used
            // during OreParticleTextureManager's initialization. Checking first is a minor optimization.
            if( OreParticleTextureManager.INSTANCE.isActive() )
            {
                combinedTexture = OreParticleTextureManager.INSTANCE.findTextureOrNull(
                    oreInfo.oreName(),
                    host.resourceLocation.getResourceDomain(),
                    host.resourceLocation.getResourcePath(),
                    host.meta,
                    facing );
            }

            if( combinedTexture == null )
            {
                hostTexture = hostModelTextureMap.getTexture( facing );

                if( hostTexture == null )
                    hostTexture = BakedModelCache.INSTANCE.getBakedModel( host ).getParticleTexture();
            }
        }

        if( combinedTexture == null )
        {
            if( hostTexture == null && fallbackToMissing )
                hostTexture = Minecraft.getMinecraft().getTextureMapBlocks().getMissingSprite();

            oreTexture = oreInfo.modelTextureMap().getTexture( facing );

            if( oreTexture == null && fallbackToMissing )
                oreTexture = Minecraft.getMinecraft().getTextureMapBlocks().getMissingSprite();
        }

        this.combinedTexture = combinedTexture;
        this.hostTexture = hostTexture;
        this.oreTexture = oreTexture;
    }
}
