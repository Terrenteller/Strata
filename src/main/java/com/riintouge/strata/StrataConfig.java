package com.riintouge.strata;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.io.File;

@EventBusSubscriber
public final class StrataConfig extends ConfigBase
{
    public static final StrataConfig INSTANCE = new StrataConfig();

    // Client Settings
    public static final String CATEGORY_CLIENT = "client";
    public static boolean useModernWallStyle = true;

    private final Configuration config;

    public StrataConfig()
    {
        config = new Configuration( new File( Loader.instance().getConfigDir() , Strata.modid + ".cfg" ) );

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

        pushCategory( config , CATEGORY_CLIENT );
        useModernWallStyle = getBoolean( "useModernWallStyle" , true );
        popCategory();

        // TODO: remove any property we don't recognize?

        if( config.hasChanged() )
            config.save();
    }

    // Statics

    @SubscribeEvent
    public static void onConfigChanged( ConfigChangedEvent.OnConfigChangedEvent event )
    {
        if( event.getModID().equals( Strata.modid ) )
            INSTANCE.sync( false );
    }
}