package com.riintouge.strata.block.geo;

import com.riintouge.strata.Strata;
import com.riintouge.strata.block.ProtoBlockTextureMap;
import com.riintouge.strata.block.ParticleHelper;
import net.minecraft.block.BlockPressurePlate;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class GeoBlockPressurePlate extends BlockPressurePlate
{
    protected IGeoTileInfo info;

    public GeoBlockPressurePlate( IGeoTileInfo info )
    {
        // Match vanilla stone pressure plate sensitivity
        super( info.material() , Sensitivity.MOBS );
        this.info = info;

        ResourceLocation registryName = info.registryName();
        setRegistryName( registryName );
        setUnlocalizedName( registryName.toString() );
        setCreativeTab( Strata.MISC_BLOCK_TAB );

        // Vanilla sets a precedent with gold pressure plates.
        // Gold blocks require an iron pick or higher to mine but any level pick will break the plate.
        // This justification is extended to Strata buttons and levers as well.
        setHarvestLevel( info.harvestTool() , 0 );
        setSoundType( info.soundType() );
        setHardness( info.hardness() );
        setResistance( info.explosionResistance() );
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
