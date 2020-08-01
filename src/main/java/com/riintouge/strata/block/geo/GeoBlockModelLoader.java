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
        Pair< String , String > stoneAndType = getStoneAndTypePairFromLocation( modelLocation );
        return stoneAndType != null && findTileType( stoneAndType.getLeft() , stoneAndType.getRight() ) != null;
    }

    @Override
    public IModel loadModel( ResourceLocation modelLocation )
    {
        ModelResourceLocation modelResourceLocation = (ModelResourceLocation)modelLocation;
        System.out.println( String.format( "GeoBlockModelLoader::loadModel( \"%s\" )" , modelResourceLocation.toString() ) );

        Pair< String , String > stoneAndType = getStoneAndTypePairFromLocation( modelLocation );
        TileType tileType = findTileType( stoneAndType.getLeft() , stoneAndType.getRight() );
        ModelResourceLocation templateModelResource = new ModelResourceLocation( tileType.modelName , modelResourceLocation.getVariant() );
        TileType primaryOrSecondaryType = tileType.parentType != null ? tileType.parentType : tileType;
        IGeoTileInfo tileInfo = GeoTileSetRegistry.INSTANCE.findTileInfo( stoneAndType.getLeft() , primaryOrSecondaryType );
        IModelRetexturizerMap textureMap = tileInfo.modelTextureMap();
        return new ModelRetexturizer( templateModelResource , textureMap );
    }

    // IResourceManagerReloadListener overrides

    @Override
    public void onResourceManagerReload( IResourceManager resourceManager )
    {
        // Nothing to do
    }

    // Statics

    private static TileType findTileType( String tileSetName , String type )
    {
        if( type != null && !type.isEmpty() )
        {
            try
            {
                return Enum.valueOf( TileType.class , type.toUpperCase() );
            }
            catch( IllegalArgumentException ex )
            {
                return null;
            }
        }

        // No type, so must be a primary. Let GeoTileSetRegistry figure out which one.
        IGeoTileInfo tileInfo = GeoTileSetRegistry.INSTANCE.findTileInfo( tileSetName , type );
        return tileInfo != null ? tileInfo.type() : null;
    }

    private static Pair< String , String > getStoneAndTypePairFromLocation( ResourceLocation modelLocation )
    {
        Matcher matcher = ResourceRegex.matcher( modelLocation.toString() );
        if( !matcher.find() )
            return null;

        // We don't have the case insensitive version of isValidEnum
        return matcher.group( 3 ) != null && EnumUtils.isValidEnum( TileType.class , matcher.group( 3 ).toUpperCase() )
            ? new ImmutablePair<>( matcher.group( 2 ) , matcher.group( 3 ) )
            : new ImmutablePair<>( matcher.group( 1 ) , null );
    }
}
