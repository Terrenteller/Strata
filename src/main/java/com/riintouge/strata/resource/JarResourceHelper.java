package com.riintouge.strata.resource;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;
import java.util.function.Function;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JarResourceHelper
{
    public static final String JAR_PATH_SEPARATOR = "/";

    protected final Class clazz;
    protected final List< String > resources = new ArrayList<>();

    public JarResourceHelper( Class clazz ) throws IOException
    {
        this.clazz = clazz;

        JarFile jar = new JarFile( getTopLevelClassFile( clazz ) );
        Enumeration< JarEntry > entries = jar.entries();
        while( entries.hasMoreElements() )
        {
            JarEntry entry = entries.nextElement();
            if( !entry.isDirectory() )
                resources.add( entry.getName() );
        }
    }

    @Nonnull
    public List< String > find( Function< String , Boolean > predicate )
    {
        List< String > matches = new Vector<>();

        for( String path : resources )
            if( predicate.apply( path ) )
                matches.add( path );

        return matches;
    }

    public InputStream getResource( String internalFilePath )
    {
        return clazz.getClassLoader().getResourceAsStream( internalFilePath );
    }

    public String getTopLevelClassFile( Class clazz )
    {
        Pattern regex = Pattern.compile( "^(?:[a-z]+:)*([^!]+)" );
        String classPath = clazz.getProtectionDomain().getCodeSource().getLocation().toString();
        Matcher match = regex.matcher( classPath );

        if( !match.find() )
            throw new IllegalArgumentException( clazz.getCanonicalName() );

        return match.group( 1 );
    }
}
