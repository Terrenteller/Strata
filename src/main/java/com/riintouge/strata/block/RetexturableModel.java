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

// GenericModel? We aren't required to retexture it and instead use it to duplicate block states...
public final class RetexturableModel implements IModel
{
    private final ModelResourceLocation templateModelResource;
    private final ResourceLocation textureResource;

    public RetexturableModel( ModelResourceLocation templateModelResource , ResourceLocation textureResource )
    {
        this.templateModelResource = templateModelResource;
        this.textureResource = textureResource;
    }

    @Override
    public Collection<ResourceLocation> getDependencies()
    {
        return ImmutableList.of( templateModelResource );
    }

    @Override
    public Collection<ResourceLocation> getTextures()
    {
        return ImmutableList.of( textureResource );
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
            textureResource == null
                ? bakedTextureGetter
                : ( resourceLocation ) -> bakedTextureGetter.apply( textureResource ) );
    }
}
