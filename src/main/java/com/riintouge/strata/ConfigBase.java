package com.riintouge.strata;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

import java.util.ArrayList;
import java.util.List;

public abstract class ConfigBase
{
    private Configuration config = null;
    private String categoryName = null;
    private List< String > propertyNames = null;
    private boolean useDeprecatedTranslator = false;

    protected void pushCategory( Configuration config , String categoryName )
    {
        this.config = config;
        this.categoryName = categoryName;
        propertyNames = new ArrayList<>();
    }

    protected void popCategory()
    {
        config.setCategoryPropertyOrder( categoryName , propertyNames );

        config = null;
        categoryName = null;
        propertyNames = null;
    }

    protected String getLocalizedValue( String key )
    {
        if( useDeprecatedTranslator )
            return net.minecraft.util.text.translation.I18n.translateToLocal( key );

        try
        {
            return net.minecraft.client.resources.I18n.format( key );
        }
        catch( NoClassDefFoundError e )
        {
            // net.minecraft.client.resources.I18n doesn't exist server-side
            useDeprecatedTranslator = true;
            return getLocalizedValue( key );
        }
    }

    protected boolean getBoolean( String variableName , boolean defaultValue )
    {
        String languageKey = "strata.config." + variableName;
        Property prop = config.get( categoryName , variableName , defaultValue , getLocalizedValue( languageKey + "Desc" ) );
        prop.setLanguageKey( languageKey );
        propertyNames.add( prop.getName() );
        return prop.getBoolean( defaultValue );
    }
}
