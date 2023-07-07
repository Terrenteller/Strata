package com.riintouge.strata.command;

import net.minecraft.command.ICommandSender;
import net.minecraftforge.server.command.CommandTreeBase;

public class DumpCommandTree extends CommandTreeBase
{
    public DumpCommandTree()
    {
        addSubcommand( new DumpEnchantmentsCommand() );
    }

    // CommandTreeBase overrides

    @Override
    public String getName()
    {
        return "dump";
    }

    @Override
    public String getUsage( ICommandSender sender )
    {
        return "/strata dump";
    }
}
