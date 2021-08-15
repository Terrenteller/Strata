package com.riintouge.strata.block.geo;

import com.riintouge.strata.Strata;
import com.riintouge.strata.image.LayeredTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class GeoItemFragmentTextureManager
{
    public static final GeoItemFragmentTextureManager INSTANCE = new GeoItemFragmentTextureManager();

    private GeoItemFragmentTextureManager()
    {
        // Nothing to do
    }

    // Statics

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
        System.out.println( "GeoItemFragmentTextureManager::onEvent( TextureStitchEvent.Pre )" );

        TextureMap textureMap = event.getMap();

        GeoTileSetRegistry tileSetRegistry = GeoTileSetRegistry.INSTANCE;
        for( String tileSetName : tileSetRegistry.tileSetNames() )
        {
            for( TileType type : TileType.values() )
            {
                if( !type.isPrimary )
                    continue;

                IGeoTileInfo tileSet = tileSetRegistry.findTileInfo( tileSetName , type );
                if( tileSet != null && tileSet.hasFragment() )
                {
                    String fragmentType = GeoItemFragment.getTypeForMaterial( tileSet.material() );
                    if( fragmentType == null )
                        continue;

                    TextureAtlasSprite generatedTexture = new LayeredTexture(
                        getTextureLocation( tileSet , type ),
                        tileSet.fragmentTextureLayers() );

                    textureMap.setTextureEntry( generatedTexture );
                }
            }
        }
    }
}
