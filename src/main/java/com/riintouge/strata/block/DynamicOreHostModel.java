package com.riintouge.strata.block;

import com.riintouge.strata.property.UnlistedPropertyHostRock;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.property.IExtendedBlockState;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Vector;

public class DynamicOreHostModel implements IBakedModel
{
    private IBakedModel originalModel;

    public DynamicOreHostModel( IBakedModel originalModel )
    {
        this.originalModel = originalModel;
    }

    @Override
    public List<BakedQuad> getQuads( @Nullable IBlockState state , @Nullable EnumFacing side , long rand )
    {
        // When side is null is it the inventory item?
        if( side == null || !( state instanceof IExtendedBlockState ) )
            return originalModel.getQuads( state , side , rand );

        String ore = DynamicOreHostManager.INSTANCE.getOreName( state );
        String host = StateUtil.getValue( state , UnlistedPropertyHostRock.PROPERTY , UnlistedPropertyHostRock.DEFAULT );

        TextureAtlasSprite hostOreSprite = DynamicOreHostManager.INSTANCE.getGeneratedTexture( ore , host );
        List<BakedQuad> newQuads = new Vector<>();
        newQuads.add( BakedQuadUtil.createBakedQuadForFace( 0 , hostOreSprite , side ) );

        return newQuads;
    }

    @Override
    public ItemOverrideList getOverrides()
    {
        return originalModel.getOverrides();
    }

    @Override
    public TextureAtlasSprite getParticleTexture()
    {
        return originalModel.getParticleTexture();
    }

    @Override
    public boolean isAmbientOcclusion()
    {
        return originalModel.isAmbientOcclusion();
    }

    @Override
    public boolean isBuiltInRenderer()
    {
        return originalModel.isBuiltInRenderer();
    }

    @Override
    public boolean isGui3d()
    {
        return originalModel.isGui3d();
    }
}
