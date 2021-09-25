package com.riintouge.strata.block.geo;

import com.riintouge.strata.Strata;
import com.riintouge.strata.image.LayeredTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly( Side.CLIENT )
public final class GeoItemFragmentTextureManager
{
    public static ResourceLocation getTextureLocation( IHostInfo hostInfo , TileType fragmentType )
    {
        String type = GeoItemFragment.getTypeForMaterial( fragmentType.material );
        return type != null
            ? Strata.resource( String.format( "items/%s_%d_%s" , hostInfo.registryName().getResourcePath() , hostInfo.meta() , type ) )
            : null;
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

                IGeoTileInfo tileSet = tileSetRegistry.findTileInfo( tileSetName , tileType );
                if( tileSet == null || !tileSet.hasFragment() )
                    continue;

                String fragmentType = GeoItemFragment.getTypeForMaterial( tileSet.material() );
                if( fragmentType == null )
                    continue;

                ResourceLocation textureLocation = getTextureLocation( tileSet , tileType );
                if( textureLocation == null )
                    continue;

                TextureAtlasSprite generatedTexture = new LayeredTexture(
                    textureLocation,
                    tileSet.fragmentTextureLayers() );

                textureMap.setTextureEntry( generatedTexture );
            }
        }
    }
}
