package com.riintouge.strata.item.ore;

import com.riintouge.strata.Strata;
import com.riintouge.strata.property.PropertyMediumStoneOre;
import com.riintouge.strata.item.base.OreItemBase;
import com.riintouge.strata.property.IPropertyEnumProvider;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.oredict.OreDictionary;

public class MediumStoneOreItem extends OreItemBase
{
    public static final MediumStoneOreItem INSTANCE = new MediumStoneOreItem();
    public static final String RegistryName = "strata:medium_stone_ore_item";
    public static final String UnlocalizedName = "strata:medium_stone_ore_item";

    public MediumStoneOreItem()
    {
        setHasSubtypes( true );
        setRegistryName( RegistryName );
        setUnlocalizedName( UnlocalizedName );
    }

    // OreItemBase overrides

    @Override
    public void registerOres( RegistryEvent.Register< Item > event )
    {
        OreDictionary.registerOre( "oreBarium" , INSTANCE.getSubItem( PropertyMediumStoneOre.Type.BARITE.getValue() ) );
        // bastnasite
        OreDictionary.registerOre( "oreCopper" , INSTANCE.getSubItem( PropertyMediumStoneOre.Type.CHALCOPYRITE.getValue() ) );
        OreDictionary.registerOre( "oreNickel" , INSTANCE.getSubItem( PropertyMediumStoneOre.Type.GARNIERITE.getValue() ) );
        OreDictionary.registerOre( "oreLithium" , INSTANCE.getSubItem( PropertyMediumStoneOre.Type.LEPIDOLITE.getValue() ) );
        // magnesite
        // pentlandite
        OreDictionary.registerOre( "oreTungsten" , INSTANCE.getSubItem( PropertyMediumStoneOre.Type.SCHEELITE.getValue() ) );
        OreDictionary.registerOre( "oreZinc" , INSTANCE.getSubItem( PropertyMediumStoneOre.Type.SPHALERITE.getValue() ) );
        OreDictionary.registerOre( "oreTungsten" , INSTANCE.getSubItem( PropertyMediumStoneOre.Type.WOLFRAMITE.getValue() ) );
        // oreBauxite because "aluminium" vs. "aluminum"
        OreDictionary.registerOre( "oreBauxite" , INSTANCE.getSubItem( PropertyMediumStoneOre.Type.ALUNITE.getValue() ) );
        OreDictionary.registerOre( "oreStrontium" , INSTANCE.getSubItem( PropertyMediumStoneOre.Type.CELESTINE.getValue() ) );
        OreDictionary.registerOre( "oreMagnesium" , INSTANCE.getSubItem( PropertyMediumStoneOre.Type.DOLOMITE.getValue() ) );
        // fluorite
        // wollastonite
        // zeolite
    }

    // Item overrides

    @Override
    public void getSubItems( CreativeTabs tabs , NonNullList< ItemStack > items )
    {
        for( int index = 0 ; index < PropertyMediumStoneOre.types().length ; index++ )
            items.add( new ItemStack( this , 1 , index ) );
    }

    @Override
    public String getUnlocalizedName( ItemStack stack )
    {
        String oreName = PropertyMediumStoneOre.types()[ stack.getMetadata() ].toString();
        return String.format( "%s:%s" , Strata.modid , oreName );
    }

    // IMetaPropertyProvider overrides

    @Override
    public IPropertyEnumProvider getMetaPropertyProvider()
    {
        return PropertyMediumStoneOre.INSTANCE;
    }
}
