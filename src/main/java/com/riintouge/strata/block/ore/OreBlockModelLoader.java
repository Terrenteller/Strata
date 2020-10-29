package com.riintouge.strata.block.ore;

import com.riintouge.strata.block.IModelRetexturizerMap;
import com.riintouge.strata.block.ModelRetexturizer;
import com.riintouge.strata.Strata;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ICustomModelLoader;
import net.minecraftforge.client.model.IModel;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OreBlockModelLoader implements ICustomModelLoader
{
    private static final String ResourcePattern = String.format( "^%s:(.+)%s#" , Strata.modid , OreBlock.RegistryNameSuffix );
    private static final int ResourcePatternOreNameGroup = 1;
    private static final Pattern ResourceRegex = Pattern.compile( ResourcePattern );

    // ICustomModelLoader overrides

    @Override
    public boolean accepts( ResourceLocation modelLocation )
    {
        Matcher matcher = ResourceRegex.matcher( modelLocation.toString() );
        if( !matcher.find() )
            return false;

        String oreName = matcher.group( ResourcePatternOreNameGroup );
        return OreRegistry.INSTANCE.contains( oreName );
    }

    @Override
    public IModel loadModel( ResourceLocation modelLocation )
    {
        System.out.println( String.format( "OreBlockModelLoader::loadModel( \"%s\" )" , modelLocation.toString() ) );

        Matcher matcher = ResourceRegex.matcher( modelLocation.toString() );
        matcher.find();

        ResourceLocation blockState = new ResourceLocation( Strata.modid , "generic_cube" );
        ModelResourceLocation templateModelResource = new ModelResourceLocation( blockState , null );
        String oreName = matcher.group( ResourcePatternOreNameGroup );
        IModelRetexturizerMap textureMap = OreRegistry.INSTANCE.find( oreName ).getInfo().modelTextureMap();
        return new ModelRetexturizer( templateModelResource , textureMap );
    }

    // IResourceManagerReloadListener overrides

    @Override
    public void onResourceManagerReload( IResourceManager resourceManager )
    {
        // Nothing to do
    }
}
