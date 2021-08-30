package com.riintouge.strata.resource;

public class DocsDir extends RootDir
{
    public static final DocsDir INSTANCE = new DocsDir();

    public DocsDir()
    {
        super( "docs" );
    }
}
