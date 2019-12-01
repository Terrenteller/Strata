package com.riintouge.strata.property;

import com.riintouge.strata.Strata;
import com.riintouge.strata.block.DynamicOreHostManager;
import com.riintouge.strata.item.OreItemTextureManager;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.util.ResourceLocation;

public class PropertyWeakStoneOre implements IPropertyEnumProvider
{
    public static PropertyWeakStoneOre INSTANCE = new PropertyWeakStoneOre();
    public static PropertyEnum PROPERTY = PropertyEnum.create( "type" , Type.class );
    public static Type DEFAULT = Type.BORAX;

    static
    {
        // TODO: move
        for( Type value : PropertyWeakStoneOre.Type.values() )
        {
            DynamicOreHostManager.INSTANCE.registerOre(
                value.toString(),
                new ResourceLocation( Strata.modid , "blocks/ore/weak/" + value.toString() ) );
            OreItemTextureManager.INSTANCE.registerOre(
                value.toString(),
                new ResourceLocation( Strata.modid , "blocks/ore/weak/" + value.toString() ) );
        }
    }

    public enum Type implements IPropertyEnumValue
    {
        BORAX( 0 , "borax" ),
        CINNABAR( 1 , "cinnabar" ),
        GALENA( 2 , "galena" ),
        MOLYBDENITE( 3 , "molybdenite" ),
        PYROLUSITE( 4 , "pyrolusite" ),
        SALT( 5 , "salt" ),
        STIBNITE( 6 , "stibnite" ),
        CHRYSOTILE( 7 , "chrysotile" ),
        DIATOMITE( 8 , "diatomite" ), // salitre
        REALGAR( 9 , "realgar" ),
        GRAPHITE( 10 , "graphite" ),
        GYPSUM( 11 , "gypsum" ),
        MIRABILITE( 12 , "mirabilite" ),
        MICA( 13 , "mica" ),
        SOAPSTONE( 14 , "soapstone" ),
        TRONA( 15 , "trona" );

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
