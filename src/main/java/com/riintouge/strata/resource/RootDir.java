package com.riintouge.strata.resource;

import net.minecraftforge.fml.common.Loader;

import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;
import java.util.Vector;
import java.util.function.Function;

public class RootDir
{
    private static final String JarExternalDir = "assets/external";

    protected static Path GameRootDir;
    private String rootSubdirectory;
    private Path externalPath;

    public RootDir( @Nullable String subdirectory )
    {
        this.rootSubdirectory = subdirectory;

        if( GameRootDir == null )
            GameRootDir = Paths.get( Loader.instance().getConfigDir().getParentFile().getAbsolutePath() );

        externalPath = rootSubdirectory == null ? GameRootDir : GameRootDir.resolve( rootSubdirectory );
    }

    public Path resolve( String relativePath )
    {
        return externalPath.resolve( relativePath );
    }

    public List< String > find( Function< String , Boolean > predicate , boolean recursive ) throws IOException
    {
        FileSelector fileSelector = new FileSelector( predicate , recursive );
        Files.walkFileTree( externalPath , fileSelector );

        return fileSelector.selectedFiles();
    }

    public List< String > allIn( String subDirPath , boolean recursive ) throws IOException
    {
        System.out.println( resolve( subDirPath ).toString() );
        FileSelector fileSelector = new FileSelector( s -> true , recursive );
        Files.walkFileTree( resolve( subDirPath ) , fileSelector );

        return fileSelector.selectedFiles();
    }

    public void extractMissingResourceFiles()
    {
        String internalJarDir = String.format( "%s/%s/" , JarExternalDir , rootSubdirectory ).replaceAll( "\\\\" , JarResourceHelper.Separator );
        for( String path : JarResourceHelper.INSTANCE.find( s -> s.startsWith( internalJarDir ) ) )
        {
            String resourcePath = path.substring( internalJarDir.length() );
            File targetFile = new File( externalPath.resolve( resourcePath ).toString() );
            if( targetFile.exists() )
                continue;

            targetFile.getParentFile().mkdirs();
            InputStream stream = RootDir.class.getClassLoader().getResourceAsStream( path );

            try
            {
                Files.copy( stream , targetFile.toPath() , StandardCopyOption.REPLACE_EXISTING );
            }
            catch( IOException e )
            {
                e.printStackTrace();
            }
        }
    }

    protected class FileSelector extends SimpleFileVisitor< Path >
    {
        private Function< String , Boolean > predicate;
        private boolean recursive , initialDirectory = true;
        private List< String > selectedFiles = new Vector<>();

        public FileSelector( Function< String , Boolean > predicate , boolean recursive )
        {
            this.predicate = predicate;
            this.recursive = recursive;
        }

        public List< String > selectedFiles()
        {
            return selectedFiles;
        }

        @Override
        public FileVisitResult preVisitDirectory( Path path , BasicFileAttributes basicFileAttributes )
        {
            if( recursive )
                return FileVisitResult.CONTINUE;

            if( initialDirectory )
            {
                initialDirectory = false;
                return FileVisitResult.CONTINUE;
            }

            return FileVisitResult.SKIP_SUBTREE;
        }

        @Override
        public FileVisitResult visitFile( Path file , BasicFileAttributes attr )
        {
            String path = file.toAbsolutePath().toString();
            if( predicate.apply( path ) )
                selectedFiles.add( path );

            return FileVisitResult.CONTINUE;
        }
    }
}
