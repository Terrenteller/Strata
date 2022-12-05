package com.riintouge.strata.block.ore;

import com.riintouge.strata.block.MetaResourceLocation;
import com.riintouge.strata.block.geo.BakedModelCache;
import com.riintouge.strata.util.StateUtil;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Vector;

import static com.riintouge.strata.block.ore.OreBlockTileEntity.DAMAGE_MODEL_FACE_COUNT_HACK;

@SideOnly( Side.CLIENT )
public class OreBlockModel implements IBakedModel
{
    protected IOreTileSet oreTileSet;
    protected IBakedModel originalModel;

    public OreBlockModel( IOreTileSet oreTileSet , IBakedModel originalModel )
    {
        this.oreTileSet = oreTileSet;
        this.originalModel = originalModel;
    }

    // IBakedModel overrides

    @Override
    public List< BakedQuad > getQuads( @Nullable IBlockState state , @Nullable EnumFacing side , long rand )
    {
        if( side == null || !( state instanceof IExtendedBlockState ) )
            return originalModel.getQuads( state , side , rand );

        // renderLayer is null when the damage model is being constructed or when pistons are involved
        BlockRenderLayer renderLayer = MinecraftForgeClient.getRenderLayer();
        List< BakedQuad > newQuads = new Vector<>();

        if( DAMAGE_MODEL_FACE_COUNT_HACK.get() > 0 )
        {
            DAMAGE_MODEL_FACE_COUNT_HACK.set( DAMAGE_MODEL_FACE_COUNT_HACK.get() - 1 );
        }
        else if( renderLayer == BlockRenderLayer.SOLID || renderLayer == null )
        {
            MetaResourceLocation host = StateUtil.getValue( state , UnlistedPropertyHostRock.PROPERTY , UnlistedPropertyHostRock.DEFAULT );
            IBakedModel hostModel = BakedModelCache.INSTANCE.getBakedModel( host );
            Block hostBlock = Block.REGISTRY.getObject( host.resourceLocation );
            IBlockState hostState = hostBlock.getStateFromMeta( host.meta );

            // When hostModel is WeightedBakedModel, the value of rand has a major role in overlay Z-fighting.
            // Stone, netherrack, and dirt blockstates (at least) specify random rotations to break up monotony.
            // This is good, but some rotations (non-default?) cause additional quads to Z-fight.
            // Using a zero value for rand (because it's actually a long) solves the problem for unknown reasons.
            // However, I cannot prove it solves the problem in all cases. Therefore, continue passing rand to reap
            // the benefits of WeightedBakedModel. Ore blocks will use a model with an anti-Z-fighting fudge factor.
            // It's not the solution I was hoping for and is noticeable in-game if you're looking for it,
            // but the flexibility of allowing the host and ore to draw separately is worth the "ehh".
            newQuads.addAll( hostModel.getQuads( hostState , side , rand ) );
        }

        if( renderLayer == BlockRenderLayer.TRANSLUCENT || renderLayer == null )
            newQuads.addAll( originalModel.getQuads( state , side , rand ) );

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
        // This will return the ore overlay because we have no block state per ore/host combo.
        // OreBlock.addDestroyEffects(), OreBlock.addHitEffects(), and OreBlock.addLandingEffects()
        // will spawn particles with the host. Running on the ore block will still involve this method.
        return oreTileSet.getInfo().modelTextureMap().getTexture( EnumFacing.UP );
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
