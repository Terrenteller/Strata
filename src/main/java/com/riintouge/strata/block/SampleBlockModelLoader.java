package com.riintouge.strata.block;

import com.riintouge.strata.Strata;
import com.riintouge.strata.block.geo.GeoTileSetRegistry;
import com.riintouge.strata.block.geo.IGeoTileInfo;
import com.riintouge.strata.block.geo.IGeoTileSet;
import com.riintouge.strata.block.ore.IOreTileSet;
import com.riintouge.strata.block.ore.OreRegistry;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ICustomModelLoader;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SideOnly( Side.CLIENT )
public final class SampleBlockModelLoader implements ICustomModelLoader
{
    private static final String ResourcePattern = String.format( "^%s:(.+)%s#" , Strata.modid , SampleBlock.REGISTRY_NAME_SUFFIX );
    private static final int ResourcePatternBlockNameGroup = 1;
    private static final Pattern ResourceRegex = Pattern.compile( ResourcePattern );

    // ICustomModelLoader overrides

    @Override
    public boolean accepts( ResourceLocation modelLocation )
    {
        Matcher matcher = ResourceRegex.matcher( modelLocation.toString() );
        if( !matcher.find() )
            return false;

        String blockName = matcher.group( ResourcePatternBlockNameGroup );
        return GeoTileSetRegistry.INSTANCE.contains( blockName ) || OreRegistry.INSTANCE.contains( blockName );
    }

    @Override
    public IModel loadModel( ResourceLocation modelLocation )
    {
        Strata.LOGGER.trace( String.format( "SampleBlockModelLoader::loadModel( \"%s\" )" , modelLocation.toString() ) );

        Matcher matcher = ResourceRegex.matcher( modelLocation.toString() );
        matcher.find();
        String blockName = matcher.group( ResourcePatternBlockNameGroup );

        IGeoTileSet geoTileSet = GeoTileSetRegistry.INSTANCE.find( blockName );
        if( geoTileSet != null )
        {
            IGeoTileInfo geoTileInfo = geoTileSet.getInfo( null );
            if( geoTileInfo == null )
            {
                throw new IllegalStateException(
                    String.format(
                        "GeoTileSet '%s' does not have a primary tile type!",
                        blockName ) );
            }

            ModelResourceLocation templateModelResource = new ModelResourceLocation( Strata.resource( "proto_sample" ) , null );
            IResourceLocationMap textureMap = geoTileInfo.modelTextureMap();
            return new ModelRetexturizer( templateModelResource , textureMap );
        }

        IOreTileSet oreTileSet = OreRegistry.INSTANCE.find( blockName );
        if( oreTileSet != null )
        {
            ModelResourceLocation templateModelResource = new ModelResourceLocation( Strata.resource( "proto_sample_overlay" ) , null );
            IResourceLocationMap textureMap = oreTileSet.getInfo().modelTextureMap();
            return new ModelRetexturizer( templateModelResource , textureMap );
        }

        throw new IllegalStateException( "Sample block not found in GeoTileSetRegistry or OreRegistry!" );
    }

    // IResourceManagerReloadListener overrides

    @Override
    public void onResourceManagerReload( IResourceManager resourceManager )
    {
        // Nothing to do
    }
}
