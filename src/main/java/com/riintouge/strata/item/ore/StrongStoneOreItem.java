package com.riintouge.strata.item.ore;

import com.riintouge.strata.Strata;
import com.riintouge.strata.property.PropertyStrongStoneOre;
import com.riintouge.strata.item.base.OreItemBase;
import com.riintouge.strata.property.IPropertyEnumProvider;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.oredict.OreDictionary;

public class StrongStoneOreItem extends OreItemBase
{
    public static final StrongStoneOreItem INSTANCE = new StrongStoneOreItem();
    public static final String RegistryName = "strata:strong_stone_ore_item";
    public static final String UnlocalizedName = "strata:strong_stone_ore_item";

    public StrongStoneOreItem()
    {
        setHasSubtypes( true );
        setRegistryName( RegistryName );
        setUnlocalizedName( UnlocalizedName );
    }

    // OreItemBase overrides

    @Override
    public void registerOres( RegistryEvent.Register< Item > event )
    {
        OreDictionary.registerOre( "oreIron" , INSTANCE.getSubItem( PropertyStrongStoneOre.Type.BANDED_IRON.getValue() ) );
        // quartz
        OreDictionary.registerOre( "oreTin" , INSTANCE.getSubItem( PropertyStrongStoneOre.Type.CASSITERITE.getValue() ) );
        OreDictionary.registerOre( "oreChromium" , INSTANCE.getSubItem( PropertyStrongStoneOre.Type.CHROMITE.getValue() ) );
        OreDictionary.registerOre( "oreTitanium" , INSTANCE.getSubItem( PropertyStrongStoneOre.Type.ILMENITE.getValue() ) );
        OreDictionary.registerOre( "oreIron" , INSTANCE.getSubItem( PropertyStrongStoneOre.Type.MAGNETITE.getValue() ) );
        OreDictionary.registerOre( "oreCaesium" , INSTANCE.getSubItem( PropertyStrongStoneOre.Type.POLLUCITE.getValue() ) );
        OreDictionary.registerOre( "oreLithium" , INSTANCE.getSubItem( PropertyStrongStoneOre.Type.SPODUMENE.getValue() ) );
        OreDictionary.registerOre( "oreTantalum" , INSTANCE.getSubItem( PropertyStrongStoneOre.Type.TANTALITE.getValue() ) );
        // TODO: see if other mods use this as an ACTUAL ore or uranium itself
        OreDictionary.registerOre( "oreUranium" , INSTANCE.getSubItem( PropertyStrongStoneOre.Type.PITCHBLENDE.getValue() ) );
        OreDictionary.registerOre( "oreVanadium" , INSTANCE.getSubItem( PropertyStrongStoneOre.Type.VANADIUM_MAGNETITE.getValue() ) );
        // apatite
        // kyanite
        // perlite
        // pumice
        // pyrite
    }

    // Item overrides

    @Override
    public void getSubItems( CreativeTabs tabs , NonNullList< ItemStack > items )
    {
        for( int index = 0 ; index < PropertyStrongStoneOre.types().length ; index++ )
            items.add( new ItemStack( this , 1 , index ) );
    }

    @Override
    public String getUnlocalizedName( ItemStack stack )
    {
        String oreName = PropertyStrongStoneOre.types()[ stack.getMetadata() ].toString();
        return String.format( "%s:%s" , Strata.modid , oreName );
    }

    // IMetaPropertyProvider overrides

    @Override
    public IPropertyEnumProvider getMetaPropertyProvider()
    {
        return PropertyStrongStoneOre.INSTANCE;
    }
}
