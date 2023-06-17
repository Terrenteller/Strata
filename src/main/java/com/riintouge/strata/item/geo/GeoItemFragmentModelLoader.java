package com.riintouge.strata.item.geo;

import com.riintouge.strata.Strata;
import com.riintouge.strata.block.geo.GeoTileSetRegistry;
import com.riintouge.strata.block.geo.IGeoTileInfo;
import com.riintouge.strata.block.geo.TileType;
import com.riintouge.strata.item.StrataItemModel;
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
public final class GeoItemFragmentModelLoader implements ICustomModelLoader
{
    private static final String RESOURCE_REGEX = String.format( "^%s:models/item/(.+)(_[^_]+)$" , Strata.MOD_ID );
    private static final Pattern RESOURCE_PATTERN = Pattern.compile( RESOURCE_REGEX );
    private static final int TILE_SET_NAME_GROUP = 1;
    private static final int FRAGMENT_TYPE_SUFFIX_GROUP = 2;

    // ICustomModelLoader overrides

    @Override
    public boolean accepts( ResourceLocation modelLocation )
    {
        Matcher matcher = RESOURCE_PATTERN.matcher( modelLocation.toString() );
        if( !matcher.find() )
            return false;

        String tileSetName = matcher.group( TILE_SET_NAME_GROUP );
        String fragmentTypeSuffix = matcher.group( FRAGMENT_TYPE_SUFFIX_GROUP );

        for( TileType tileType : TileType.values() )
            if( tileType.isPrimary && fragmentTypeSuffix.equals( tileType.fragmentResourceLocationSuffix ) )
                return GeoTileSetRegistry.INSTANCE.findTileInfo( tileSetName , tileType ) != null;

        return false;
    }

    @Override
    public IModel loadModel( ResourceLocation modelLocation )
    {
        String modelLocationString = modelLocation.toString();
        Strata.LOGGER.trace( String.format( "GeoItemFragmentModelLoader::loadModel( '%s' )" , modelLocationString ) );

        Matcher matcher = RESOURCE_PATTERN.matcher( modelLocationString );
        if( !matcher.find() )
            throw new IllegalArgumentException( modelLocationString );

        String tileSetName = matcher.group( TILE_SET_NAME_GROUP );
        String fragmentTypeSuffix = matcher.group( FRAGMENT_TYPE_SUFFIX_GROUP );

        for( TileType tileType : TileType.values() )
        {
            if( tileType.isPrimary && fragmentTypeSuffix.equals( tileType.fragmentResourceLocationSuffix ) )
            {
                IGeoTileInfo tileInfo = GeoTileSetRegistry.INSTANCE.findTileInfo( tileSetName , tileType );
                return new StrataItemModel( GeoItemFragmentTextureManager.getTextureLocation( tileInfo ) );
            }
        }

        return ModelLoaderRegistry.getMissingModel();
    }

    // IResourceManagerReloadListener overrides

    @Override
    public void onResourceManagerReload( IResourceManager resourceManager )
    {
        // Nothing to do
    }
}
