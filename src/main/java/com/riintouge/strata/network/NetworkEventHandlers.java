package com.riintouge.strata.network;

import com.riintouge.strata.StrataConfig;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@EventBusSubscriber
public class NetworkEventHandlers
{
    @SideOnly( Side.CLIENT ) // Fired when the client connects to a server
    @SubscribeEvent( priority = EventPriority.LOWEST )
    public static void clientConnectedToServerEvent( FMLNetworkEvent.ClientConnectedToServerEvent event )
    {
        //Strata.LOGGER.trace( "NetworkEventHandlers.clientConnectedToServerEvent()" );
    }

    @SideOnly( Side.SERVER ) // Fired when a client connects
    @SubscribeEvent( priority = EventPriority.LOWEST )
    public static void serverConnectionFromClientEvent( FMLNetworkEvent.ServerConnectionFromClientEvent event )
    {
        //Strata.LOGGER.trace( "NetworkEventHandlers.serverConnectionFromClientEvent()" );
    }

    @SideOnly( Side.SERVER ) // Fired after a client connects
    @SubscribeEvent( priority = EventPriority.LOWEST )
    public static void playerLoggedInEvent( PlayerEvent.PlayerLoggedInEvent event )
    {
        //Strata.LOGGER.trace( "NetworkEventHandlers.playerLoggedInEvent()" );

        if( StrataConfig.enforceClientSynchronization )
        {
            // TODO: Start this asynchronously so we can re-send packets and kick unresponsive clients
            EntityPlayer player = event.player;
            if( player instanceof EntityPlayerMP )
                StrataNetworkManager.INSTANCE.sendTo( new HostRequestMessage() , (EntityPlayerMP)player );
        }
    }

    @SideOnly( Side.CLIENT ) // Fired when either side initiates a disconnection
    @SubscribeEvent( priority = EventPriority.LOWEST )
    public static void clientDisconnectionFromServerEvent( FMLNetworkEvent.ClientDisconnectionFromServerEvent event )
    {
        //Strata.LOGGER.trace( "NetworkEventHandlers.clientDisconnectionFromServerEvent()" );
    }

    @SideOnly( Side.SERVER ) // Fired when the server initiates the disconnection
    @SubscribeEvent( priority = EventPriority.LOWEST )
    public static void serverDisconnectionFromClientEvent( FMLNetworkEvent.ServerDisconnectionFromClientEvent event )
    {
        //Strata.LOGGER.trace( "NetworkEventHandlers.serverDisconnectionFromClientEvent()" );
    }

    @SideOnly( Side.SERVER ) // Fired after a client disconnects
    @SubscribeEvent( priority = EventPriority.LOWEST )
    public static void playerLoggedOutEvent( PlayerEvent.PlayerLoggedOutEvent event )
    {
        //Strata.LOGGER.trace( "NetworkEventHandlers.playerLoggedOutEvent()" );
    }
}
