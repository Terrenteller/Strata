package com.riintouge.strata.property;

import com.riintouge.strata.Strata;
import com.riintouge.strata.block.DynamicOreHostManager;
import com.riintouge.strata.item.OreItemTextureManager;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.util.ResourceLocation;

public class PropertyMediumStoneOre implements IPropertyEnumProvider
{
    public static PropertyMediumStoneOre INSTANCE = new PropertyMediumStoneOre();
    public static PropertyEnum PROPERTY = PropertyEnum.create( "type" , Type.class );
    public static Type DEFAULT = Type.BARITE;

    static
    {
        for( Type value : PropertyMediumStoneOre.Type.values() )
        {
            DynamicOreHostManager.INSTANCE.registerOre(
                value.toString(),
                new ResourceLocation( Strata.modid , "blocks/ore/medium/" + value.toString() ) );
            OreItemTextureManager.INSTANCE.registerOre(
                value.toString(),
                new ResourceLocation( Strata.modid , "blocks/ore/medium/" + value.toString() ) );
        }
    }

    public enum Type implements IPropertyEnumValue
    {
        BARITE( 0 , "barite" ),
        BASTNASITE( 1 , "bastnasite" ),
        CHALCOPYRITE( 2 , "chalcopyrite" ),
        GARNIERITE( 3 , "garnierite" ),
        LEPIDOLITE( 4 , "lepidolite" ),
        MAGNESITE( 5 , "magnesite" ),
        PENTLANDITE( 6 , "pentlandite" ),
        SCHEELITE( 7 , "scheelite" ),
        SPHALERITE( 8 , "sphalerite" ),
        WOLFRAMITE( 9 , "wolframite" ),
        ALUNITE( 10 , "alunite" ),
        CELESTINE( 11 , "celestine" ),
        DOLOMITE( 12 , "dolomite" ),
        FLUORITE( 13 , "fluorite" ),
        WOLLASTONITE( 14 , "wollastonite" ),
        ZEOLITE( 15 , "zeolite" );

        private int value;
        private String name;

        Type( int value , String name )
        {
            this.value = value;
            this.name = name;
        }

        // IPropertyEnumValue overrides

        @Override
        public int getValue() { return value; }

        // IStringSerializable overrides

        @Override
        public String getName() { return name; }

        // Object overrides

        @Override
        public String toString() { return getName(); }
    }

    // IPropertyEnumProvider overrides

    @Override
    public PropertyEnum property()
    {
        return PROPERTY;
    }

    @Override
    public IPropertyEnumValue[] propertyValues()
    {
        return Type.values();
    }

    // Statics

    public static Type[] types()
    {
        return Type.values();
    }
}
