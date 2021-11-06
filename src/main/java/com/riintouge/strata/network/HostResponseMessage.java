package com.riintouge.strata.network;

import com.riintouge.strata.Strata;
import com.riintouge.strata.StrataConfig;
import com.riintouge.strata.block.MetaResourceLocation;
import com.riintouge.strata.block.geo.HostRegistry;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public final class HostResponseMessage extends ZipMessage
{
    private Set< MetaResourceLocation > hosts = null;

    public HostResponseMessage()
    {
        // Nothing to do, but required
    }

    // IMessage overrides

    @Override
    public void toBytes( DataOutputStream stream ) throws IOException
    {
        hosts = HostRegistry.INSTANCE.allHostResources();

        stream.writeInt( hosts.size() );
        for( MetaResourceLocation metaResource : hosts )
            stream.writeUTF( metaResource.toString() );
    }

    @Override
    public void fromBytes( DataInputStream stream ) throws IOException
    {
        hosts = new HashSet<>();

        int hostCount = stream.readInt();
        for( int index = 0 ; index < hostCount ; index++ )
            hosts.add( new MetaResourceLocation( stream.readUTF() ) );
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

            if( message.caughtException != null )
            {
                notifyObservers( message , ctx , message.caughtException );
                return null;
            }

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
