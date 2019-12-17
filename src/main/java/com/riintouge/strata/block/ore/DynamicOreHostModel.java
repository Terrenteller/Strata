package com.riintouge.strata.block.ore;

import com.riintouge.strata.util.BakedQuadUtil;
import com.riintouge.strata.util.StateUtil;
import com.riintouge.strata.block.MetaResourceLocation;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
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

    // IBakedModel overrides

    @Override
    public List< BakedQuad > getQuads( @Nullable IBlockState state , @Nullable EnumFacing side , long rand )
    {
        if( side == null || !( state instanceof IExtendedBlockState ) )
            return originalModel.getQuads( state , side , rand );

        Block block = state.getBlock();
        ResourceLocation registryName = block.getRegistryName();
        String oreName = registryName.getResourcePath();
        if( !GenericOreRegistry.INSTANCE.contains( oreName ) )
            return originalModel.getQuads( state , side , rand );

        MetaResourceLocation host = StateUtil.getValue( state , UnlistedPropertyHostRock.PROPERTY , UnlistedPropertyHostRock.DEFAULT );
        TextureAtlasSprite hostTexture = DynamicOreHostManager.INSTANCE.findTexture(
            registryName,
            block.getMetaFromState( state ),
            host.resourceLocation,
            host.meta );
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
