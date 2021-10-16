package com.riintouge.strata.network;

import com.riintouge.strata.Strata;
import com.riintouge.strata.StrataConfig;
import com.riintouge.strata.block.MetaResourceLocation;
import com.riintouge.strata.block.geo.HostRegistry;
import com.riintouge.strata.misc.ByteBufStream;
import io.netty.buffer.ByteBuf;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.util.HashSet;
import java.util.Set;

public final class HostResponseMessage implements IMessage
{
    private Set< MetaResourceLocation > hosts = null;

    public HostResponseMessage()
    {
        // Nothing to do, but required
    }

    // IMessage overrides

    @Override
    public void toBytes( ByteBuf buf )
    {
        ByteBufStream stream = new ByteBufStream( buf );
        hosts = HostRegistry.INSTANCE.allHostResources();

        stream.write( hosts.size() );
        for( MetaResourceLocation metaResource : hosts )
            stream.write( metaResource.toString() );
    }

    @Override
    public void fromBytes( ByteBuf buf )
    {
        ByteBufStream stream = new ByteBufStream( buf );
        hosts = new HashSet<>();

        int hostCount = stream.readInt();
        for( int index = 0 ; index < hostCount ; index++ )
            hosts.add( new MetaResourceLocation( stream.readString() ) );
    }

    // Nested classes

    public static final class Handler extends ObservableMessageHandler< HostResponseMessage , IMessage >
    {
        public static final Handler INSTANCE = new Handler();

        private Handler()
        {
            // Nothing to do
        }

        // ObservableMessageHandler overrides

        @Override
        public IMessage onMessage( HostResponseMessage message , MessageContext ctx )
        {
            Strata.LOGGER.trace( "HostResponseMessage::Handler::onMessage()" );

            Set< MetaResourceLocation > serverHosts = HostRegistry.INSTANCE.allHostResources();
            serverHosts.removeAll( message.hosts );
            if( serverHosts.isEmpty() )
            {
                notifyObservers( message , ctx , true );
                return null;
            }

            // Why does TextComponentTranslation not localize here?
            TextComponentString text = new TextComponentString(
                String.format(
                    net.minecraft.util.text.translation.I18n.translateToLocal(
                        StrataConfig.enforceClientSynchronization
                            ? "strata.multiplayer.disconnect.missingHosts"
                            : "strata.multiplayer.warning.missingHosts" ),
                    serverHosts.iterator().next(),
                    serverHosts.size() - 1 ) );

            if( StrataConfig.enforceClientSynchronization )
                ctx.getServerHandler().player.connection.disconnect( text );
            else
                ctx.getServerHandler().player.sendMessage( text );

            notifyObservers( message , ctx , !StrataConfig.enforceClientSynchronization );
            return null;
        }
    }
}
