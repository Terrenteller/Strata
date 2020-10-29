package com.riintouge.strata.block.ore;

import com.google.common.collect.ImmutableMap;
import com.riintouge.strata.Strata;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ModelBlock;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ICustomModelLoader;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ItemLayerModel;

import java.util.ArrayList;

// TODO: Adjust this class to accept and create flat and full block models depending on overlay transparency.
// e.g. fully opaque textures become blocks while anything else is rendered as a flat item.
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

        ImmutableMap.Builder< String , String > textures = ImmutableMap.builder();
        String oreName = modelLocation.getResourcePath().replaceFirst( ModelResourceBasePath , "" );
        textures.put( "layer0" , OreItemTextureManager.getTextureLocation( oreName ).toString() );

        ModelBlock blockModel = new ModelBlock(
            new ResourceLocation( "item/generated" ),
            new ArrayList<>(),
            textures.build(),
            true,
            true,
            ItemCameraTransforms.DEFAULT,
            new ArrayList<>() );
        return new ItemLayerModel( blockModel );
    }

    // IResourceManagerReloadListener overrides

    @Override
    public void onResourceManagerReload( IResourceManager resourceManager )
    {
        // Nothing to do
    }
}
