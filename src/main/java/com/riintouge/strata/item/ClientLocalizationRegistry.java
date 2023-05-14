package com.riintouge.strata.item;

import com.riintouge.strata.Strata;
import com.riintouge.strata.util.DebugUtil;
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
    // There are TWO maps for resolving localized strings. They appear to be identical.
    private Map< String , String > localeProperties1 = null; // LanguageManager's Locale
    private Map< String , String > localeProperties2 = null; // LanguageMap's Map
    private Map< String , Map< String , String > > languageMaps = new HashMap<>();
    private Map< String , String > localizedStrings = new HashMap<>();

    public ClientLocalizationRegistry()
    {
        acquireLocalizationMaps();
        ( (IReloadableResourceManager)Minecraft.getMinecraft().getResourceManager() ).registerReloadListener( this );
    }

    @SuppressWarnings( "unchecked" )
    private void acquireLocalizationMaps()
    {
        // Not all code paths traverse Block.getLocalizedName() or Item.getLocalizedName(), such as tooltips
        // on the Statistics page. Don't rely on this, however. Use get() where possible because it's more correct.

        // The Statistics page uses this
        try
        {
            Field localeField = ReflectionUtil.findFieldByType( LanguageManager.class , Locale.class , false );
            localeField.setAccessible( true );
            Locale locale = (Locale)localeField.get( Minecraft.getMinecraft().getLanguageManager() );

            Field propertiesField = ReflectionUtil.findFieldByType( Locale.class , Map.class , true );
            propertiesField.setAccessible( true );
            localeProperties1 = (Map< String , String >)propertiesField.get( locale );
        }
        catch( Exception e )
        {
            Strata.LOGGER.error( DebugUtil.prettyPrintThrowable( e , "Caught %s while acquiring the LanguageManager localization dictionary! Some strings may appear unlocalized." ) );
        }

        // Item tooltips use this
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
            Strata.LOGGER.error( DebugUtil.prettyPrintThrowable( e , "Caught %s while acquiring the LanguageMap localization dictionary! Some strings may appear unlocalized." ) );
        }
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

    // LocalizationRegistry overrides

    @Override
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
    @Override
    public String get( String unlocalizedKey )
    {
        // TODO: We need to make a distinction between blocks and items and get rid of this method.
        // These classes should only be used to facilitate the addition of strings to the internal
        // localization dictionaries at runtime, not as our version of the registries.
        // Other things to consider:
        // 1. If we keep this getter, it should call an appropriate internal method
        // 2. See about cleaning up the try/catch in ConfigHelper.getLocalizedString()
        // 3. Do these classes belong in the item package?
        return localizedStrings.getOrDefault( unlocalizedKey , null );
    }

    // IResourceManagerReloadListener overrides

    @Override
    public void onResourceManagerReload( IResourceManager resourceManager )
    {
        localeProperties1 = null;
        localeProperties2 = null;
        currentLanguage = Minecraft.getMinecraft().getLanguageManager().getCurrentLanguage();
        acquireLocalizationMaps();

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
