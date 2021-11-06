package com.riintouge.strata.resource;

public class DocsDir extends InstallationRootDir
{
    public static final DocsDir INSTANCE = new DocsDir();

    public DocsDir()
    {
        super( "docs" );
    }
}
