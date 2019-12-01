package com.riintouge.strata.item.ore;

import com.riintouge.strata.Strata;
import com.riintouge.strata.property.PropertyWeakStoneOre;
import com.riintouge.strata.item.base.OreItemBase;
import com.riintouge.strata.property.IPropertyEnumProvider;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.oredict.OreDictionary;

public class WeakStoneOreItem extends OreItemBase
{
    public static final WeakStoneOreItem INSTANCE = new WeakStoneOreItem();
    public static final String RegistryName = "strata:weak_stone_ore_item";
    public static final String UnlocalizedName = "strata:weak_stone_ore_item";

    public WeakStoneOreItem()
    {
        setHasSubtypes( true );
        setRegistryName( RegistryName );
        setUnlocalizedName( UnlocalizedName );
    }

    // OreItemBase overrides

    @Override
    public void registerOres( RegistryEvent.Register< Item > event )
    {
        // borax
        // This really should be oreMercury but the OLD-ONES-THAT-CAME-PREVIOUSLY used oreCinnabar
        OreDictionary.registerOre( "oreCinnabar" , INSTANCE.getSubItem( PropertyWeakStoneOre.Type.CINNABAR.getValue() ) );
        OreDictionary.registerOre( "oreLead" , INSTANCE.getSubItem( PropertyWeakStoneOre.Type.GALENA.getValue() ) );
        OreDictionary.registerOre( "oreMolybdenum" , INSTANCE.getSubItem( PropertyWeakStoneOre.Type.MOLYBDENITE.getValue() ) );
        OreDictionary.registerOre( "oreManganese" , INSTANCE.getSubItem( PropertyWeakStoneOre.Type.PYROLUSITE.getValue() ) );
        // salt
        OreDictionary.registerOre( "oreAntimony" , INSTANCE.getSubItem( PropertyWeakStoneOre.Type.STIBNITE.getValue() ) );
        // chrysotile
        // diatomite
        OreDictionary.registerOre( "oreArsenic" , INSTANCE.getSubItem( PropertyWeakStoneOre.Type.REALGAR.getValue() ) );
        // graphite
        // gypsum
        // mirabilite
        // mica
        // soapstone
        // trona
    }

    // Item overrides

    // TODO: addInformation?

    @Override
    public void getSubItems( CreativeTabs tabs , NonNullList< ItemStack > items )
    {
        for( int index = 0 ; index < PropertyWeakStoneOre.types().length ; index++ )
            items.add( new ItemStack( this , 1 , index ) );
    }

    @Override
    public String getUnlocalizedName( ItemStack stack )
    {
        String oreName = PropertyWeakStoneOre.types()[ stack.getMetadata() ].toString();
        return String.format( "%s:%s" , Strata.modid , oreName );
    }

    // IMetaPropertyProvider overrides

    @Override
    public IPropertyEnumProvider getMetaPropertyProvider()
    {
        return PropertyWeakStoneOre.INSTANCE;
    }
}
