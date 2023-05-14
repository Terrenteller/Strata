package com.riintouge.strata.resource;

import com.google.common.collect.ImmutableList;
import net.minecraftforge.fml.common.Loader;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Collections;
import java.util.List;
import java.util.Vector;
import java.util.function.Function;

public class InstallationRootDir
{
    public static final Path ABS_ROOT_DIR = Paths.get( Loader.instance().getConfigDir().getParentFile().getAbsolutePath() );

    public final String subdirectory;
    public final String internalDir;
    public final Path externalDir;

    public InstallationRootDir( @Nullable String subdirectory )
    {
        Path externalDirInternalPath = Paths.get( "assets/external" );

        if( subdirectory != null )
        {
            externalDir = ABS_ROOT_DIR.resolve( subdirectory ).normalize();
            this.subdirectory = externalDir.toString().substring( ABS_ROOT_DIR.toString().length() + 1 );
            externalDirInternalPath = externalDirInternalPath.resolve( this.subdirectory );
        }
        else
        {
            externalDir = ABS_ROOT_DIR;
            this.subdirectory = null;
        }

        internalDir = externalDirInternalPath.toString()
            .replaceAll( "\\\\" , JarResourceHelper.JAR_PATH_SEPARATOR )
            .concat( JarResourceHelper.JAR_PATH_SEPARATOR ); // end with the separator for consumer convenience
    }

    @Nonnull
    public List< String > find( boolean recursive , Function< String , Boolean > predicate ) throws IOException
    {
        FileSelector fileSelector = new FileSelector( recursive , predicate );
        Files.walkFileTree( externalDir , fileSelector );
        return fileSelector.selectedFiles();
    }

    @Nonnull
    public List< String > allIn( String subDirPath , boolean recursive ) throws IOException
    {
        try
        {
            FileSelector fileSelector = new FileSelector( recursive , s -> true );
            Files.walkFileTree( externalDir.resolve( subDirPath ) , fileSelector );
            return fileSelector.selectedFiles();
        }
        catch( NoSuchFileException e )
        {
            // No files in a path which doesn't exist
            return Collections.emptyList();
        }
    }

    public void extractResources( JarResourceHelper resourceHelper , boolean overwrite ) throws IOException
    {
        for( String internalResource : resourceHelper.find( s -> s.startsWith( internalDir ) ) )
        {
            String relativeResourcePath = internalResource.substring( internalDir.length() );
            File targetFile = new File( externalDir.resolve( relativeResourcePath ).toString() );
            if( targetFile.exists() && !overwrite )
                continue;

            targetFile.getParentFile().mkdirs(); // Ignore the return because we expect Files.copy() to complain
            Files.copy( resourceHelper.getResource( internalDir ) , targetFile.toPath() , StandardCopyOption.REPLACE_EXISTING );
        }
    }

    // Nested classes

    protected class FileSelector extends SimpleFileVisitor< Path >
    {
        protected Function< String , Boolean > predicate;
        protected boolean recursive , initialDirectory = true;
        protected List< String > selectedFiles = new Vector<>();

        public FileSelector( boolean recursive , Function< String , Boolean > predicate )
        {
            this.recursive = recursive;
            this.predicate = predicate;
        }

        @Nonnull
        public List< String > selectedFiles()
        {
            return ImmutableList.copyOf( selectedFiles );
        }

        // SimpleFileVisitor overrides

        @Nonnull
        @Override
        public FileVisitResult preVisitDirectory( Path path , BasicFileAttributes basicFileAttributes )
        {
            if( recursive )
                return FileVisitResult.CONTINUE;
            else if( !initialDirectory )
                return FileVisitResult.SKIP_SUBTREE;

            initialDirectory = false;
            return FileVisitResult.CONTINUE;
        }

        @Nonnull
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
