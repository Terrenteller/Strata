package com.riintouge.strata.block.geo;

import com.riintouge.strata.Strata;
import com.riintouge.strata.item.StrataItemModel;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ICustomModelLoader;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SideOnly( Side.CLIENT )
public final class GeoItemFragmentModelLoader implements ICustomModelLoader
{
    private static final String ResourcePattern = String.format( "^%s:models/item/(.+)(_[^_]+)$" , Strata.modid );
    private static final int ResourcePatternTileSetNameGroup = 1;
    private static final int ResourcePatternFragmentTypeSuffixGroup = 2;
    private static final Pattern ResourceRegex = Pattern.compile( ResourcePattern );

    // ICustomModelLoader overrides

    @Override
    public boolean accepts( ResourceLocation modelLocation )
    {
        Matcher matcher = ResourceRegex.matcher( modelLocation.toString() );
        if( !matcher.find() )
            return false;

        String tileSetName = matcher.group( ResourcePatternTileSetNameGroup );
        String fragmentTypeSuffix = matcher.group( ResourcePatternFragmentTypeSuffixGroup );

        for( TileType type : TileType.values() )
            if( type.isPrimary && fragmentTypeSuffix.equals( type.fragmentResourceLocationSuffix ) )
                return GeoTileSetRegistry.INSTANCE.findTileInfo( tileSetName , type ) != null;

        return false;
    }

    @Override
    public IModel loadModel( ResourceLocation modelLocation )
    {
        Strata.LOGGER.trace( String.format( "GeoItemFragmentModelLoader::loadModel( \"%s\" )" , modelLocation.toString() ) );

        Matcher matcher = ResourceRegex.matcher( modelLocation.toString() );
        matcher.find();
        String tileSetName = matcher.group( ResourcePatternTileSetNameGroup );
        String fragmentTypeSuffix = matcher.group( ResourcePatternFragmentTypeSuffixGroup );

        for( TileType tileType : TileType.values() )
        {
            if( tileType.isPrimary && fragmentTypeSuffix.equals( tileType.fragmentResourceLocationSuffix ) )
            {
                IGeoTileInfo tileInfo = GeoTileSetRegistry.INSTANCE.findTileInfo( tileSetName , tileType );
                return new StrataItemModel( GeoItemFragmentTextureManager.getTextureLocation( tileInfo ) );
            }
        }

        return null;
    }

    // IResourceManagerReloadListener overrides

    @Override
    public void onResourceManagerReload( IResourceManager resourceManager )
    {
        // Nothing to do
    }
}
