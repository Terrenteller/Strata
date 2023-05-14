package com.riintouge.strata.item;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.BakedItemModel;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ItemLayerModel;
import net.minecraftforge.common.model.IModelState;

import java.util.Collection;
import java.util.function.Function;

public class StrataItemModel implements IModel
{
    protected ResourceLocation textureResource;

    public StrataItemModel( ResourceLocation textureResource )
    {
        this.textureResource = textureResource;
    }

    // IModel overrides

    @Override
    public IBakedModel bake(
        IModelState state,
        VertexFormat format,
        Function< ResourceLocation , TextureAtlasSprite > bakedTextureGetter )
    {
        ItemLayerModel itemLayerModel = new ItemLayerModel( ImmutableList.of( textureResource ) );
        IBakedModel bakedModel = itemLayerModel.bake( state , format , bakedTextureGetter );
        ImmutableList.Builder< BakedQuad > builder = ImmutableList.builder();
        builder.addAll( bakedModel.getQuads( null , null , 0 ) );

        return new BakedItemModel(
            builder.build(),
            bakedModel.getParticleTexture(),
            Maps.immutableEnumMap( StrataItemCameraTransform.getTransforms( state ) ),
            ItemOverrideList.NONE );
    }

    @Override
    public Collection< ResourceLocation > getTextures()
    {
        return ImmutableList.of( textureResource );
    }
}
