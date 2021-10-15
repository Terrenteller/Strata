package com.riintouge.strata.network;

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
            Set< MetaResourceLocation > serverHosts = HostRegistry.INSTANCE.allHostResources();
            serverHosts.removeAll( message.hosts );
            notifyObservers( message , ctx , serverHosts.isEmpty() );

            if( !serverHosts.isEmpty() )
            {
                // Why does TextComponentTranslation not localize here?
                ctx.getServerHandler().player.connection.disconnect(
                    new TextComponentString(
                        String.format(
                            net.minecraft.util.text.translation.I18n.translateToLocal( "strata.multiplayer.disconnect.missingHosts" ),
                            serverHosts.iterator().next(),
                            serverHosts.size() - 1 ) ) );
            }

            return null;
        }
    }
}
