package com.riintouge.strata;

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

public class Config
{
    public static final Config INSTANCE = new Config( Strata.modid );

    private static final String JarConfigDir = "assets/external/config/";
    private static Path GlobalConfigPath;

    private Path instConfigPath;

    public Config( @Nullable String baseConfigDir )
    {
        if( GlobalConfigPath == null )
            GlobalConfigPath = Paths.get( Loader.instance().getConfigDir().getAbsolutePath() );

        instConfigPath = baseConfigDir == null ? GlobalConfigPath : GlobalConfigPath.resolve( baseConfigDir );
    }

    public String getAbsPath( String relativePath )
    {
        return instConfigPath.resolve( relativePath ).toString();
    }

    public List< String > find( Function< String , Boolean > predicate , boolean recursive ) throws IOException
    {
        FileSelector fileSelector = new FileSelector( predicate , recursive );
        Files.walkFileTree( instConfigPath , fileSelector );
        return fileSelector.selectedFiles();
    }

    public List< String > allIn( String subDirPath , boolean recursive ) throws IOException
    {
        System.out.println( instConfigPath.resolve( subDirPath ).toString() );
        FileSelector fileSelector = new FileSelector( s -> true , recursive );
        Files.walkFileTree( instConfigPath.resolve( subDirPath ) , fileSelector );
        return fileSelector.selectedFiles();
    }

    public static void extractMissingConfigFiles()
    {
        for( String path : JarResourceHelper.INSTANCE.find( s -> s.startsWith( JarConfigDir ) ) )
        {
            String configPath = path.substring( JarConfigDir.length() );
            File targetFile = new File( GlobalConfigPath.resolve( configPath ).toString() );

            if( targetFile.exists() )
                continue;

            targetFile.getParentFile().mkdirs();
            InputStream stream = Config.class.getClassLoader().getResourceAsStream( path );

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

    private class FileSelector extends SimpleFileVisitor< Path >
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
