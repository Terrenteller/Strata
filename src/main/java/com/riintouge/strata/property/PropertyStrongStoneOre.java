package com.riintouge.strata.property;

import com.riintouge.strata.Strata;
import com.riintouge.strata.block.DynamicOreHostManager;
import com.riintouge.strata.item.OreItemTextureManager;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.util.ResourceLocation;

public class PropertyStrongStoneOre implements IPropertyEnumProvider
{
    public static PropertyStrongStoneOre INSTANCE = new PropertyStrongStoneOre();
    public static PropertyEnum PROPERTY = PropertyEnum.create( "type" , Type.class );
    public static Type DEFAULT = Type.BANDED_IRON;

    static
    {
        for( Type value : PropertyStrongStoneOre.Type.values() )
        {
            DynamicOreHostManager.INSTANCE.registerOre(
                value.toString(),
                new ResourceLocation( Strata.modid , "blocks/ore/strong/" + value.toString() ) );
            OreItemTextureManager.INSTANCE.registerOre(
                value.toString(),
                new ResourceLocation( Strata.modid , "blocks/ore/strong/" + value.toString() ) );
        }
    }

    public enum Type implements IPropertyEnumValue
    {
        BANDED_IRON( 0 , "banded_iron" ),
        QUARTZ( 1 , "quartz" ),
        CASSITERITE( 2 , "cassiterite" ),
        CHROMITE( 3 , "chromite" ),
        ILMENITE( 4 , "ilmenite" ),
        MAGNETITE( 5 , "magnetite" ),
        POLLUCITE( 6 , "pollucite" ),
        SPODUMENE( 7 , "spodumene" ),
        TANTALITE( 8 , "tantalite" ),
        PITCHBLENDE( 9 , "pitchblende" ),
        VANADIUM_MAGNETITE( 10 , "vanadium_magnetite" ),
        APATITE( 11 , "apatite" ),
        KYANITE( 12 , "kyanite" ),
        PERLITE( 13 , "perlite" ),
        PUMICE( 14 , "pumice" ),
        PYRITE( 15 , "pyrite" );

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
