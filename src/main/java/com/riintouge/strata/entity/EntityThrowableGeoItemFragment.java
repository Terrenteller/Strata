package com.riintouge.strata.entity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.ParticleBreaking;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class EntityThrowableGeoItemFragment extends EntityThrowable
{
    public static final byte BROKEN_STATE = 0;
    private static final DataParameter< ItemStack > ITEM = EntityDataManager.createKey( EntityThrowableGeoItemFragment.class , DataSerializers.ITEM_STACK );

    public EntityThrowableGeoItemFragment( World world )
    {
        super( world );
    }

    public EntityThrowableGeoItemFragment( World world , EntityLivingBase entity )
    {
        super( world , entity );
    }

    public void setItemStack( ItemStack itemStack )
    {
        EntityDataManager dataManager = getDataManager();
        dataManager.set( ITEM , itemStack );
        dataManager.setDirty( ITEM );
    }

    public ItemStack getItemStack()
    {
        return getDataManager().get( ITEM );
    }

    // EntityThrowable overrides

    @Override
    protected void entityInit()
    {
        getDataManager().register( ITEM , ItemStack.EMPTY );
    }

    @Override
    protected float getGravityVelocity()
    {
        return 1.0f / 20.0f;
    }

    @Override
    protected void onImpact( RayTraceResult result )
    {
        if( world.isRemote )
            return;

        if( result.entityHit != null )
        {
            result.entityHit.attackEntityFrom( DamageSource.causeThrownDamage( this , getThrower() ) , 2.0f );

            if( result.entityHit instanceof EntityEnderman )
                return;
        }

        world.setEntityState( this , BROKEN_STATE );
        setDead();
    }

    @Override
    public void onUpdate()
    {
        super.onUpdate();

        if( ticksExisted > 600 )
            setDead();
    }

    @Override
    public void readEntityFromNBT( NBTTagCompound compound )
    {
        super.readEntityFromNBT( compound );

        ItemStack itemStack = new ItemStack( compound.getCompoundTag( "item" ) );
        if( !itemStack.isEmpty() )
            setItemStack( itemStack );
        else
            setDead();
    }

    @Override
    public void writeEntityToNBT( NBTTagCompound compound )
    {
        super.writeEntityToNBT( compound );

        ItemStack itemStack = getItemStack();
        if( !itemStack.isEmpty() )
            compound.setTag( "item" , itemStack.writeToNBT( new NBTTagCompound() ) );
    }

    // Entity overrides

    @Override
    @SideOnly( Side.CLIENT )
    public void handleStatusUpdate( byte id )
    {
        if( id == BROKEN_STATE )
        {
            ParticleManager particleManager = Minecraft.getMinecraft().effectRenderer;
            Item item = getItemStack().getItem();
            for( int index = 0 ; index < 8 ; index++ )
                particleManager.addEffect( new Particle( world , posX , posY , posZ , item ) );
        }
    }

    // Nested classes

    private class Particle extends ParticleBreaking
    {
        public Particle( World worldIn , double posXIn , double posYIn , double posZIn , Item itemIn )
        {
            // ParticleBreaking's constructor is protected for no good reason
            super( worldIn , posXIn , posYIn , posZIn , itemIn , 0 );
        }
    }
}
