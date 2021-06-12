package com.riintouge.strata.resource;

public class Config extends Root
{
    public static final Config INSTANCE = new Config();

    public Config()
    {
        super( "config" );
    }
}
