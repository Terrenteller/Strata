package com.riintouge.strata.block.geo;

import com.riintouge.strata.Strata;
import com.riintouge.strata.block.IResourceLocationMap;
import com.riintouge.strata.block.ModelRetexturizer;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ICustomModelLoader;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SideOnly( Side.CLIENT )
public class GeoBlockModelLoader implements ICustomModelLoader
{
    private static final String ResourcePattern = String.format( "^%s:(([a-z_]+?)(?:_([a-z]+))?)(?:#.+)$" , Strata.modid );
    private static final int ResourcePatternResourceLocationPathGroup = 1;
    private static final int ResourcePatternTileSetNameGroup = 2;
    private static final int ResourcePatternTileTypeGroup = 3;
    private static final Pattern ResourceRegex = Pattern.compile( ResourcePattern );

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
        System.out.println( String.format( "GeoBlockModelLoader::loadModel( \"%s\" )" , modelResourceLocation.toString() ) );

        Pair< String , TileType > stoneAndType = getNameAndTileTypeFromLocation( modelLocation );
        TileType tileType = stoneAndType.getRight();
        IGeoTileInfo tileInfo = GeoTileSetRegistry.INSTANCE.findTileInfo( stoneAndType.getLeft() , tileType );
        ModelResourceLocation templateModelResource = new ModelResourceLocation( tileInfo.blockstateResourceLocation() , modelResourceLocation.getVariant() );
        IResourceLocationMap textureMap = tileInfo.modelTextureMap();
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
        if( !GeoTileSetRegistry.INSTANCE.contains( tileSetName ) )
            return null;

        if( type != null && !type.isEmpty() )
        {
            try
            {
                TileType tileType = Enum.valueOf( TileType.class , type.toUpperCase() );
                // Tertiary types cannot be validated to exist in the registry
                return tileType.parentType != null
                    ? tileType
                    : GeoTileSetRegistry.INSTANCE.findTileInfo( tileSetName , tileType ) != null ? tileType : null;
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

    private static Pair< String , TileType > getNameAndTileTypeFromLocation( ResourceLocation modelLocation )
    {
        Matcher matcher = ResourceRegex.matcher( modelLocation.toString() );
        if( !matcher.find() )
            return null;

        String tileTypeName = matcher.group( ResourcePatternTileTypeGroup );
        if( tileTypeName != null && EnumUtils.isValidEnum( TileType.class , tileTypeName.toUpperCase() ) )
        {
            String tileSetName = matcher.group( ResourcePatternTileSetNameGroup );
            TileType tileType = findTileType( tileSetName , tileTypeName );
            if( tileType != null )
                return new ImmutablePair<>( tileSetName , tileType );
        }

        // The resource name for a primary type may end with a primary type enum value and fail the above search
        String resourcePath = matcher.group( ResourcePatternResourceLocationPathGroup );
        TileType tileType = findTileType( resourcePath , null );
        return new ImmutablePair<>( resourcePath , tileType );
    }
}
