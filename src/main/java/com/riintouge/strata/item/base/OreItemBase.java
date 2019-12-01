package com.riintouge.strata.item.base;

import com.riintouge.strata.Strata;
import com.riintouge.strata.property.IPropertyEnumValue;
import com.riintouge.strata.item.IItemMeshDefinitionProvider;
import com.riintouge.strata.item.OreItemModelLoader;
import com.riintouge.strata.property.IMetaPropertyProvider;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public abstract class OreItemBase extends Item implements IMetaPropertyProvider , IItemMeshDefinitionProvider
{
    public OreItemBase()
    {
        addPropertyOverride( new ResourceLocation( "meta" ) , META_GETTER );
        setCreativeTab( Strata.ITEM_TAB );
    }

    public ItemStack getSubItem( int metadata )
    {
        return new ItemStack( this , 1 , metadata );
    }

    @SubscribeEvent
    public abstract void registerOres( RegistryEvent.Register< Item > event );

    // Item overrides

    @Override
    public int getMetadata( int damage )
    {
        return damage;
    }

    // IItemMeshDefinitionProvider overrides

    @Override
    public ItemMeshDefinition getItemMeshDefinition()
    {
        return new ItemMeshDefinition()
        {
            public ModelResourceLocation getModelLocation( ItemStack stack )
            {
                IPropertyEnumValue orePropertyValue = getMetaPropertyProvider().propertyValues()[ stack.getItemDamage() ];
                return OreItemModelLoader.getModelResourceLocation( orePropertyValue.getName() );
            }
        };
    }

    // Statics

    private static final IItemPropertyGetter META_GETTER = new IItemPropertyGetter()
    {
        @Override
        public float apply( ItemStack stack , World worldIn , EntityLivingBase entityIn )
        {
            return stack.getMetadata();
        }
    };
}
