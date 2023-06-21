package com.riintouge.strata.command;

import net.minecraft.command.ICommandSender;
import net.minecraftforge.server.command.CommandTreeBase;

public class StrataCommandTree extends CommandTreeBase
{
    public StrataCommandTree()
    {
        addSubcommand( new CountBlocksCommand() );
    }

    // CommandTreeBase overrides

    @Override
    public String getName()
    {
        return "strata";
    }

    @Override
    public String getUsage( ICommandSender sender )
    {
        return "/strata";
    }
}
