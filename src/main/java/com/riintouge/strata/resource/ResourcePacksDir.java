package com.riintouge.strata.resource;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.ResourcePackRepository;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

public class ResourcePacksDir extends InstallationRootDir
{
    public ResourcePacksDir()
    {
        super( "resourcepacks" );
    }

    public List< String > activeResourcePackPaths() throws IOException
    {
        try
        {
            ResourcePackRepository resourceRepo = Minecraft.getMinecraft().getResourcePackRepository();
            Path resourcePacksPath = resourceRepo.getDirResourcepacks().toPath();
            return resourceRepo.getRepositoryEntries()
                .stream()
                .map( entry -> resourcePacksPath.resolve( entry.getResourcePackName() ).toAbsolutePath().toString() )
                .collect( Collectors.toList() );
        }
        catch( NoClassDefFoundError e )
        {
            // We must be running on a dedicated server
            return find( false , s -> true );
        }
    }
}
