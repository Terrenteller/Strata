package com.riintouge.strata.block;

import com.riintouge.strata.Strata;
import com.riintouge.strata.property.UnlistedPropertyHostRock;
import net.minecraft.block.Block;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

public class DynamicOreHostManager
{
    public static final DynamicOreHostManager INSTANCE = new DynamicOreHostManager();

    private static final Logger LOGGER = LogManager.getLogger();
    private boolean alreadyInitializedOnce = false;
    private Map< String , ResourceLocation > oreNameToTextureResourceMap = new HashMap<>();
    private Map< String , ResourceLocation > hostNameToTextureResourceMap = new HashMap<>();
    private Map< String , Pair< Block , Integer > > hostBlockMetaMap = new HashMap<>();
    private Map< String , TextureAtlasSprite > generatedTextureMap = new HashMap<>();
    private Map< String , PropertyEnum > registryNameToOrePropertyMap = new HashMap<>();
    private Vector< ModelResourceLocation > oreBlockModels = new Vector<>();

    private DynamicOreHostManager()
    {
        // TODO: Get a real default like vanilla stone or the missing texture
        registerHost( UnlistedPropertyHostRock.DEFAULT , new ResourceLocation( Strata.modid , "blocks/stone/weak/breccia" ) );
    }

    public void registerOre( String ore , ResourceLocation resourceLocation )
    {
        if( alreadyInitializedOnce )
            LOGGER.warn( "registerOre called too late!" );

        if( !oreNameToTextureResourceMap.containsKey( ore ) )
            oreNameToTextureResourceMap.put( ore , resourceLocation );
    }

    // A block with {registryName} uses {oreProperty}'s name for its ore name
    // A block with {registryName} and {oreProperty} variants have their models replaced with DynamicOreHostModel
    public void registerOreBlock( String registryName , PropertyEnum oreProperty )
    {
        if( alreadyInitializedOnce )
            LOGGER.warn( "registerOreBlock called too late!" );

        if( !registryNameToOrePropertyMap.containsKey( registryName ) )
            registryNameToOrePropertyMap.put( registryName , oreProperty );

        for( Object value : oreProperty.getValueClass().getEnumConstants() )
        {
            String variant = String.format( "%s=%s" , oreProperty.getName() , value.toString() );
            oreBlockModels.add( new ModelResourceLocation( registryName , variant ) );
        }
    }

    public List< ModelResourceLocation > getAllOreBlockModels()
    {
        return oreBlockModels;
    }

    // Add mapping from host name to texture resource location
    public void registerHost( String host , ResourceLocation resourceLocation )
    {
        if( alreadyInitializedOnce )
            LOGGER.warn( "registerHost called too late!" );

        if( !hostNameToTextureResourceMap.containsKey( host ) )
            hostNameToTextureResourceMap.put( host , resourceLocation );
    }

    public void registerHostBlock( String host , Block block , int meta )
    {
        if( alreadyInitializedOnce )
            LOGGER.warn( "registerHostBlock called too late!" );

        if( !hostBlockMetaMap.containsKey( host ) )
            hostBlockMetaMap.put( host , new ImmutablePair<>( block , meta ) );
    }

    public String getOreName( IBlockState state )
    {
        String registryName = state.getBlock().getRegistryName().toString();
        PropertyEnum oreProperty = registryNameToOrePropertyMap.getOrDefault( registryName , null );
        if( oreProperty != null && state.getProperties().containsKey( oreProperty ) )
            return StateUtil.getValue( state , oreProperty ).toString();

        return UnlistedPropertyHostRock.DEFAULT;
    }

    public Pair< Block , Integer > getHostBlock( String host )
    {
        return hostBlockMetaMap.getOrDefault( host , null );
    }

    public TextureAtlasSprite getGeneratedTexture( String ore , String host )
    {
        ResourceLocation targetResourcePath = getGeneratedResourceLocation( ore , host );
        String resourcePath = targetResourcePath.getResourcePath();
        if( !generatedTextureMap.containsKey( resourcePath ) )
        {
            // FIXME: I don't think this actually works
            System.out.println( String.format( "No texture was generated for \"%s\"!" , resourcePath ) );
            resourcePath = TextureMap.LOCATION_MISSING_TEXTURE.getResourcePath();
        }

        return Minecraft
            .getMinecraft()
            .getTextureMapBlocks()
            .getTextureExtry( resourcePath ); // Yup, that's a typo in the Forge API
    }

    public ResourceLocation getGeneratedResourceLocation( String ore , String host )
    {
        return new ResourceLocation( Strata.modid , String.format( "ore_%s_host_%s" , ore , host ) );
    }

    public void regenerate( TextureMap textureMap )
    {
        System.out.println( "DynamicOreHostManager::onEvent( TextureStitchEvent.Pre )" );

        long startTime = System.nanoTime();
        int generatedTextureCount = 0 , oreCount = 0 , hostCount = 0;

        // Here goes...
        for( String oreName : oreNameToTextureResourceMap.keySet() )
        {
            oreCount++;

            for( String hostName : hostNameToTextureResourceMap.keySet() )
            {
                hostCount++;

                ResourceLocation ore = oreNameToTextureResourceMap.get( oreName );
                ResourceLocation host = hostNameToTextureResourceMap.get( hostName );
                ResourceLocation generatedResourceLocation = getGeneratedResourceLocation( oreName , hostName );
                //System.out.println( "Generating " + generatedResourceLocation.toString() );
                // TODO: This should become a builder, like TextureBuilder( host ).overlay( ore ).build()
                TextureAtlasSprite generatedTexture = new GeneratedOverlayTexture( host , ore , generatedResourceLocation.getResourcePath() );

                textureMap.setTextureEntry( generatedTexture );
                generatedTextureMap.put( generatedResourceLocation.getResourcePath() , generatedTexture );
                generatedTextureCount++;
            }
        }

        long endTime = System.nanoTime();
        LOGGER.info( String.format(
            "Generate %d texture(s) from %d hosts and %d ores in %d millisecond(s)",
            generatedTextureCount,
            hostCount / oreCount,
            oreCount,
            ( endTime - startTime ) / 1000000 ) );

        alreadyInitializedOnce = true;
    }
}
