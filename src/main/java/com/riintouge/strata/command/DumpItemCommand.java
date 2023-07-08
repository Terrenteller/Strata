package com.riintouge.strata.command;

import com.riintouge.strata.Strata;
import com.riintouge.strata.item.ItemHelper;
import com.riintouge.strata.util.DebugUtil;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

public class DumpItemCommand extends CommandBase
{
    public DumpItemCommand()
    {
        // Nothing to do
    }

    // CommandBase overrides

    @Override
    public void execute( MinecraftServer server , ICommandSender sender , String[] args ) throws CommandException
    {
        try
        {
            ItemStack itemStack = sender instanceof EntityPlayer ? ( (EntityPlayer)sender ).getHeldItemMainhand() : null;

            if( ItemHelper.isNullOrAirOrEmpty( itemStack ) )
                notifyCommandListener( sender , this , "Command executor must be a player with an item in their main hand!" );
            else
                notifyCommandListener( sender , this , itemStack.serializeNBT().toString() );
        }
        catch( Exception e )
        {
            Strata.LOGGER.error( DebugUtil.prettyPrintThrowable( e , null ) );
            throw new CommandException( String.format( "Caught %s while executing command!" , e.getClass().getName() ) );
        }
    }

    @Override
    public String getName()
    {
        return "item";
    }

    @Override
    public List< String > getTabCompletions( MinecraftServer server , ICommandSender sender , String[] args , @Nullable BlockPos targetPos )
    {
        return Collections.emptyList();
    }

    @Override
    public String getUsage( ICommandSender sender )
    {
        return "/strata dump item";
    }
}
