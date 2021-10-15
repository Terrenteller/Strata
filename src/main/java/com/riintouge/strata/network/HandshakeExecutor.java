package com.riintouge.strata.network;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class HandshakeExecutor implements Observer
{
    private final ExecutorService executorService = Executors.newCachedThreadPool();
    private final ListeningExecutorService listeningExecutorService = MoreExecutors.listeningDecorator( executorService );
    private final Set< ObservableMessageHandler > messageHandlers = new HashSet<>();
    private final List< Pair< Class< ? extends IMessage > , ObservableMessageHandler > > reqReplyPairs = new ArrayList<>();
    private final Map< UUID , Pair< ListenableFuture< HandshakeResult > , MessageContainer > > handshakePairs = new HashMap<>();

    private final SimpleNetworkWrapper networkWrapper;
    private final long maxAttempts;
    private final long baseTimeoutMillis;
    private final long additionalMillisPerAttempt;

    public enum HandshakeResult
    {
        Success,
        Failure,
        Disconnected,
        NoResponse,
        Interrupted,
        Terminated,
        InternalError
    }

    public HandshakeExecutor(
        SimpleNetworkWrapper networkWrapper,
        long maxAttempts,
        long baseTimeoutMillis,
        long additionalMillisPerAttempt )
    {
        this.networkWrapper = networkWrapper;
        this.maxAttempts = Math.max( 0 , maxAttempts );
        this.baseTimeoutMillis = Math.max( 0 , baseTimeoutMillis );
        this.additionalMillisPerAttempt = Math.max( 0 , additionalMillisPerAttempt );
    }

    public void register( Class< ? extends IMessage > reqClass , ObservableMessageHandler replyHandler )
    {
        synchronized( reqReplyPairs )
        {
            reqReplyPairs.add( new ImmutablePair<>( reqClass , replyHandler ) );
            if( !messageHandlers.contains( replyHandler ) )
            {
                messageHandlers.add( replyHandler );
                replyHandler.addObserver( this );
            }
        }
    }

    public ListenableFuture< HandshakeResult > initiate( EntityPlayerMP player )
    {
        MessageContainer messageContainer = new MessageContainer();
        ListenableFuture< HandshakeResult > handshake = listeningExecutorService.submit( () ->
        {
            UUID playerID = player.getGameProfile().getId();
            HandshakeResult result = performHandshake( player , messageContainer );
            synchronized( handshakePairs )
            {
                handshakePairs.remove( playerID );
            }
            return result;
        } );

        handshakePairs.put( player.getGameProfile().getId() , new ImmutablePair<>( handshake , messageContainer ) );
        return handshake;
    }

    private HandshakeResult performHandshake( EntityPlayerMP player , MessageContainer messageContainer )
    {
        try
        {
            List< Pair< Class< ? extends IMessage > , ObservableMessageHandler > > localReqReplyPairs;
            synchronized( reqReplyPairs )
            {
                localReqReplyPairs = new ArrayList<>( reqReplyPairs );
            }

            synchronized( messageContainer )
            {
                for( Pair< Class< ? extends IMessage > , ObservableMessageHandler > reqReplyPair : localReqReplyPairs )
                {
                    int attempt = 0;
                    for( ; attempt < maxAttempts ; attempt++ )
                    {
                        if( Thread.currentThread().isInterrupted() )
                            return HandshakeResult.Interrupted;
                        else if( player.hasDisconnected() )
                            return HandshakeResult.Disconnected;
                        else if( messageContainer.terminated )
                            return HandshakeResult.Terminated;

                        networkWrapper.sendTo( reqReplyPair.getKey().newInstance() , player );
                        messageContainer.wait( baseTimeoutMillis + ( attempt * additionalMillisPerAttempt ) );

                        if( player.hasDisconnected() )
                            return HandshakeResult.Disconnected;
                        else if( messageContainer.terminated )
                            return HandshakeResult.Terminated;

                        ObservableMessageHandler.Data data = reqReplyPair.getValue().decode( messageContainer.takeData() );
                        if( data != null )
                        {
                            if( !data.success )
                                return HandshakeResult.Failure;
                            else
                                break;
                        }
                    }

                    if( attempt == maxAttempts )
                        return HandshakeResult.NoResponse;
                }
            }

            return HandshakeResult.Success;
        }
        catch( InterruptedException e )
        {
            return HandshakeResult.Interrupted;
        }
        catch( IllegalAccessException | InstantiationException e )
        {
            e.printStackTrace();
            return HandshakeResult.InternalError;
        }
    }

    public void terminate( UUID playerID )
    {
        synchronized( handshakePairs )
        {
            Pair< ListenableFuture< HandshakeResult > , MessageContainer > pair = handshakePairs.remove( playerID );
            if( pair == null )
                return;

            MessageContainer messageContainer = pair.getValue();
            synchronized( messageContainer )
            {
                messageContainer.terminated = true;
                messageContainer.notify();
            }

            ListenableFuture< HandshakeResult > handshake = pair.getKey();
            if( handshake != null )
                handshake.cancel( true );
        }
    }

    // Observer overrides

    @Override
    public void update( Observable observable , Object o )
    {
        ObservableMessageHandler handler = (ObservableMessageHandler)observable;
        ObservableMessageHandler.Data data = handler.decode( o );
        if( data == null )
            return;

        synchronized( handshakePairs )
        {
            Pair< ListenableFuture< HandshakeResult > , MessageContainer > pair = handshakePairs.getOrDefault( data.playerID , null );
            if( pair == null )
                return;

            MessageContainer messageContainer = pair.getValue();
            synchronized( messageContainer )
            {
                messageContainer.data = o;
                messageContainer.notify();
            }
        }
    }

    // Nested classes

    private class MessageContainer
    {
        public boolean terminated = false;
        public Object data = null;

        public Object takeData()
        {
            Object ret = data;
            data = null;
            return ret;
        }
    }
}
