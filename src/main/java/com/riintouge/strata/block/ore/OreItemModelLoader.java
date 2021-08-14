package com.riintouge.strata.block.ore;

import com.riintouge.strata.Strata;
import com.riintouge.strata.item.StrataItemModel;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ICustomModelLoader;
import net.minecraftforge.client.model.IModel;

public class OreItemModelLoader implements ICustomModelLoader
{
    private static final String ModelResourceBasePath = "models/item/";
    private static final String DomainResourcePrefix = Strata.resource( ModelResourceBasePath ).toString();

    // ICustomModelLoader overrides

    @Override
    public boolean accepts( ResourceLocation modelLocation )
    {
        String oreName = modelLocation.toString().replaceFirst( DomainResourcePrefix , "" );
        return OreRegistry.INSTANCE.contains( oreName );
    }

    @Override
    public IModel loadModel( ResourceLocation modelLocation )
    {
        System.out.println( String.format( "OreItemModelLoader::loadModel( \"%s\" )" , modelLocation.toString() ) );

        String oreName = modelLocation.getResourcePath().replaceFirst( ModelResourceBasePath , "" );
        return new StrataItemModel( OreItemTextureManager.getTextureLocation( oreName ) );
    }

    // IResourceManagerReloadListener overrides

    @Override
    public void onResourceManagerReload( IResourceManager resourceManager )
    {
        // Nothing to do
    }
}
