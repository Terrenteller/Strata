package com.riintouge.strata.block.geo;

import com.riintouge.strata.Strata;
import com.riintouge.strata.block.IResourceLocationMap;
import com.riintouge.strata.block.ModelRetexturizer;
import com.riintouge.strata.util.EnumUtil;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ICustomModelLoader;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nullable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SideOnly( Side.CLIENT )
public final class GeoBlockModelLoader implements ICustomModelLoader
{
    private static final String RESOURCE_REGEX = String.format( "^%s:(([a-z_]+?)(?:_([a-z]+))?)(?:#.+)$" , Strata.MOD_ID );
    private static final Pattern RESOURCE_PATTERN = Pattern.compile( RESOURCE_REGEX );
    private static final int FULL_NAME_GROUP = 1;
    private static final int TILE_SET_NAME_GROUP = 2;
    private static final int TILE_TYPE_GROUP = 3;

    public GeoBlockModelLoader()
    {
        // Nothing to do
    }

    @Nullable
    private Pair< String , TileType > getNameAndTileTypeFromLocation( ResourceLocation modelLocation )
    {
        Matcher matcher = RESOURCE_PATTERN.matcher( modelLocation.toString() );
        if( !matcher.find() )
            return null;

        String tileTypeName = matcher.group( TILE_TYPE_GROUP );
        TileType tileType = EnumUtil.valueOfOrNull( TileType.class , tileTypeName );
        if( tileType != null )
        {
            String tileSetName = matcher.group( TILE_SET_NAME_GROUP );
            IGeoTileInfo geoTileInfo = GeoTileSetRegistry.INSTANCE.findTileInfo( tileSetName , tileType );
            if( geoTileInfo != null )
                return new ImmutablePair<>( tileSetName , geoTileInfo.tileType() );
        }

        // The resource name for a primary type may end with a primary type enum value and fail the above search.
        // For example, quartz_sand#... is not quartz and SAND, it's quartz_sand and SAND.
        String resourcePath = matcher.group( FULL_NAME_GROUP );
        IGeoTileInfo geoTileInfo = GeoTileSetRegistry.INSTANCE.findTileInfo( resourcePath , null );
        return new ImmutablePair<>( resourcePath , geoTileInfo != null ? geoTileInfo.tileType() : null );
    }

    // ICustomModelLoader overrides

    @Override
    public boolean accepts( ResourceLocation modelLocation )
    {
        Pair< String , TileType > stoneAndType = getNameAndTileTypeFromLocation( modelLocation );
        return stoneAndType != null && stoneAndType.getRight() != null;
    }

    @Override
    public IModel loadModel( ResourceLocation modelLocation )
    {
        ModelResourceLocation modelResourceLocation = (ModelResourceLocation)modelLocation;
        Strata.LOGGER.trace( String.format( "GeoBlockModelLoader::loadModel( '%s' )" , modelResourceLocation.toString() ) );

        Pair< String , TileType > stoneAndType = getNameAndTileTypeFromLocation( modelLocation );
        TileType tileType = stoneAndType.getRight();
        IGeoTileInfo tileInfo = GeoTileSetRegistry.INSTANCE.findTileInfo( stoneAndType.getLeft() , tileType );
        ModelResourceLocation originalModelResource = new ModelResourceLocation( tileInfo.blockStateResource() , modelResourceLocation.getVariant() );
        IResourceLocationMap textureMap = tileInfo.modelTextureMap();
        return new ModelRetexturizer( originalModelResource , textureMap );
    }

    // IResourceManagerReloadListener overrides

    @Override
    public void onResourceManagerReload( IResourceManager resourceManager )
    {
        // Nothing to do
    }
}
