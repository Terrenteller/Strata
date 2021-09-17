package com.riintouge.strata.block.geo;

import com.riintouge.strata.Strata;
import com.riintouge.strata.item.StrataItemModel;
import net.minecraft.block.material.Material;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ICustomModelLoader;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SideOnly( Side.CLIENT )
public class GeoItemFragmentModelLoader implements ICustomModelLoader
{
    private static final String ResourcePattern = String.format( "^%s:models/item/(.+)_([^_]+)$" , Strata.modid );
    private static final int ResourcePatternHostNameGroup = 1;
    private static final int ResourcePatternFragmentTypeGroup = 2;
    private static final Pattern ResourceRegex = Pattern.compile( ResourcePattern );

    // ICustomModelLoader overrides

    @Override
    public boolean accepts( ResourceLocation modelLocation )
    {
        Matcher matcher = ResourceRegex.matcher( modelLocation.toString() );
        if( !matcher.find() )
            return false;

        String hostName = matcher.group( ResourcePatternHostNameGroup );
        String fragmentTypeName = matcher.group( ResourcePatternFragmentTypeGroup );
        Material material = GeoItemFragment.getMaterialForType( fragmentTypeName );
        if( material == Material.CLAY )
            return GeoTileSetRegistry.INSTANCE.findTileInfo( hostName , TileType.CLAY ) != null;

        return false;
    }

    @Override
    public IModel loadModel( ResourceLocation modelLocation )
    {
        System.out.println( String.format( "GeoItemFragmentModelLoader::loadModel( \"%s\" )" , modelLocation.toString() ) );

        Matcher matcher = ResourceRegex.matcher( modelLocation.toString() );
        matcher.find();
        String hostName = matcher.group( ResourcePatternHostNameGroup );
        String fragmentTypeName = matcher.group( ResourcePatternFragmentTypeGroup );
        Material material = GeoItemFragment.getMaterialForType( fragmentTypeName );
        if( material == Material.CLAY )
        {
            IHostInfo hostInfo = GeoTileSetRegistry.INSTANCE.findTileInfo( hostName , TileType.CLAY );
            return new StrataItemModel( GeoItemFragmentTextureManager.getTextureLocation( hostInfo , TileType.CLAY ) );
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
