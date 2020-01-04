package com.riintouge.strata;

import java.io.IOException;
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
    public static final JarResourceHelper INSTANCE = new JarResourceHelper();

    private List< String > resourcePaths;

    private JarResourceHelper()
    {
        resourcePaths = getResources( JarResourceHelper.class );
    }

    public List< String > find( Function< String , Boolean > predicate )
    {
        List< String > matches = new Vector<>();

        for( String path : resourcePaths )
            if( predicate.apply( path ) )
                matches.add( path );

        return matches;
    }

    private String getClassJar( Class clazz )
    {
        Pattern regex = Pattern.compile( "^(?:[a-z]+:)*([^!]+)" );
        String classPath = clazz.getProtectionDomain().getCodeSource().getLocation().toString();
        Matcher match = regex.matcher( classPath );

        return match.find() ? match.group( 1 ) : null;
    }

    private List< String > getResources( Class clazz )
    {
        List< String > paths = new Vector<>();

        try
        {
            JarFile jar = new JarFile( getClassJar( clazz ) );
            Enumeration< JarEntry > entries = jar.entries();

            while( entries.hasMoreElements() )
            {
                JarEntry entry = entries.nextElement();

                if( entry.isDirectory() )
                    continue;

                String name = entry.getName();
                if( name.startsWith( "assets/" ) )
                    paths.add( name );
            }
        }
        catch( IOException | NullPointerException e )
        {
            // TODO
        }

        return paths;
    }
}
