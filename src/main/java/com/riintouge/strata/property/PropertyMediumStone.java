package com.riintouge.strata.property;

import com.riintouge.strata.Strata;
import com.riintouge.strata.block.DynamicOreHostManager;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.util.ResourceLocation;

public class PropertyMediumStone implements IPropertyEnumProvider
{
    public static PropertyMediumStone INSTANCE = new PropertyMediumStone();
    public static PropertyEnum PROPERTY = PropertyEnum.create( "type" , Type.class );
    public static Type DEFAULT = Type.LIMESTONE;

    static
    {
        for( PropertyMediumStone.Type type : types() )
        {
            DynamicOreHostManager.INSTANCE.registerHost(
                type.toString(),
                new ResourceLocation( Strata.modid , "blocks/stone/medium/" + type.toString() ) );
        }
    }

    public enum Type implements IPropertyEnumValue
    {
        LIMESTONE( 0 , "limestone" ),
        SCHIST( 1 , "schist" ),
        SERPENTINITE( 2 , "serpentinite" ),
        SLATE( 3 , "slate" ),
        SKARN( 4 , "skarn" ),
        CHALK( 5 , "chalk" );


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
