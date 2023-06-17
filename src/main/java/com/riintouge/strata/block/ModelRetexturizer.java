package com.riintouge.strata.block;

import com.google.common.collect.ImmutableList;
import com.riintouge.strata.misc.IResourceLocationMap;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.client.model.ModelStateComposition;
import net.minecraftforge.common.model.IModelState;

import java.util.Collection;
import java.util.function.Function;

public class ModelRetexturizer implements IModel
{
    private final ResourceLocation originalModelResource;
    private final IResourceLocationMap textureMap;

    public ModelRetexturizer( ResourceLocation originalModelResource , IResourceLocationMap textureMap )
    {
        this.originalModelResource = originalModelResource;
        this.textureMap = textureMap;
    }

    // IModel overrides

    @Override
    public IBakedModel bake(
        IModelState state,
        VertexFormat format,
        Function< ResourceLocation , TextureAtlasSprite > bakedTextureGetter )
    {
        IModel model = ModelLoaderRegistry.getModelOrLogError(
            originalModelResource,
            String.format( "Couldn't load original model '%s'" , originalModelResource ) );
        return model.bake(
            new ModelStateComposition( state , model.getDefaultState() ),
            format,
            ( resourceLocation ) -> bakedTextureGetter.apply( textureMap.get( resourceLocation ) ) );
    }

    @Override
    public Collection< ResourceLocation > getDependencies()
    {
        return ImmutableList.of( originalModelResource );
    }

    @Override
    public Collection< ResourceLocation > getTextures()
    {
        return textureMap.getAll();
    }
}
