package com.riintouge.strata.property;

import com.riintouge.strata.Strata;
import com.riintouge.strata.block.DynamicOreHostManager;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.util.ResourceLocation;

public class PropertyVeryStrongStone implements IPropertyEnumProvider
{
    public static PropertyVeryStrongStone INSTANCE = new PropertyVeryStrongStone();
    public static PropertyEnum PROPERTY = PropertyEnum.create( "type" , Type.class );
    public static Type DEFAULT = Type.DIORITE;

    static
    {
        for( PropertyVeryStrongStone.Type type : types() )
        {
            DynamicOreHostManager.INSTANCE.registerHost(
                type.toString(),
                new ResourceLocation( Strata.modid , "blocks/stone/very_strong/" + type.toString() ) );
        }
    }

    public enum Type implements IPropertyEnumValue
    {
        DIORITE( 0 , "diorite" ),
        GABBRO( 1 , "gabbro" ),
        HORNFELS( 2 , "hornfels" ),
        PERIDOTITE( 3 , "peridotite" ),
        QUARTZITE( 4 , "quartzite" );

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
