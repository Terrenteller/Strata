package com.riintouge.strata.property;

import com.riintouge.strata.Strata;
import com.riintouge.strata.block.DynamicOreHostManager;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.util.ResourceLocation;

public class PropertyStrongStone implements IPropertyEnumProvider
{
    public static PropertyStrongStone INSTANCE = new PropertyStrongStone();
    public static PropertyEnum PROPERTY = PropertyEnum.create( "type" , Type.class );
    public static Type DEFAULT = Type.ANDESITE;

    static
    {
        for( PropertyStrongStone.Type type : types() )
        {
            DynamicOreHostManager.INSTANCE.registerHost(
                type.toString(),
                new ResourceLocation( Strata.modid , "blocks/stone/strong/" + type.toString() ) );
        }
    }

    public enum Type implements IPropertyEnumValue
    {
        ANDESITE( 0 , "andesite" ),
        BASALT( 1 , "basalt" ),
        GNEISS( 2 , "gneiss" ),
        GRANITE( 3 , "granite" ),
        GREENSCHIST( 4 , "greenschist" ),
        MARBLE( 5 , "marble" ),
        PEGMATITE( 6 , "pegmatite" ),
        RHYOLITE( 7 , "rhyolite" ),
        SANDSTONE( 8 , "sandstone" ),
        RED_SANDSTONE( 9 , "red_sandstone" );

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
