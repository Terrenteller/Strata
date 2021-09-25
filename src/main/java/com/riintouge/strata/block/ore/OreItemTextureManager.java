package com.riintouge.strata.block.ore;

import com.riintouge.strata.Strata;
import com.riintouge.strata.image.LayeredTexture;
import com.riintouge.strata.image.LayeredTextureLayer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.HashMap;
import java.util.Map;

public final class OreItemTextureManager
{
    public static final OreItemTextureManager INSTANCE = new OreItemTextureManager();

    private Map< String , ResourceLocation > oreNameToTextureResourceMap = new HashMap<>();

    private OreItemTextureManager()
    {
        // Nothing to do
    }

    public void register( String ore , ResourceLocation resourceLocation )
    {
        Strata.LOGGER.trace( String.format( "OreItemTextureManager::register( \"%s\" , \"%s\" )" , ore , resourceLocation.toString() ) );
        if( !oreNameToTextureResourceMap.containsKey( ore ) )
            oreNameToTextureResourceMap.put( ore , resourceLocation );
    }

    // Statics

    public static ResourceLocation getTextureLocation( String oreName )
    {
        return Strata.resource( "items/" + oreName );
    }

    @SideOnly( Side.CLIENT )
    @SubscribeEvent( priority = EventPriority.LOWEST )
    public static void stitchTextures( TextureStitchEvent.Pre event )
    {
        Strata.LOGGER.trace( "OreItemTextureManager::stitchTextures()" );

        TextureMap textureMap = event.getMap();

        for( String oreName : INSTANCE.oreNameToTextureResourceMap.keySet() )
        {
            ResourceLocation textureResource = INSTANCE.oreNameToTextureResourceMap.get( oreName );
            ResourceLocation generatedResourceLocation = getTextureLocation( oreName );
            Strata.LOGGER.trace( "Stitching " + generatedResourceLocation.toString() );

            LayeredTextureLayer oreLayer = new LayeredTextureLayer( textureResource );
            TextureAtlasSprite generatedTexture = new LayeredTexture(
                generatedResourceLocation,
                new LayeredTextureLayer[] { oreLayer } );

            textureMap.setTextureEntry( generatedTexture );
        }
    }
}
