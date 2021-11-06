package com.riintouge.strata.resource;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.ResourcePackRepository;

import javax.annotation.Nullable;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

public class ResourcePacksDir extends InstallationRootDir
{
    public static final ResourcePacksDir INSTANCE = new ResourcePacksDir();

    public ResourcePacksDir()
    {
        super( "resourcepacks" );
    }

    public @Nullable List< String > activeResourcePackPaths()
    {
        try
        {
            ResourcePackRepository resourceRepo = Minecraft.getMinecraft().getResourcePackRepository();
            final Path resourcePacksPath = resourceRepo.getDirResourcepacks().toPath();
            return resourceRepo.getRepositoryEntries()
                .stream()
                .map( entry -> resourcePacksPath.resolve( entry.getResourcePackName() ).toAbsolutePath().toString() )
                .collect( Collectors.toList() );
        }
        catch( NoClassDefFoundError e )
        {
            // We must be running on a dedicated server
            return null;
        }
    }
}
