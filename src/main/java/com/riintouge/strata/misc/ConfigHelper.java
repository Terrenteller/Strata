package com.riintouge.strata.misc;

import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;

public class ConfigHelper
{
    protected Configuration config;
    protected String categoryName;
    protected List< String > propertyNames;
    protected boolean useDeprecatedTranslator;

    public ConfigHelper( Configuration config )
    {
        this.config = config;
    }

    public void beginCategory( String categoryName )
    {
        this.categoryName = categoryName;
        this.propertyNames = new ArrayList<>();
    }

    public void endCategory( boolean removeUnusedProperties )
    {
        if( removeUnusedProperties )
        {
            ConfigCategory category = config.getCategory( categoryName );
            Set< String > unusedPropertyNames = new HashSet<>( category.getValues().keySet() );
            unusedPropertyNames.removeAll( propertyNames );

            if( unusedPropertyNames.size() > 0 )
                for( String unusedProperty : unusedPropertyNames )
                    category.remove( unusedProperty );
        }

        config.setCategoryPropertyOrder( categoryName , propertyNames );
        categoryName = null;
        propertyNames = null;
    }

    protected String getLocalizedString( String localizationKey )
    {
        if( useDeprecatedTranslator )
            return net.minecraft.util.text.translation.I18n.translateToLocal( localizationKey );

        try
        {
            return net.minecraft.client.resources.I18n.format( localizationKey );
        }
        catch( NoClassDefFoundError e )
        {
            // net.minecraft.client.resources.I18n doesn't exist server-side
            useDeprecatedTranslator = true;
            return getLocalizedString( localizationKey );
        }
    }

    protected Pair< String , String > getNameAndDescriptionLocalizationKeys( String variableName )
    {
        String name = "strata.config." + variableName;
        return new ImmutablePair<>( name , name + "Desc" );
    }

    public boolean getBoolean( String variableName , boolean defaultValue )
    {
        Pair< String , String > nameAndDesc = getNameAndDescriptionLocalizationKeys( variableName );
        Property prop = config.get( categoryName , variableName , defaultValue , getLocalizedString( nameAndDesc.getRight() ) );
        prop.setLanguageKey( nameAndDesc.getLeft() );
        propertyNames.add( prop.getName() );

        return prop.getBoolean( defaultValue );
    }
}
