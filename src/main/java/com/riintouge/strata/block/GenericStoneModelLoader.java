package com.riintouge.strata.block;

import com.riintouge.strata.RetexturableModel;
import com.riintouge.strata.Strata;
import com.riintouge.strata.GenericStoneRegistry;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ICustomModelLoader;
import net.minecraftforge.client.model.IModel;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GenericStoneModelLoader implements ICustomModelLoader
{
    private static final Pattern ModelLocationPattern = Pattern.compile( "strata:(([a-z]+)(?:_([a-z]+))?)" );

    // ICustomModelLoader overrides

    @Override
    public boolean accepts( ResourceLocation modelLocation )
    {
        Matcher matcher = ModelLocationPattern.matcher( modelLocation.toString() );
        return matcher.find() && GenericStoneRegistry.INSTANCE.find( matcher.group( 2 ) ) != null;
    }

    @Override
    public IModel loadModel( ResourceLocation modelLocation )
    {
        System.out.println( String.format( "GenericStoneModelLoader::loadModel( \"%s\" )" , modelLocation.toString() ) );

        Matcher matcher = ModelLocationPattern.matcher( modelLocation.toString() );
        matcher.find();
        ResourceLocation generatedTextureResource = new ResourceLocation( Strata.modid , matcher.group( 1 ) );
        System.out.println( generatedTextureResource.toString() );
        String blockType = matcher.group( 3 );

        if( blockType == null )
            blockType = "stone";

        ResourceLocation blockState = new ResourceLocation( Strata.modid , "generic_" + blockType );
        ModelResourceLocation templateModelResource = new ModelResourceLocation( blockState , null );
        return new RetexturableModel( templateModelResource , generatedTextureResource );
    }

    // IResourceManagerReloadListener overrides

    @Override
    public void onResourceManagerReload( IResourceManager resourceManager )
    {
        // Nothing to do
    }
}
