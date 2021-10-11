package com.riintouge.strata.network;

import com.riintouge.strata.Strata;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public class StrataNetworkManager
{
    public static final SimpleNetworkWrapper INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel( Strata.modid );
    private static int discriminator = 0;

    public static void init()
    {
        if( discriminator > 0 )
            return;

        INSTANCE.registerMessage( HostRequestMessage.Handler.class , HostRequestMessage.class , discriminator++ , Side.CLIENT );
        INSTANCE.registerMessage( HostResponseMessage.Handler.class , HostResponseMessage.class , discriminator++ , Side.SERVER );
        INSTANCE.registerMessage( BlockPropertiesRequestMessage.Handler.class , BlockPropertiesRequestMessage.class , discriminator++ , Side.CLIENT );
        INSTANCE.registerMessage( BlockPropertiesResponseMessage.Handler.class , BlockPropertiesResponseMessage.class , discriminator++ , Side.SERVER );
    }
}
