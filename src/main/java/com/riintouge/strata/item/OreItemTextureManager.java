package com.riintouge.strata.item;

import com.riintouge.strata.Strata;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.ResourceLocation;
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

    public void regenerate( TextureMap textureMap )
    {
        System.out.println( "OreItemTextureManager::onEvent( TextureStitchEvent.Pre )" );

        long startTime = System.nanoTime();
        int generatedTextureCount = 0;

        // Here goes...
        for( String oreName : oreNameToTextureResourceMap.keySet() )
        {
            ResourceLocation textureResource = oreNameToTextureResourceMap.get( oreName );
            ResourceLocation generatedResourceLocation = getTextureLocation( oreName );
            //System.out.println( "Generating " + generatedResourceLocation.toString() );
            TextureAtlasSprite generatedTexture = new GeneratedOreItemTexture( textureResource , generatedResourceLocation.toString() );
            textureMap.setTextureEntry( generatedTexture );
            generatedTextureCount++;
        }

        long endTime = System.nanoTime();
        LOGGER.info( String.format(
            "Generate %d ore item textures in %d millisecond(s)",
            generatedTextureCount,
            ( endTime - startTime ) / 1000000 ) );

        alreadyInitializedOnce = true;
    }

    public static ResourceLocation getTextureLocation( String oreName )
    {
        return new ResourceLocation( Strata.modid , "items/ore_" + oreName );
    }
}
