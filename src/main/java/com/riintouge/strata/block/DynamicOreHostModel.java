package com.riintouge.strata.block;

import com.riintouge.strata.GenericOreRegistry;
import com.riintouge.strata.property.UnlistedPropertyHostRock;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
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
    public List< BakedQuad > getQuads( @Nullable IBlockState state , @Nullable EnumFacing side , long rand )
    {
        if( side == null || !( state instanceof IExtendedBlockState ) )
            return originalModel.getQuads( state , side , rand );

        String oreName = state.getBlock().getRegistryName().getResourcePath();
        if( !GenericOreRegistry.INSTANCE.contains( oreName ) )
            oreName = DynamicOreHostManager.INSTANCE.getOreName( state );
            // Use this instead when DynamicOreHostManager is reworked
            //return originalModel.getQuads( state , side , rand );

        String host = StateUtil.getValue( state , UnlistedPropertyHostRock.PROPERTY , UnlistedPropertyHostRock.DEFAULT );
        TextureAtlasSprite hostTexture = DynamicOreHostManager.INSTANCE.getGeneratedTexture( oreName , host );
        List< BakedQuad > newQuads = new Vector<>();
        newQuads.add( BakedQuadUtil.createBakedQuadForFace( 0 , hostTexture , side ) );

        return newQuads;
    }

    @Override
    public ItemOverrideList getOverrides()
    {
        return ItemOverrideList.NONE;
    }

    @Override
    public TextureAtlasSprite getParticleTexture()
    {
        // FIXME: This will ultimately return the ore overlay instead of the host.
        // Not a deal breaker, but could stand to be improved.
        return originalModel.getParticleTexture();
    }

    @Override
    public boolean isAmbientOcclusion()
    {
        return true;
    }

    @Override
    public boolean isBuiltInRenderer()
    {
        return false;
    }

    @Override
    public boolean isGui3d()
    {
        return true;
    }
}
