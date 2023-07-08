package com.riintouge.strata.command;

import com.riintouge.strata.Strata;
import com.riintouge.strata.item.ItemHelper;
import com.riintouge.strata.misc.TreeIndenter;
import com.riintouge.strata.util.CollectionUtil;
import com.riintouge.strata.util.DebugUtil;
import com.riintouge.strata.util.Util;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.*;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import org.apache.commons.lang3.NotImplementedException;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DumpItemCommand extends CommandBase
{
    public DumpItemCommand()
    {
        // Nothing to do
    }

    @Nullable
    public String simpleTagToString( NBTBase tag )
    {
        switch( tag.getId() )
        {
            case 1: // byte
                return String.format( "%d" , ( (NBTTagByte)tag ).getByte() );
            case 2: // short
                return String.format( "%d" , ( (NBTTagShort)tag ).getShort() );
            case 3: // int
                return String.format( "%d" , ( (NBTTagInt)tag ).getInt() );
            case 4: // long
                return String.format( "%d" , ( (NBTTagLong)tag ).getLong() );
            case 5: // float
                return String.format( "%f" , ( (NBTTagFloat)tag ).getFloat() );
            case 6: // double
                return String.format( "%f" , ( (NBTTagDouble)tag ).getDouble() );
            case 7: // byte array
                return String.format( "%s" , ( (NBTTagByteArray)tag ).toString().replace( "B;" , "" ) );
            case 8: // string
                return String.format( "\"%s\"" , ( (NBTTagString)tag ).getString() );
            case 9: // list
            {
                NBTTagList tagList = (NBTTagList)tag;
                if( tagList.getTagType() == 10 ) // compound
                    throw new UnsupportedOperationException();

                List< String > values = new ArrayList<>();
                for( NBTBase subTag : tagList )
                    values.add( simpleTagToString( subTag ) );

                return String.format( "[%s]" , String.join( "," , values ) );
            }
            case 10: // compound
                throw new UnsupportedOperationException();
            case 11: // int array
                return String.format( "%s" , ( (NBTTagIntArray)tag ).toString().replace( "I;" , "" ) );
            case 12: // long array
                return String.format( "%s" , ( (NBTTagLongArray)tag ).toString().replace( "L;" , "" ) );
            default:
                throw new NotImplementedException( String.format( "Unknown NBT ID '%d'!" , tag.getId() ) );
        }
    }

    public void prettyPrintNBT( NBTTagCompound compound , TreeIndenter tree , List< String > lines )
    {
        if( compound.hasNoTags() )
            return;

        List< String > keys = new ArrayList<>( compound.getKeySet() );
        keys.sort( String::compareToIgnoreCase );

        List< Pair< String , NBTBase > > tags = new ArrayList<>();
        for( String key : keys )
            tags.add( new ImmutablePair<>( key , compound.getTag( key ) ) );

        tree.indent( TreeIndenter.Indent.CHILD );

        for( CollectionUtil.EnumeratedElement< Pair< String , NBTBase > > element : CollectionUtil.enumerate( tags ) )
        {
            tree.reindent( element.isLast ? TreeIndenter.Indent.LAST_CHILD : TreeIndenter.Indent.CHILD );

            String name = element.element.getKey();
            NBTBase tag = element.element.getValue();

            if( tag.getId() == 9 ) // list
            {
                NBTTagList tagList = (NBTTagList)tag;
                if( tagList.getTagType() == 10 ) // compound
                {
                    for( int index = 0 ; index < tagList.tagCount() ; index++ )
                    {
                        tree.reindent( element.isLast ? TreeIndenter.Indent.LAST_CHILD : TreeIndenter.Indent.CHILD );
                        lines.add( String.format( "%s%s[%d]" , tree.toString() , name , index ) );
                        tree.reindent( element.isLast && index == ( tagList.tagCount() - 1 ) ? TreeIndenter.Indent.BLANK : TreeIndenter.Indent.CONT );
                        prettyPrintNBT( (NBTTagCompound)tagList.get( index ) , tree , lines );
                    }

                    continue;
                }
            }
            else if( tag.getId() == 10 ) // compound
            {
                lines.add( String.format( "%s%s" , tree.toString() , name ) );
                tree.reindent( element.isLast ? TreeIndenter.Indent.BLANK : TreeIndenter.Indent.CONT );
                prettyPrintNBT( (NBTTagCompound)tag , tree , lines );

                continue;
            }

            lines.add( String.format( "%s%s -> %s" , tree.toString() , name , simpleTagToString( tag ) ) );
        }

        tree.unindent();
    }

    // CommandBase overrides

    @Override
    public void execute( MinecraftServer server , ICommandSender sender , String[] args ) throws CommandException
    {
        try
        {
            ItemStack itemStack = sender instanceof EntityLivingBase ? ( (EntityLivingBase)sender ).getHeldItemMainhand() : null;
            if( ItemHelper.isNullOrAirOrEmpty( itemStack ) )
            {
                notifyCommandListener( sender , this , "Command executor must be an entity with an item in their main hand!" );
                return;
            }

            int verbosity = args.length >= 1 ? Util.clamp( 0 , parseInt( args[ 0 ] ) , 1 ) : 0;
            switch( verbosity )
            {
                case 0:
                {
                    notifyCommandListener( sender , this , itemStack.serializeNBT().toString() );
                    break;
                }
                default:
                {
                    // "%s-- " looks great in the monospaced log, but not in chat
                    TreeIndenter tree = new TreeIndenter( "%s " );
                    List< String > lines = new ArrayList<>();
                    lines.add( String.format( "\"%s\"" , itemStack.getItem().getItemStackDisplayName( itemStack ) ) );
                    prettyPrintNBT( itemStack.serializeNBT() , tree , lines );
                    notifyCommandListener( sender , this , String.join( "\n" , lines ) );
                }
            }
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
    public int getRequiredPermissionLevel()
    {
        return 2;
    }

    @Override
    public List< String > getTabCompletions( MinecraftServer server , ICommandSender sender , String[] args , @Nullable BlockPos targetPos )
    {
        return Collections.emptyList();
    }

    @Override
    public String getUsage( ICommandSender sender )
    {
        return "/strata dump item <verbosity:int>";
    }
}
