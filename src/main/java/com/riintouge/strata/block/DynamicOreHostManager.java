package com.riintouge.strata.block;

import com.riintouge.strata.Strata;
import com.riintouge.strata.image.LayeredTexture;
import com.riintouge.strata.image.LayeredTextureLayer;
import com.riintouge.strata.property.UnlistedPropertyHostRock;
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

public class DynamicOreHostManager
{
    public static final DynamicOreHostManager INSTANCE = new DynamicOreHostManager();

    private static final Logger LOGGER = LogManager.getLogger();
    private boolean alreadyInitializedOnce = false;
    private Map< String , ResourceLocation > oreNameToTextureResourceMap = new HashMap<>();
    private Map< String , ResourceLocation > hostNameToTextureResourceMap = new HashMap<>();
    private Map< String , TextureAtlasSprite > generatedTextureMap = new HashMap<>();

    private DynamicOreHostManager()
    {
        // TODO: Get a real default like vanilla stone or the missing texture
        registerHost( UnlistedPropertyHostRock.DEFAULT , new ResourceLocation( "blocks/stone" ) );
    }

    public void registerOre( String ore , ResourceLocation resourceLocation )
    {
        if( alreadyInitializedOnce )
            LOGGER.warn( "registerOre called too late!" );

        if( !oreNameToTextureResourceMap.containsKey( ore ) )
            oreNameToTextureResourceMap.put( ore , resourceLocation );
    }

    // Add mapping from host name to texture resource location
    public void registerHost( String host , ResourceLocation resourceLocation )
    {
        if( alreadyInitializedOnce )
            LOGGER.warn( "registerHost called too late!" );

        if( !hostNameToTextureResourceMap.containsKey( host ) )
            hostNameToTextureResourceMap.put( host , resourceLocation );
    }

    public TextureAtlasSprite getGeneratedTexture( String ore , String host )
    {
        String resourcePath = getGeneratedResourceLocation( ore , host ).getResourcePath();
        if( generatedTextureMap.containsKey( resourcePath ) )
            return generatedTextureMap.get( resourcePath );

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
        System.out.println( "DynamicOreHostManager::onEvent( TextureStitchEvent.Pre )" );

        TextureMap textureMap = event.getMap();
        int generatedTextureCount = 0 , oreCount = 0 , hostCount = 0;
        long startTime = System.nanoTime();

        for( String oreName : INSTANCE.oreNameToTextureResourceMap.keySet() )
        {
            oreCount++;

            for( String hostName : INSTANCE.hostNameToTextureResourceMap.keySet() )
            {
                hostCount++;

                ResourceLocation ore = INSTANCE.oreNameToTextureResourceMap.get( oreName );
                ResourceLocation host = INSTANCE.hostNameToTextureResourceMap.get( hostName );
                ResourceLocation generatedResourceLocation = getGeneratedResourceLocation( oreName , hostName );
                //System.out.println( "Generating " + generatedResourceLocation.toString() );

                LayeredTextureLayer oreLayer = new LayeredTextureLayer( ore );
                LayeredTextureLayer hostLayer = new LayeredTextureLayer( host );
                TextureAtlasSprite generatedTexture = new LayeredTexture(
                    generatedResourceLocation,
                    new LayeredTextureLayer[] { oreLayer , hostLayer } );

                textureMap.setTextureEntry( generatedTexture );
                INSTANCE.generatedTextureMap.put( generatedResourceLocation.getResourcePath() , generatedTexture );
                generatedTextureCount++;
            }
        }

        long endTime = System.nanoTime();
        LOGGER.info( String.format(
            "Generated %d texture(s) from %d hosts and %d ores in %d millisecond(s)",
            generatedTextureCount,
            hostCount / oreCount,
            oreCount,
            ( endTime - startTime ) / 1000000 ) );

        INSTANCE.alreadyInitializedOnce = true;
    }

    private static ResourceLocation getGeneratedResourceLocation( String ore , String host )
    {
        return new ResourceLocation( Strata.modid , String.format( "ore_%s_host_%s" , ore , host ) );
    }
}
