package com.riintouge.strata.block.ore;

import com.riintouge.strata.block.IResourceLocationMap;
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
        Strata.LOGGER.trace( String.format( "OreBlockModelLoader::loadModel( \"%s\" )" , modelLocation.toString() ) );

        Matcher matcher = ResourceRegex.matcher( modelLocation.toString() );
        matcher.find();

        String oreName = matcher.group( ResourcePatternOreNameGroup );
        IOreInfo oreInfo = OreRegistry.INSTANCE.find( oreName ).getInfo();
        ModelResourceLocation templateModelResource = new ModelResourceLocation( oreInfo.blockstateResourceLocation() , null );
        IResourceLocationMap textureMap = oreInfo.modelTextureMap();
        return new ModelRetexturizer( templateModelResource , textureMap );
    }

    // IResourceManagerReloadListener overrides

    @Override
    public void onResourceManagerReload( IResourceManager resourceManager )
    {
        // Nothing to do
    }
}
