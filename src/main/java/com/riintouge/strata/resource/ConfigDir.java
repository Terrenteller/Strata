package com.riintouge.strata.resource;

public class ConfigDir extends RootDir
{
    public static final ConfigDir INSTANCE = new ConfigDir();

    public ConfigDir()
    {
        super( "config" );
    }
}
