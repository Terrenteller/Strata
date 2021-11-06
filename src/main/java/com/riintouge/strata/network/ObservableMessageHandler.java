package com.riintouge.strata.network;

import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

import javax.annotation.Nullable;
import java.util.Observable;
import java.util.UUID;

public abstract class ObservableMessageHandler< REQ extends IMessage , REPLY extends IMessage >
    extends Observable
    implements IMessageHandler< REQ , REPLY >
{
    protected void notifyObservers( REQ message , MessageContext ctx , boolean success )
    {
        setChanged();
        notifyObservers( new Data( message , ctx , success , null ) );
    }

    protected void notifyObservers( REQ message , MessageContext ctx , Exception exception )
    {
        setChanged();
        notifyObservers( new Data( message , ctx , false , exception ) );
    }

    @Nullable
    public Data decode( @Nullable Object o )
    {
        return Data.class.isInstance( o ) ? (Data)o : null;
    }

    // IMessageHandler overrides

    @Override
    public REPLY onMessage( REQ message , MessageContext ctx )
    {
        notifyObservers( message , ctx , true );
        return null;
    }

    // Nested classes

    public class Data
    {
        public final REQ message;
        public final MessageContext ctx;
        public final UUID playerID;
        public final boolean success;
        public final Exception exception;

        public Data( REQ message , MessageContext ctx , boolean success , Exception exception )
        {
            this.message = message;
            this.ctx = ctx;
            this.playerID = ctx.side == Side.CLIENT
                ? ctx.getClientHandler().getGameProfile().getId()
                : ctx.getServerHandler().player.getGameProfile().getId();
            this.success = success;
            this.exception = exception;
        }
    }
}
