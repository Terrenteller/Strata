package com.riintouge.strata.block.ore;

import com.riintouge.strata.block.MetaResourceLocation;
import com.riintouge.strata.block.geo.HostRegistry;
import com.riintouge.strata.util.OverlayBakedQuadUtil;
import com.riintouge.strata.util.StateUtil;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.common.property.IExtendedBlockState;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Vector;

public class OreBlockModel implements IBakedModel
{
    protected IOreTileSet oreTileSet;
    protected IBakedModel originalModel;
    protected BakedQuad[] oreQuads = new BakedQuad[ EnumFacing.values().length ];

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

        // renderLayer is null when pistons are involved
        BlockRenderLayer renderLayer = MinecraftForgeClient.getRenderLayer();
        List< BakedQuad > newQuads = new Vector<>();

        if( renderLayer == BlockRenderLayer.SOLID || renderLayer == null )
        {
            MetaResourceLocation host = StateUtil.getValue( state , UnlistedPropertyHostRock.PROPERTY , UnlistedPropertyHostRock.DEFAULT );
            IBakedModel hostModel = HostRegistry.INSTANCE.getBakedModel( host );
            Block hostBlock = Block.REGISTRY.getObject( host.resourceLocation );
            IBlockState hostState = hostBlock.getStateFromMeta( host.meta );

            // When hostModel is WeightedBakedModel, the value of rand has a major role in overlay Z-fighting.
            // Stone, netherrack, and dirt blockstates (at least) specify random rotations to break up monotony.
            // This is good, but some rotations (non-default?) cause additional quads to Z-fight.
            // Using a zero value for rand (because it's actually a long) appears to solve the problem.
            // However, I cannot prove it solves the problem in all cases.
            // Therefore, continue passing rand to satisfy the purpose of WeightedBakedModel
            // and use OverlayBakedQuadUtil to produce special quads with an anti-Z-fighting fudge factor.
            // It's not the solution I was hoping for and is noticeable in-game if you're looking for it,
            // but the flexibility of allowing the host to draw itself is worth the "ehh".
            newQuads.addAll( hostModel.getQuads( hostState , side , rand ) );
        }

        if( renderLayer == BlockRenderLayer.TRANSLUCENT || renderLayer == null )
        {
            BakedQuad quad = oreQuads[ side.ordinal() ];
            if( quad == null )
            {
                ResourceLocation textureResourceLocation = oreTileSet.getInfo().modelTextureMap().get( side );
                TextureAtlasSprite texture = Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite( textureResourceLocation.toString() );
                quad = OverlayBakedQuadUtil.createBakedQuadForFace( 0 , texture , side );
                oreQuads[ side.ordinal() ] = quad;
            }

            newQuads.add( quad );
        }

        return newQuads.size() > 0 ? newQuads : originalModel.getQuads( state , side , rand );
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
        // OreBlock.addDestroyEffects() and OreBlock.addHitEffects() will spawn particles with the host.
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
