package com.riintouge.strata.block;

import com.riintouge.strata.Strata;
import com.riintouge.strata.block.geo.GeoTileSetRegistry;
import com.riintouge.strata.block.geo.IGeoTileSet;
import com.riintouge.strata.block.ore.IOreTileSet;
import com.riintouge.strata.block.ore.OreRegistry;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ICustomModelLoader;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SideOnly( Side.CLIENT )
public final class SampleBlockModelLoader implements ICustomModelLoader
{
    private static final ResourceLocation GEO_SAMPLE_MODEL_RESOURCE = new ModelResourceLocation( Strata.resource( "proto_sample" ) , null );
    private static final ResourceLocation ORE_SAMPLE_MODEL_RESOURCE = new ModelResourceLocation( Strata.resource( "proto_sample_overlay" ) , null );
    private static final String RESOURCE_REGEX = String.format( "^%s:(.+)%s#" , Strata.MOD_ID , SampleBlock.REGISTRY_NAME_SUFFIX );
    private static final Pattern RESOURCE_PATTERN = Pattern.compile( RESOURCE_REGEX );
    private static final int BLOCK_NAME_GROUP = 1;

    // ICustomModelLoader overrides

    @Override
    public boolean accepts( ResourceLocation modelLocation )
    {
        Matcher matcher = RESOURCE_PATTERN.matcher( modelLocation.toString() );
        if( !matcher.find() )
            return false;

        String blockName = matcher.group( BLOCK_NAME_GROUP );
        return GeoTileSetRegistry.INSTANCE.contains( blockName ) || OreRegistry.INSTANCE.contains( blockName );
    }

    @Override
    public IModel loadModel( ResourceLocation modelLocation )
    {
        String modelLocationString = modelLocation.toString();
        Strata.LOGGER.trace( String.format( "SampleBlockModelLoader::loadModel( '%s' )" , modelLocationString ) );

        Matcher matcher = RESOURCE_PATTERN.matcher( modelLocationString );
        if( !matcher.find() )
            throw new IllegalArgumentException( modelLocationString );

        String blockName = matcher.group( BLOCK_NAME_GROUP );
        IGeoTileSet geoTileSet = GeoTileSetRegistry.INSTANCE.find( blockName );
        if( geoTileSet != null )
            return new ModelRetexturizer( GEO_SAMPLE_MODEL_RESOURCE , geoTileSet.getInfo( null ).modelTextureMap() );

        IOreTileSet oreTileSet = OreRegistry.INSTANCE.find( blockName );
        if( oreTileSet != null )
            return new ModelRetexturizer( ORE_SAMPLE_MODEL_RESOURCE , oreTileSet.getInfo().modelTextureMap() );

        return ModelLoaderRegistry.getMissingModel();
    }

    // IResourceManagerReloadListener overrides

    @Override
    public void onResourceManagerReload( IResourceManager resourceManager )
    {
        // Nothing to do
    }
}
