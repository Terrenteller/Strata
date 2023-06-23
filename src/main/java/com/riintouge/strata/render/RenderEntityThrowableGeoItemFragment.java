package com.riintouge.strata.render;

import com.riintouge.strata.entity.EntityThrowableGeoItemFragment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderSnowball;
import net.minecraft.item.ItemStack;

public class RenderEntityThrowableGeoItemFragment extends RenderSnowball< EntityThrowableGeoItemFragment >
{
    public RenderEntityThrowableGeoItemFragment( RenderManager renderManager )
    {
        super( renderManager , null , Minecraft.getMinecraft().getRenderItem() );
    }

    // RenderSnowball overrides

    @Override
    public ItemStack getStackToRender( EntityThrowableGeoItemFragment entityIn )
    {
        return entityIn.getItemStack();
    }
}
