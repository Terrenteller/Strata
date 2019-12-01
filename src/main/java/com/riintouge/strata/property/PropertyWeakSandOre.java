package com.riintouge.strata.property;

import net.minecraft.block.properties.PropertyEnum;

public class PropertyWeakSandOre implements IPropertyEnumProvider
{
    public static PropertyWeakSandOre INSTANCE = new PropertyWeakSandOre();
    public static PropertyEnum PROPERTY = PropertyEnum.create( "type" , Type.class );
    public static Type DEFAULT = Type.BASALTIC_MINERAL_SAND;

    public enum Type implements IPropertyEnumValue
    {
        BASALTIC_MINERAL_SAND( 0 , "basaltic_mineral_sand" ),
        CASSITERITE_SAND( 1 , "cassiterite_sand" ),
        GARNET_SAND( 2 , "garnet_sand" ),
        GRANITIC_MINERAL_SAND( 3 , "granitic_mineral_sand" ),
        QUARTZ_SAND( 4 , "quartz_sand" ),
        VOLCANIC_ASH( 5 , "volcanic_ash" ),
        GLAUCONITE( 6 , "glauconite" );

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
