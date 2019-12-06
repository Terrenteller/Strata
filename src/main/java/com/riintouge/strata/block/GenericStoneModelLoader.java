package com.riintouge.strata.block;

import com.riintouge.strata.RetexturableModel;
import com.riintouge.strata.Strata;
import com.riintouge.strata.GenericStoneRegistry;
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

public class GenericStoneModelLoader implements ICustomModelLoader
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
        return GenericStoneRegistry.INSTANCE.find( stoneAndType.getLeft() ) != null;
    }

    @Override
    public IModel loadModel( ResourceLocation modelLocation )
    {
        System.out.println( String.format( "GenericStoneModelLoader::loadModel( \"%s\" )" , modelLocation.toString() ) );

        Matcher matcher = ResourceRegex.matcher( modelLocation.toString() );
        matcher.find();

        Pair< String , String > stoneAndType = getStoneAndTypePairFromFoundMatch( matcher );
        ResourceLocation blockState = new ResourceLocation( Strata.modid , "generic_" + stoneAndType.getRight() );
        ModelResourceLocation templateModelResource = new ModelResourceLocation( blockState , null );
        ResourceLocation generatedTextureResource = new ResourceLocation( Strata.modid , matcher.group( 1 ) );
        return new RetexturableModel( templateModelResource , generatedTextureResource );
    }

    // IResourceManagerReloadListener overrides

    @Override
    public void onResourceManagerReload( IResourceManager resourceManager )
    {
        // Nothing to do
    }

    // Statics

    private Pair< String , String > getStoneAndTypePairFromFoundMatch( Matcher match )
    {
        // We don't have the case insensitive version of isValidEnum
        return match.group( 3 ) != null && EnumUtils.isValidEnum( StoneBlockType.class , match.group( 3 ).toUpperCase() )
            ? new ImmutablePair<>( match.group( 2 ) , match.group( 3 ) )
            : new ImmutablePair<>( match.group( 1 ) , "stone" );
    }
}
