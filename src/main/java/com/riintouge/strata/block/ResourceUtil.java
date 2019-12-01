package com.riintouge.strata.block;

import com.riintouge.strata.item.base.OreItemBase;
import com.riintouge.strata.property.IMetaPropertyProvider;
import com.riintouge.strata.property.IPropertyEnumValue;
import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.client.model.ModelLoader;

public class ResourceUtil
{
    public static final String ModelResourceBasePath = "models/item/";

    public static < B extends Block & IMetaPropertyProvider , V extends Comparable< V > & IPropertyEnumValue >
    void registerModelVariants( B block , Item item , IProperty< V > variantProperty )
    {
        for( V value : variantProperty.getValueClass().getEnumConstants() )
        {
            String variant = String.format( "%s=%s" , variantProperty.getName() , value.getName() );
            ModelResourceLocation model = new ModelResourceLocation( block.getRegistryName().toString() , variant );
            ModelLoader.setCustomModelResourceLocation( item , value.getValue() , model );
        }
    }

    public static < V extends Comparable< V > & IPropertyEnumValue >
    void registerModelVariants( Item item , IProperty< V > variantProperty )
    {
        NonNullList< ItemStack > subBlocks = NonNullList.create();
        item.getSubItems( CreativeTabs.SEARCH , subBlocks );

        for( V value : variantProperty.getValueClass().getEnumConstants() )
        {
            String variant = String.format( "%s=%s" , variantProperty.getName() , value.getName() );
            ModelResourceLocation model = new ModelResourceLocation( item.getRegistryName().toString() , variant );
            ModelLoader.setCustomModelResourceLocation( item , value.getValue() , model );
        }
    }

    public static void registerOreItem( OreItemBase oreItem )
    {
        ItemMeshDefinition meshDef = oreItem.getItemMeshDefinition();
        for( IPropertyEnumValue value : oreItem.getMetaPropertyProvider().propertyValues() )
        {
            ItemStack itemStack = new ItemStack( oreItem , 1 , value.getValue() );
            ModelBakery.registerItemVariants( oreItem , meshDef.getModelLocation( itemStack ) );
        }
        ModelLoader.setCustomMeshDefinition( oreItem , meshDef );
    }
}
