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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

public class OreItemTextureManager
{
    public static final OreItemTextureManager INSTANCE = new OreItemTextureManager();

    private static final Logger LOGGER = LogManager.getLogger();
    private boolean alreadyInitializedOnce = false;
    private Map< String , ResourceLocation > oreNameToTextureResourceMap = new HashMap<>();

    private OreItemTextureManager()
    {
        // Nothing to do
    }

    public void registerOre( String ore , ResourceLocation resourceLocation )
    {
        if( alreadyInitializedOnce )
            LOGGER.warn( "registerOre called too late!" );

        if( !oreNameToTextureResourceMap.containsKey( ore ) )
            oreNameToTextureResourceMap.put( ore , resourceLocation );
    }

    // Statics

    public static ResourceLocation getTextureLocation( String oreName )
    {
        return new ResourceLocation( Strata.modid , "items/ore_" + oreName );
    }

    @SubscribeEvent( priority = EventPriority.LOWEST )
    public static void stitchTextures( TextureStitchEvent.Pre event )
    {
        System.out.println( "OreItemTextureManager::onEvent( TextureStitchEvent.Pre )" );

        TextureMap textureMap = event.getMap();
        int generatedTextureCount = 0;
        long startTime = System.nanoTime();

        for( String oreName : INSTANCE.oreNameToTextureResourceMap.keySet() )
        {
            ResourceLocation textureResource = INSTANCE.oreNameToTextureResourceMap.get( oreName );
            ResourceLocation generatedResourceLocation = getTextureLocation( oreName );
            //System.out.println( "Generating " + generatedResourceLocation.toString() );

            LayeredTextureLayer oreLayer = new LayeredTextureLayer( textureResource );
            TextureAtlasSprite generatedTexture = new LayeredTexture(
                generatedResourceLocation,
                new LayeredTextureLayer[] { oreLayer } );

            textureMap.setTextureEntry( generatedTexture );
            generatedTextureCount++;
        }

        long endTime = System.nanoTime();
        LOGGER.info( String.format(
            "Generated %d ore item textures in %d millisecond(s)",
            generatedTextureCount,
            ( endTime - startTime ) / 1000000 ) );

        INSTANCE.alreadyInitializedOnce = true;
    }
}
