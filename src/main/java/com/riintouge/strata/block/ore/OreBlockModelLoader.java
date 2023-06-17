package com.riintouge.strata.block.ore;

import com.riintouge.strata.misc.IResourceLocationMap;
import com.riintouge.strata.block.ModelRetexturizer;
import com.riintouge.strata.Strata;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ICustomModelLoader;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SideOnly( Side.CLIENT )
public final class OreBlockModelLoader implements ICustomModelLoader
{
    private static final String RESOURCE_REGEX = String.format( "^%s:(.+)%s#" , Strata.MOD_ID , OreBlock.REGISTRY_NAME_SUFFIX );
    private static final Pattern RESOURCE_PATTERN = Pattern.compile( RESOURCE_REGEX );
    private static final int ORE_NAME_GROUP = 1;

    // ICustomModelLoader overrides

    @Override
    public boolean accepts( ResourceLocation modelLocation )
    {
        Matcher matcher = RESOURCE_PATTERN.matcher( modelLocation.toString() );
        if( !matcher.find() )
            return false;

        String oreName = matcher.group( ORE_NAME_GROUP );
        return OreRegistry.INSTANCE.contains( oreName );
    }

    @Override
    public IModel loadModel( ResourceLocation modelLocation )
    {
        String modelLocationString = modelLocation.toString();
        Strata.LOGGER.trace( String.format( "OreBlockModelLoader::loadModel( '%s' )" , modelLocationString ) );

        Matcher matcher = RESOURCE_PATTERN.matcher( modelLocationString );
        if( !matcher.find() )
            throw new IllegalArgumentException( modelLocationString );

        String oreName = matcher.group( ORE_NAME_GROUP );
        IOreInfo oreInfo = OreRegistry.INSTANCE.find( oreName ).getInfo();
        ModelResourceLocation originalModelResource = new ModelResourceLocation( oreInfo.blockStateResourceLocation() , null );
        IResourceLocationMap textureMap = oreInfo.modelTextureMap();
        return new ModelRetexturizer( originalModelResource , textureMap );
    }

    // IResourceManagerReloadListener overrides

    @Override
    public void onResourceManagerReload( IResourceManager resourceManager )
    {
        // Nothing to do
    }
}
