package com.riintouge.strata.block;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
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
    private final ModelResourceLocation templateModelResource;
    private final ResourceLocation textureResource;
    private final IModelRetexturizerMap textureMap;

    public ModelRetexturizer( ModelResourceLocation templateModelResource , ResourceLocation textureResource )
    {
        this.templateModelResource = templateModelResource;
        this.textureMap = null;
        this.textureResource = textureResource;
    }

    public ModelRetexturizer( ModelResourceLocation templateModelResource , IModelRetexturizerMap textureMap )
    {
        this.templateModelResource = templateModelResource;
        this.textureMap = textureMap;
        this.textureResource = null;
    }

    @Override
    public Collection< ResourceLocation > getDependencies()
    {
        return ImmutableList.of( templateModelResource );
    }

    @Override
    public Collection< ResourceLocation > getTextures()
    {
        if( textureResource != null )
            return ImmutableList.of( textureResource );

        return textureMap.getAll();
    }

    @Override
    public IBakedModel bake(
        IModelState state,
        VertexFormat format,
        Function< ResourceLocation , TextureAtlasSprite > bakedTextureGetter )
    {
        IModel model = ModelLoaderRegistry.getModelOrLogError(
            templateModelResource,
            "Couldn't load template model: " + templateModelResource );
        return model.bake(
            new ModelStateComposition( state , model.getDefaultState() ),
            format,
            textureResource != null
                ? ( resourceLocation ) -> bakedTextureGetter.apply( textureResource )
                : ( resourceLocation ) -> bakedTextureGetter.apply( textureMap.getOrDefault( resourceLocation ) ) );
    }
}
