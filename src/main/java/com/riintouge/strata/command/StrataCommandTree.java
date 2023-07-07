package com.riintouge.strata.command;

import net.minecraft.command.ICommandSender;
import net.minecraftforge.server.command.CommandTreeBase;

public class StrataCommandTree extends CommandTreeBase
{
    public StrataCommandTree()
    {
        addSubcommand( new CountBlocksCommand() );
        addSubcommand( new DumpCommandTree() );
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
        // Sub-commands have to specify the name of this command in their getUsage() which is not good
        return "/strata";
    }
}
