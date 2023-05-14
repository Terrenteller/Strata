package com.riintouge.strata;

import com.riintouge.strata.misc.ConfigHelper;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.io.File;

@EventBusSubscriber
public final class StrataConfig
{
    public static final StrataConfig INSTANCE = new StrataConfig();

    public static final String CATEGORY_CLIENT = "client";
    public static Boolean useModernWallStyle;
    public static Boolean usePrecomputedOreParticles;
    public static Boolean additionalBlockSounds;
    public static Boolean restrictSampleOffset;
    public static Boolean prioritizeCreativeTabs;

    public static final String CATEGORY_SERVER = "server";
    public static Boolean enforceClientSynchronization;
    public static Boolean dropNonStandardFragments;

    private final Configuration config;

    public StrataConfig()
    {
        config = new Configuration( new File( Loader.instance().getConfigDir() , Strata.MOD_ID + ".cfg" ) );
    }

    public void init()
    {
        sync( true );
    }

    public Configuration getConfig()
    {
        return config;
    }

    private void sync( boolean load )
    {
        if( load && !config.isChild )
            config.load();

        ConfigHelper configHelper = new ConfigHelper( config );

        configHelper.beginCategory( CATEGORY_CLIENT );
        useModernWallStyle = configHelper.getBoolean( "useModernWallStyle" , true );
        usePrecomputedOreParticles = configHelper.getBoolean( "usePrecomputedOreParticles" , true );
        additionalBlockSounds = configHelper.getBoolean( "additionalBlockSounds" , true );
        restrictSampleOffset = configHelper.getBoolean( "restrictSampleOffset" , true );
        prioritizeCreativeTabs = configHelper.getBoolean( "prioritizeCreativeTabs" , true );
        configHelper.endCategory( true );

        configHelper.beginCategory( CATEGORY_SERVER );
        enforceClientSynchronization = configHelper.getBoolean( "enforceClientSynchronization" , true );
        dropNonStandardFragments = configHelper.getBoolean( "dropNonStandardFragments" , false );
        configHelper.endCategory( true );

        if( config.hasChanged() )
            config.save();
    }

    // Statics

    @SubscribeEvent
    public static void onConfigChanged( ConfigChangedEvent.OnConfigChangedEvent event )
    {
        if( event.getModID().equals( Strata.MOD_ID ) )
            INSTANCE.sync( false );
    }
}
