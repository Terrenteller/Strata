package com.riintouge.strata.block.geo;

import com.riintouge.strata.block.IModelRetexturizerMap;
import com.riintouge.strata.block.ModelRetexturizer;
import com.riintouge.strata.Strata;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ICustomModelLoader;
import net.minecraftforge.client.model.IModel;
import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GeoBlockModelLoader implements ICustomModelLoader
{
    private static final String ResourcePattern = String.format( "^%s:(([a-z_]+?)(?:_([a-z]+))?)(?:#.+)$" , Strata.modid );
    private static final Pattern ResourceRegex = Pattern.compile( ResourcePattern );

    // ICustomModelLoader overrides

    @Override
    public boolean accepts( ResourceLocation modelLocation )
    {
        Matcher matcher = ResourceRegex.matcher( modelLocation.toString() );
        if( !matcher.find() )
            return false;

        Pair< String , String > stoneAndType = getStoneAndTypePairFromFoundMatch( matcher );
        return findTextureMap( stoneAndType.getLeft() , stoneAndType.getRight() ) != null;
    }

    @Override
    public IModel loadModel( ResourceLocation modelLocation )
    {
        System.out.println( String.format( "GeoBlockModelLoader::loadModel( \"%s\" )" , modelLocation.toString() ) );

        Matcher matcher = ResourceRegex.matcher( modelLocation.toString() );
        matcher.find();

        Pair< String , String > stoneAndType = getStoneAndTypePairFromFoundMatch( matcher );
        // Rename blockstate and model to geo_block? strata_block? What if ores were done with an additional layer?
        // Should IGeoTileInfo provide the blockState resource to synchronize the model and texture map?
        ResourceLocation blockState = new ResourceLocation( Strata.modid , "generic_cube" );
        ModelResourceLocation templateModelResource = new ModelResourceLocation( blockState , null );
        IModelRetexturizerMap textureMap = findTextureMap( stoneAndType.getLeft() , stoneAndType.getRight() );
        return new ModelRetexturizer( templateModelResource , textureMap );
    }

    // IResourceManagerReloadListener overrides

    @Override
    public void onResourceManagerReload( IResourceManager resourceManager )
    {
        // Nothing to do
    }

    // Statics

    private static IModelRetexturizerMap findTextureMap( String tileSetName , String type )
    {
        if( type != null )
        {
            try
            {
                return GeoTileSetRegistry.INSTANCE
                    .findTileInfo( tileSetName , Enum.valueOf( TileType.class , type.toUpperCase() ) )
                    .textureMap();
            }
            catch( NullPointerException ex )
            {
                return null;
            }
        }

        // We can't tell primary types apart here
        for( TileType tileType : TileType.values() )
        {
            if( tileType.isPrimary )
            {
                try
                {
                    return GeoTileSetRegistry.INSTANCE
                        .findTileInfo( tileSetName , tileType )
                        .textureMap();
                }
                catch( NullPointerException ex )
                {
                    // This one must not exist in the registry, so try another
                }
            }
        }

        return null;
    }

    private static Pair< String , String > getStoneAndTypePairFromFoundMatch( Matcher match )
    {
        // We don't have the case insensitive version of isValidEnum
        return match.group( 3 ) != null && EnumUtils.isValidEnum( TileType.class , match.group( 3 ).toUpperCase() )
            ? new ImmutablePair<>( match.group( 2 ) , match.group( 3 ) )
            : new ImmutablePair<>( match.group( 1 ) , null );
    }
}
