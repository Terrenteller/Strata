package com.riintouge.strata.item.ore;

import com.riintouge.strata.Strata;
import com.riintouge.strata.block.ore.OreRegistry;
import com.riintouge.strata.item.StrataItemModel;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ICustomModelLoader;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly( Side.CLIENT )
public final class OreItemModelLoader implements ICustomModelLoader
{
    private static final String MODEL_RESOURCE_BASE_PATH = "models/item/";
    private static final String DOMAIN_RESOURCE_PREFIX = Strata.resource( MODEL_RESOURCE_BASE_PATH ).toString();

    // ICustomModelLoader overrides

    @Override
    public boolean accepts( ResourceLocation modelLocation )
    {
        String oreName = modelLocation.toString().replaceFirst( DOMAIN_RESOURCE_PREFIX , "" );
        return OreRegistry.INSTANCE.contains( oreName );
    }

    @Override
    public IModel loadModel( ResourceLocation modelLocation )
    {
        Strata.LOGGER.trace( String.format( "OreItemModelLoader::loadModel( '%s' )" , modelLocation.toString() ) );

        String oreName = modelLocation.getResourcePath().replaceFirst( MODEL_RESOURCE_BASE_PATH , "" );
        return new StrataItemModel( OreItemTextureManager.getTextureLocation( oreName ) );
    }

    // IResourceManagerReloadListener overrides

    @Override
    public void onResourceManagerReload( IResourceManager resourceManager )
    {
        // Nothing to do
    }
}
