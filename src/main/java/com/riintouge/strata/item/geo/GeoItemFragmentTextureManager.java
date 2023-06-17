package com.riintouge.strata.item.geo;

import com.riintouge.strata.Strata;
import com.riintouge.strata.block.geo.GeoTileSetRegistry;
import com.riintouge.strata.block.geo.IGeoTileInfo;
import com.riintouge.strata.block.geo.TileType;
import com.riintouge.strata.image.LayeredTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;

@SideOnly( Side.CLIENT )
public final class GeoItemFragmentTextureManager
{
    @Nullable
    public static ResourceLocation getTextureLocation( IGeoTileInfo tileInfo )
    {
        String fragmentResourceLocationSuffix = tileInfo.tileType().fragmentResourceLocationSuffix;
        if( fragmentResourceLocationSuffix != null )
        {
            return Strata.resource(
                String.format(
                    "items/%s_%d%s",
                    tileInfo.registryName().getResourcePath(),
                    tileInfo.meta(),
                    fragmentResourceLocationSuffix ) );
        }

        return null;
    }

    @SubscribeEvent( priority = EventPriority.LOWEST )
    public static void stitchTextures( TextureStitchEvent.Pre event )
    {
        Strata.LOGGER.trace( "GeoItemFragmentTextureManager::stitchTextures()" );

        TextureMap textureMap = event.getMap();
        GeoTileSetRegistry tileSetRegistry = GeoTileSetRegistry.INSTANCE;

        for( String tileSetName : tileSetRegistry.tileSetNames() )
        {
            for( TileType tileType : TileType.values() )
            {
                if( !tileType.isPrimary )
                    continue;

                IGeoTileInfo tileInfo = tileSetRegistry.findTileInfo( tileSetName , tileType );
                if( tileInfo == null || !tileInfo.hasFragment() )
                    continue;

                ResourceLocation textureLocation = getTextureLocation( tileInfo );
                if( textureLocation == null )
                    continue;

                TextureAtlasSprite generatedTexture = new LayeredTexture(
                    textureLocation,
                    tileInfo.fragmentTextureLayers() );

                textureMap.setTextureEntry( generatedTexture );
            }
        }
    }
}
