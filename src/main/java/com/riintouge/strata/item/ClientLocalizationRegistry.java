package com.riintouge.strata.item;

import com.riintouge.strata.Strata;
import com.riintouge.strata.util.ReflectionUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.*;
import net.minecraft.util.text.translation.LanguageMap;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

@SideOnly( Side.CLIENT )
public final class ClientLocalizationRegistry extends LocalizationRegistry implements IResourceManagerReloadListener
{
    private Language currentLanguage;
    // Forge has TWO maps for resolving localized strings. They appear to be identical.
    private Map< String , String > localeProperties1 = null; // resources/I18n
    private Map< String , String > localeProperties2 = null; // translation/I18n
    private Map< String , Map< String , String > > languageMaps = new HashMap<>();
    private Map< String , String > localizedStrings = new HashMap<>();

    public ClientLocalizationRegistry()
    {
        reacquireLocaleLocalizationMaps();
        ( (IReloadableResourceManager)Minecraft.getMinecraft().getResourceManager() ).registerReloadListener( this );
    }

    public void register( String unlocalizedKey , Map< String , String > languageMap )
    {
        // This can't go in the constructor. When SidedProxy instantiates this class the current language is null.
        if( currentLanguage == null )
            currentLanguage = Minecraft.getMinecraft().getLanguageManager().getCurrentLanguage();

        languageMaps.put( unlocalizedKey , languageMap );
        String localizedValue = getInternal( unlocalizedKey );
        localizedStrings.put( unlocalizedKey , localizedValue );

        if( localeProperties1 != null )
            localeProperties1.putIfAbsent( unlocalizedKey , localizedValue );

        if( localeProperties2 != null )
            localeProperties2.putIfAbsent( unlocalizedKey , localizedValue );
    }

    @Nullable
    public String get( String unlocalizedKey )
    {
        return localizedStrings.getOrDefault( unlocalizedKey , null );
    }

    @Nullable
    private String getInternal( String unlocalizedKey )
    {
        Map< String , String > languageMap = languageMaps.get( unlocalizedKey );
        String localizedName = languageMap.getOrDefault( currentLanguage.getLanguageCode() , null );
        if( localizedName != null )
            return localizedName;

        localizedName = languageMap.getOrDefault( currentLanguage.getJavaLocale().getLanguage() , null );
        if( localizedName != null )
            return localizedName;

        localizedName = languageMap.getOrDefault( "en" , null );
        if( localizedName != null )
            return localizedName;

        return null;
    }

    @SuppressWarnings( "unchecked" )
    private void reacquireLocaleLocalizationMaps()
    {
        // Not all code paths traverse Block.getLocalizedName() or Item.getLocalizedName(), such as tooltips
        // on the Statistics page. Don't rely on this, however. Use get() where possible because it's more correct.

        // The Statistics page uses the following localization map
        try
        {
            Field localeField = ReflectionUtil.findFieldByType( LanguageManager.class , Locale.class , false );
            localeField.setAccessible( true );
            Locale locale = (Locale)localeField.get( Minecraft.getMinecraft().getLanguageManager() );

            Field propertyField = ReflectionUtil.findFieldByType( Locale.class , Map.class , true );
            propertyField.setAccessible( true );
            localeProperties1 = (Map< String , String >)propertyField.get( locale );
        }
        catch( Exception e )
        {
            Strata.LOGGER.warn( "Failed to acquire the resources/I18n localization dictionary for direct injection! Some strings may appear unlocalized." );
        }

        // Item tooltips use the following localization map
        try
        {
            Field instanceField = ReflectionUtil.findFieldByType( LanguageMap.class , LanguageMap.class , false );
            instanceField.setAccessible( true );
            LanguageMap languageMap = (LanguageMap)instanceField.get( null );

            Field languageListField = ReflectionUtil.findFieldByType( LanguageMap.class , Map.class , true );
            languageListField.setAccessible( true );
            localeProperties2 = (Map< String , String >)languageListField.get( languageMap );
        }
        catch( Exception e )
        {
            Strata.LOGGER.warn( "Failed to acquire the translation/I18n localization dictionary for direct injection! Some strings may appear unlocalized." );
        }
    }

    // IResourceManagerReloadListener overrides

    @Override
    public void onResourceManagerReload( IResourceManager resourceManager )
    {
        localeProperties1 = null;
        localeProperties2 = null;
        currentLanguage = Minecraft.getMinecraft().getLanguageManager().getCurrentLanguage();
        reacquireLocaleLocalizationMaps();

        languageMaps.keySet()
            .parallelStream()
            .forEach( x -> localizedStrings.put( x , getInternal( x ) ) );

        if( localeProperties1 != null )
        {
            languageMaps.keySet()
                .parallelStream()
                .forEach( x -> localeProperties1.put( x , localizedStrings.get( x ) ) );
        }

        if( localeProperties2 != null )
        {
            languageMaps.keySet()
                .parallelStream()
                .forEach( x -> localeProperties2.put( x , localizedStrings.get( x ) ) );
        }
    }
}
