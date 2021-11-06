package com.riintouge.strata.network;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.riintouge.strata.Strata;
import com.riintouge.strata.StrataConfig;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.concurrent.CancellationException;

public final class NetworkManager
{
    public static final NetworkManager INSTANCE = new NetworkManager();
    public final SimpleNetworkWrapper NetworkWrapper = NetworkRegistry.INSTANCE.newSimpleChannel( Strata.modid );

    private int discriminator = 0;
    private HandshakeExecutor handshakeExecutor;

    private NetworkManager()
    {
        // Nothing to do
    }

    public void init( Side side )
    {
        NetworkWrapper.registerMessage( HostRequestMessage.Handler.class , HostRequestMessage.class , discriminator++ , Side.CLIENT );
        NetworkWrapper.registerMessage( HostResponseMessage.Handler.INSTANCE , HostResponseMessage.class , discriminator++ , Side.SERVER );
        NetworkWrapper.registerMessage( BlockPropertiesRequestMessage.Handler.class , BlockPropertiesRequestMessage.class , discriminator++ , Side.CLIENT );
        NetworkWrapper.registerMessage( BlockPropertiesResponseMessage.Handler.INSTANCE , BlockPropertiesResponseMessage.class , discriminator++ , Side.SERVER );

        if( side == Side.SERVER )
        {
            // FIXME: We have to add a delay before the handshake to give the client time to connect
            // and reach a stable state. Kicking them too swiftly intermittently causes the client to
            // not receive (or display?) the message we supply because netty may throw an exception on the client.
            // [Netty Epoll Client IO #1/ERROR]: NetworkDispatcher exception
            // io.netty.channel.unix.Errors$NativeIoException: syscall:write(..) failed: Broken pipe
            //     at io.netty.channel.unix.FileDescriptor.writeAddress(..)(Unknown Source) ~[FileDescriptor.class:4.1.9.Final]
            // Netty 4.1.68.Final behaves similarly.
            handshakeExecutor = new HandshakeExecutor( NetworkWrapper , 5000 , 3 , 3000 , 0 );
            handshakeExecutor.register( HostRequestMessage.class , HostResponseMessage.Handler.INSTANCE );
            handshakeExecutor.register( BlockPropertiesRequestMessage.class , BlockPropertiesResponseMessage.Handler.INSTANCE );
        }
    }

    // Statics

    @SideOnly( Side.CLIENT ) // Fired when the client connects to a server
    @SubscribeEvent( priority = EventPriority.LOWEST )
    public static void clientConnectedToServerEvent( FMLNetworkEvent.ClientConnectedToServerEvent event )
    {
        //Strata.LOGGER.trace( "NetworkManager.clientConnectedToServerEvent()" );
    }

    @SideOnly( Side.SERVER ) // Fired when a client connects
    @SubscribeEvent( priority = EventPriority.LOWEST )
    public static void serverConnectionFromClientEvent( FMLNetworkEvent.ServerConnectionFromClientEvent event )
    {
        //Strata.LOGGER.trace( "NetworkManager.serverConnectionFromClientEvent()" );
    }

    @SideOnly( Side.SERVER ) // Fired after a client connects
    @SubscribeEvent( priority = EventPriority.LOWEST )
    public static void playerLoggedInEvent( PlayerEvent.PlayerLoggedInEvent event )
    {
        //Strata.LOGGER.trace( "NetworkManager.playerLoggedInEvent()" );

        if( !( event.player instanceof EntityPlayerMP ) )
            return;

        EntityPlayerMP serverPlayer = (EntityPlayerMP)event.player;
        ListenableFuture< HandshakeExecutor.HandshakeResult > handshake = INSTANCE.handshakeExecutor.initiate( serverPlayer );
        Futures.addCallback( handshake , new FutureCallback< HandshakeExecutor.HandshakeResult >()
        {
            @Override
            public void onSuccess( @Nullable HandshakeExecutor.HandshakeResult result )
            {
                if( result == null )
                    result = HandshakeExecutor.HandshakeResult.InternalError;

                String disconnectMessage = null;
                switch( result )
                {
                    case Success:
                    case Failure: // The message handlers will have already taken action
                    case Disconnected: // The client kicked themselves
                        return;
                    case NoResponse:
                        disconnectMessage = "strata.multiplayer.disconnect.noResponse";
                        break;
                    case Interrupted:
                    case Exception:
                    case Terminated:
                    case InternalError:
                        disconnectMessage = "strata.multiplayer.disconnect.unexpectedHandshakeError";
                        break;
                }

                // Why does TextComponentTranslation not localize here?
                if( !serverPlayer.hasDisconnected() )
                    serverPlayer.connection.disconnect(
                        new TextComponentString(
                            net.minecraft.util.text.translation.I18n.translateToLocal( disconnectMessage ) ) );
            }

            @Override
            public void onFailure( @Nonnull Throwable t )
            {
                if( t instanceof CancellationException )
                {
                    onSuccess( HandshakeExecutor.HandshakeResult.Interrupted );
                }
                else
                {
                    t.printStackTrace();
                    onSuccess( HandshakeExecutor.HandshakeResult.InternalError );
                }
            }
        } );
    }

    @SideOnly( Side.CLIENT ) // Fired when either side initiates a disconnection
    @SubscribeEvent( priority = EventPriority.LOWEST )
    public static void clientDisconnectionFromServerEvent( FMLNetworkEvent.ClientDisconnectionFromServerEvent event )
    {
        //Strata.LOGGER.trace( "NetworkManager.clientDisconnectionFromServerEvent()" );
    }

    @SideOnly( Side.SERVER ) // Fired when the server initiates the disconnection
    @SubscribeEvent( priority = EventPriority.LOWEST )
    public static void serverDisconnectionFromClientEvent( FMLNetworkEvent.ServerDisconnectionFromClientEvent event )
    {
        //Strata.LOGGER.trace( "NetworkManager.serverDisconnectionFromClientEvent()" );
    }

    @SideOnly( Side.SERVER ) // Fired after a client disconnects
    @SubscribeEvent( priority = EventPriority.LOWEST )
    public static void playerLoggedOutEvent( PlayerEvent.PlayerLoggedOutEvent event )
    {
        //Strata.LOGGER.trace( "NetworkManager.playerLoggedOutEvent()" );

        if( StrataConfig.enforceClientSynchronization && event.player instanceof EntityPlayerMP )
            INSTANCE.handshakeExecutor.terminate( event.player.getGameProfile().getId() );
    }
}
