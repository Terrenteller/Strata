package com.riintouge.strata.command;

import com.riintouge.strata.Strata;
import com.riintouge.strata.util.DebugUtil;
import com.riintouge.strata.util.StringUtil;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.oredict.OreDictionary;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DumpOreDictCommand extends CommandBase
{
    public DumpOreDictCommand()
    {
        // Nothing to do
    }

    // CommandBase overrides

    @Override
    public void execute( MinecraftServer server , ICommandSender sender , String[] args ) throws CommandException
    {
        try
        {
            List< Pattern > patterns = new ArrayList<>();
            for( String arg : args )
                patterns.add( Pattern.compile( arg , Pattern.CASE_INSENSITIVE ) );

            List< OreDictStringifier > oreDictEntries = new ArrayList<>();
            for( String oreName : OreDictionary.getOreNames() )
            {
                if( patterns.size() > 0 )
                {
                    boolean match = false;
                    for( Pattern pattern : patterns )
                    {
                        Matcher matcher = pattern.matcher( oreName );
                        if( matcher.find() )
                        {
                            match = true;
                            break;
                        }
                    }

                    if( !match )
                        continue;
                }

                oreDictEntries.add( new OreDictStringifier( oreName ) );
            }
            oreDictEntries.sort( Comparator.comparing( x -> x.oreName ) );

            StringBuilder logMessage = new StringBuilder( String.format( "Found %d ore groups\n" , oreDictEntries.size() ) );
            logMessage.append( StringUtil.join( "\n" , oreDictEntries , OreDictStringifier::toString ) );
            Strata.LOGGER.info( logMessage.toString() );

            String chatMessage = String.format( "Recorded %d ore groups to the game log." , oreDictEntries.size() );
            notifyCommandListener( sender , this , chatMessage );
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
        return "oreDict";
    }

    @Override
    public List< String > getTabCompletions( MinecraftServer server , ICommandSender sender , String[] args , @Nullable BlockPos targetPos )
    {
        return Collections.emptyList();
    }

    @Override
    public String getUsage( ICommandSender sender )
    {
        return "/strata dump oreDict";
    }

    // Nested classes

    public class OreDictStringifier
    {
        public final String oreName;

        public OreDictStringifier( String oreName )
        {
            this.oreName = oreName;
        }

        @Override
        public String toString()
        {
            NonNullList< ItemStack > itemStacks = OreDictionary.getOres( oreName , false );
            List< Pair< String , ItemStack > > pairs = new ArrayList<>();
            List< String > lines = new ArrayList<>();
            lines.add( oreName );

            for( ItemStack itemStack : itemStacks )
                pairs.add( new ImmutablePair<>( itemStack.getItem().getRegistryName().toString() , itemStack ) );
            pairs.sort( Comparator.comparing( Pair::getKey ) );

            if( pairs.size() > 0 )
            {
                for( Pair< String , ItemStack > pair : pairs )
                {
                    ItemStack itemStack = pair.getValue();
                    Item item = itemStack.getItem();

                    lines.add(
                        String.format(
                            "\t%s%s%s \"%s\"",
                            item instanceof ItemBlock ? "BLOCK " : "ITEM  ",
                            itemStack.getItem().getRegistryName().toString(),
                            itemStack.getHasSubtypes() ? String.format( ":%d" , itemStack.getMetadata() ) : "",
                            TextFormatting.getTextWithoutFormattingCodes( item.getItemStackDisplayName( itemStack ) ) ) );
                }
            }
            else
                lines.add( "\tEMPTY" );

            return StringUtil.join( "\n" , lines , x -> x );
        }
    }
}
