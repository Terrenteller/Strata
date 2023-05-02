package com.riintouge.strata.block.ore;

import com.riintouge.strata.block.SpecialBlockPropertyFlags;
import com.riintouge.strata.block.geo.GeoBlock;
import com.riintouge.strata.util.FlagUtil;
import net.minecraft.block.BlockFalling;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.entity.projectile.EntityWitherSkull;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.lang3.NotImplementedException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public abstract class OreBaseBlock extends BlockFalling
{
    protected IOreInfo oreInfo;

    public OreBaseBlock( IOreInfo oreInfo , Material material )
    {
        super( material );
        this.oreInfo = oreInfo;
    }

    @Nonnull
    public IOreInfo getOreInfo()
    {
        return oreInfo;
    }

    // Block overrides

    @Override
    @SideOnly( Side.CLIENT )
    public void addInformation( ItemStack stack , @Nullable World player , List< String > tooltip , ITooltipFlag advanced )
    {
        ItemStack proxyBlockItemStack = oreInfo.proxyBlockItemStack();
        if( proxyBlockItemStack != null )
        {
            IBlockState proxyBlockState = oreInfo.proxyBlockState();
            assert proxyBlockState != null;
            proxyBlockState.getBlock().addInformation( proxyBlockItemStack , player , tooltip , advanced );
            return;
        }

        super.addInformation( stack , player , tooltip , advanced );

        List< String > tooltipLines = oreInfo.localizedTooltip();
        if( tooltipLines != null )
            tooltip.addAll( tooltipLines );
    }

    @Override
    public boolean canEntityDestroy( IBlockState state , IBlockAccess world , BlockPos pos , Entity entity )
    {
        IBlockState proxyBlockState = oreInfo.proxyBlockState();
        if( proxyBlockState != null && !proxyBlockState.getBlock().canEntityDestroy( proxyBlockState , world , pos , entity ) )
            return false;

        if( FlagUtil.check( oreInfo.specialBlockPropertyFlags() , SpecialBlockPropertyFlags.DRAGON_IMMUNE )
            && entity instanceof EntityDragon )
        {
            return false;
        }
        else if( FlagUtil.check( oreInfo.specialBlockPropertyFlags() , SpecialBlockPropertyFlags.WITHER_IMMUNE )
            && ( entity instanceof EntityWither || entity instanceof EntityWitherSkull ) )
        {
            return false;
        }

        return super.canEntityDestroy( state , world , pos , entity );
    }

    @Deprecated
    @Override
    public int getLightValue( IBlockState state )
    {
        IBlockState proxyBlockState = oreInfo.proxyBlockState();
        if( proxyBlockState != null )
            return proxyBlockState.getBlock().getLightValue( proxyBlockState );

        throw new NotImplementedException( "Use the positional overload instead!" );
    }

    @Override
    public int getLightValue( IBlockState state , IBlockAccess world , BlockPos pos )
    {
        int oreLightValue = oreInfo.lightLevel();
        IBlockState proxyBlockState = oreInfo.proxyBlockState();
        if( proxyBlockState != null )
            oreLightValue = proxyBlockState.getBlock().getLightValue( proxyBlockState , world , pos );

        return oreLightValue;
    }

    @Override
    public String getLocalizedName()
    {
        IBlockState proxyBlockState = oreInfo.proxyBlockState();
        if( proxyBlockState != null )
            return proxyBlockState.getBlock().getLocalizedName();

        String localizedName = oreInfo.localizedName();
        return localizedName != null ? localizedName : getRegistryName().toString();
    }

    @Override
    public String getUnlocalizedName()
    {
        IBlockState proxyBlockState = oreInfo.proxyBlockState();
        if( proxyBlockState != null )
            return proxyBlockState.getBlock().getUnlocalizedName();

        // Strata localization doesn't make a distinction between blocks and items
        return super.getUnlocalizedName().replace( "tile." , "" );
    }

    @Override
    public void onFallenUpon( World worldIn , BlockPos pos , Entity entityIn , float fallDistance )
    {
        GeoBlock.onFallenUponCommon( this , worldIn , pos , entityIn , fallDistance );

        super.onFallenUpon( worldIn , pos , entityIn , fallDistance );
    }
}
