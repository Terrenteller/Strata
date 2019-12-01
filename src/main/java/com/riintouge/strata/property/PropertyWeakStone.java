package com.riintouge.strata.property;

import com.riintouge.strata.Strata;
import com.riintouge.strata.block.DynamicOreHostManager;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.util.ResourceLocation;

public class PropertyWeakStone implements IPropertyEnumProvider
{
    public static PropertyWeakStone INSTANCE = new PropertyWeakStone();
    public static PropertyEnum PROPERTY = PropertyEnum.create( "type" , Type.class );
    public static Type DEFAULT = Type.BRECCIA;

    static
    {
        for( Type type : types() )
        {
            DynamicOreHostManager.INSTANCE.registerHost(
                type.toString(),
                new ResourceLocation( Strata.modid , "blocks/stone/weak/" + type.toString() ) );
        }
    }

    public enum Type implements IPropertyEnumValue
    {
        BRECCIA( 0 , "breccia" ),
        CLAYSTONE( 1 , "claystone" ),
        CARBONATITE( 2 , "carbonatite" ),
        CONGLOMERATE( 3 , "conglomerate" ),
        MUDSTONE( 4 , "mudstone" );

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
