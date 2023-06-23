package com.riintouge.strata.item.geo;

import com.riintouge.strata.Strata;
import com.riintouge.strata.block.geo.IGeoTileInfo;
import com.riintouge.strata.entity.EntityThrowableGeoItemFragment;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.util.*;
import net.minecraft.world.World;

public class ThrowableGeoItemFragment extends GeoItemFragment
{
    public static final SoundEvent THROW_SOUND = new SoundEvent( Strata.resource( "item.fragment.throw" ) );

    public ThrowableGeoItemFragment( IGeoTileInfo tileInfo )
    {
        super( tileInfo );
    }

    public IGeoTileInfo getTileInfo()
    {
        return tileInfo;
    }

    // Item overrides

    @Override
    public EnumAction getItemUseAction( ItemStack stack )
    {
        return EnumAction.BOW;
    }

    @Override
    public int getMaxItemUseDuration( ItemStack stack )
    {
        return 72000;
    }

    @Override
    public ActionResult< ItemStack > onItemRightClick( World worldIn , EntityPlayer playerIn , EnumHand handIn )
    {
        playerIn.setActiveHand( handIn );

        return new ActionResult<>( EnumActionResult.SUCCESS , playerIn.getHeldItem( handIn ) );
    }

    @Override
    public void onPlayerStoppedUsing( ItemStack stack , World worldIn , EntityLivingBase entityLiving , int timeLeft )
    {
        int useTime = getMaxItemUseDuration( stack ) - timeLeft;
        if( useTime < 20 || worldIn.isRemote || !( entityLiving instanceof EntityPlayer ) )
            return;

        EntityPlayer player = (EntityPlayer)entityLiving;
        EntityThrowableGeoItemFragment entity = new EntityThrowableGeoItemFragment( worldIn , player );
        entity.setItemStack( stack );
        entity.shoot( player , player.rotationPitch , player.rotationYaw , 0.0f , 1.0f , 5.0f );
        worldIn.spawnEntity( entity );
        worldIn.playSound(
            null,
            player.posX,
            player.posY,
            player.posZ,
            THROW_SOUND,
            SoundCategory.NEUTRAL,
            0.5f,
            0.4f / ( ( itemRand.nextFloat() * 0.4f ) + 0.8f ) );

        player.addStat( StatList.getObjectUseStats( this ) );
        if( !player.isCreative() )
            stack.shrink( 1 );
    }
}
