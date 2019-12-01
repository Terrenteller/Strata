package com.riintouge.strata.item;

import com.google.common.collect.ImmutableMap;
import com.riintouge.strata.Strata;
import com.riintouge.strata.block.ResourceUtil;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ModelBlock;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ICustomModelLoader;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ItemLayerModel;

import java.util.ArrayList;

public class OreItemModelLoader implements ICustomModelLoader
{
    private static final String ItemNamePrefix = "ore_";
    private static final String ResourcePrefix = ResourceUtil.ModelResourceBasePath + ItemNamePrefix;
    private static final String DomainResourcePrefix = Strata.modid + ":" + ResourcePrefix;

    // ICustomModelLoader overrides

    @Override
    public boolean accepts( ResourceLocation modelLocation )
    {
        return modelLocation.toString().startsWith( DomainResourcePrefix );
    }

    @Override
    public IModel loadModel( ResourceLocation modelLocation ) throws Exception
    {
        //System.out.println( String.format( "OreItemModelLoader::loadModel( \"%s\" )" , modelLocation.toString() ) );

        ImmutableMap.Builder< String , String > textures = ImmutableMap.builder();
        String oreName = modelLocation.getResourcePath().replaceAll( ResourcePrefix , "" );
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

    // Statics

    public static ModelResourceLocation getModelResourceLocation( String oreName )
    {
        return new ModelResourceLocation( new ResourceLocation( Strata.modid , ItemNamePrefix + oreName ) , "inventory" );
    }
}
