package com.riintouge.strata.block.geo;

import com.riintouge.strata.Strata;
import com.riintouge.strata.block.ProtoBlockTextureMap;
import com.riintouge.strata.block.ParticleHelper;
import net.minecraft.block.BlockStairs;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class GeoBlockStairs extends BlockStairs
{
    protected IGeoTileInfo info;

    public GeoBlockStairs( IGeoTileInfo info , IBlockState blockState )
    {
        super( blockState );
        this.info = info;
        // This is what Forge does for BlockStairs in Block.registerBlocks()
        this.useNeighborBrightness = true;

        ResourceLocation registryName = info.registryName();
        setRegistryName( registryName );
        setUnlocalizedName( registryName.toString() );
        setCreativeTab( Strata.BUILDING_BLOCK_TAB );
    }

    // Block overrides

    @Override
    @SideOnly( Side.CLIENT )
    public boolean addDestroyEffects( World world , BlockPos pos , ParticleManager manager )
    {
        ProtoBlockTextureMap hostTextureMap = info.modelTextureMap();
        ParticleHelper.addDestroyEffects( world , pos , manager , RANDOM , hostTextureMap );

        return true;
    }

    @Override
    @SideOnly( Side.CLIENT )
    public boolean addHitEffects( IBlockState state , World worldObj , RayTraceResult target , ParticleManager manager )
    {
        TextureAtlasSprite texture = info.modelTextureMap().getTexture( target.sideHit );
        ParticleHelper.createHitParticle( state , worldObj , target , manager , RANDOM , texture );

        return true;
    }
}
