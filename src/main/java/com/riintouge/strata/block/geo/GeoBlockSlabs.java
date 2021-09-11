package com.riintouge.strata.block.geo;

import com.riintouge.strata.block.GenericCubeTextureMap;
import com.riintouge.strata.block.ParticleHelper;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class GeoBlockSlabs extends GeoBlockSlab
{
    protected IGeoTileInfo info;

    public GeoBlockSlabs( IGeoTileInfo info , GeoBlockSlab singleSlab )
    {
        super( info , singleSlab );
        this.info = info;
    }

    // BlockSlab overrides

    @Override
    public boolean isDouble()
    {
        return true;
    }

    // Block overrides

    @Override
    @SideOnly( Side.CLIENT )
    public boolean addDestroyEffects( World world , BlockPos pos , ParticleManager manager )
    {
        GenericCubeTextureMap hostTextureMap = info.modelTextureMap();
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
