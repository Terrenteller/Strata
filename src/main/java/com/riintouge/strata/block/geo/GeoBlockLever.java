package com.riintouge.strata.block.geo;

import com.riintouge.strata.block.ParticleHelper;
import com.riintouge.strata.gui.StrataCreativeTabs;
import net.minecraft.block.BlockLever;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Supplier;

public class GeoBlockLever extends BlockLever
{
    protected IGeoTileInfo tileInfo;

    public GeoBlockLever( IGeoTileInfo tileInfo )
    {
        this.tileInfo = tileInfo;

        ResourceLocation registryName = tileInfo.registryName();
        setCreativeTab( StrataCreativeTabs.MISC_BLOCK_TAB );
        setHardness( tileInfo.hardness() );
        setHarvestLevel( tileInfo.harvestTool() , 0 );
        setRegistryName( registryName );
        setResistance( tileInfo.explosionResistance() );
        setSoundType( tileInfo.soundType() );
        setUnlocalizedName( registryName.toString() );

        Float slipperiness = tileInfo.slipperiness();
        if( slipperiness != null )
            setDefaultSlipperiness( slipperiness );
    }

    // Block overrides

    @Override
    @SideOnly( Side.CLIENT )
    public boolean addDestroyEffects( World world , BlockPos pos , ParticleManager manager )
    {
        Supplier< TextureAtlasSprite > textureGetter = () -> tileInfo.modelTextureMap().getTexture( EnumFacing.VALUES[ RANDOM.nextInt( 6 ) ] );
        ParticleHelper.addDestroyEffects( world , pos , manager , textureGetter );

        return true;
    }

    @Override
    @SideOnly( Side.CLIENT )
    public boolean addHitEffects( IBlockState state , World worldObj , RayTraceResult target , ParticleManager manager )
    {
        TextureAtlasSprite texture = tileInfo.modelTextureMap().getTexture( target.sideHit );
        ParticleHelper.createHitParticle( state , worldObj , target , manager , RANDOM , texture );

        return true;
    }

    @Override
    @SideOnly( Side.CLIENT )
    public void addInformation( ItemStack stack , @Nullable World player , List< String > tooltip , ITooltipFlag advanced )
    {
        super.addInformation( stack , player , tooltip , advanced );

        List< String > tooltipLines = tileInfo.localizedTooltip();
        if( tooltipLines != null )
            tooltip.addAll( tooltipLines );
    }

    @Deprecated
    @Override
    public int getLightValue( IBlockState state )
    {
        return tileInfo.lightLevel();
    }

    @Override
    public int getLightValue( IBlockState state , IBlockAccess world , BlockPos pos )
    {
        return tileInfo.lightLevel();
    }
}
