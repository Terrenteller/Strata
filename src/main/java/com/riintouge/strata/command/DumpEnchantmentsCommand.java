package com.riintouge.strata.command;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.riintouge.strata.Strata;
import com.riintouge.strata.util.DebugUtil;
import com.riintouge.strata.util.StringUtil;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.translation.I18n;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class DumpEnchantmentsCommand extends CommandBase
{
    public DumpEnchantmentsCommand()
    {
        // Nothing to do
    }

    @Nullable
    public String enchantmentTypeToApplicability( EnumEnchantmentType enchantmentType )
    {
        switch( enchantmentType )
        {
            case WEAPON:
                return "weapons";
            case BOW:
                return "bows";
            case DIGGER:
                return "tools";
            case FISHING_ROD:
                return "fishing rods";
            case BREAKABLE:
                return "any breakable object";
            case WEARABLE:
                return "any wearable object";
            case ARMOR:
                return "all armor";
            case ARMOR_HEAD:
                return "head armor";
            case ARMOR_CHEST:
                return "chest armor";
            case ARMOR_LEGS:
                return "leg armor";
            case ARMOR_FEET:
                return "foot armor";
        }

        return null;
    }

    // CommandBase overrides

    @Override
    public void execute( MinecraftServer server , ICommandSender sender , String[] args ) throws CommandException
    {
        try
        {
            List< EnchantmentStringifier > enchantments = Lists.newArrayList();
            for( Enchantment enchantment : Enchantment.REGISTRY )
                enchantments.add( new EnchantmentStringifier( enchantment ) );
            enchantments.sort( Comparator.comparing( x -> x.enchantment.getRegistryName() ) );

            StringBuilder logMessage = new StringBuilder( String.format( "Found %d enchantments\n" , enchantments.size() ) );
            logMessage.append( StringUtil.join( "\n" , enchantments , EnchantmentStringifier::toString ) );
            Strata.LOGGER.info( logMessage.toString() );

            String chatMessage = String.format( "Recorded %d enchantments to the game log." , enchantments.size() );
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
        return "enchantments";
    }

    @Override
    public List< String > getTabCompletions( MinecraftServer server , ICommandSender sender , String[] args , @Nullable BlockPos targetPos )
    {
        return Collections.emptyList();
    }

    @Override
    public String getUsage( ICommandSender sender )
    {
        return "/strata dump enchantments";
    }

    // Nested classes

    public class EnchantmentStringifier
    {
        public final Enchantment enchantment;
        public final List< Enchantment > conflicts;

        public EnchantmentStringifier( Enchantment enchantment )
        {
            this.enchantment = enchantment;

            ImmutableList.Builder< Enchantment > conflictBuilder = new ImmutableList.Builder<>();
            for( Enchantment otherEnchantment : Enchantment.REGISTRY )
                if( otherEnchantment != enchantment && !enchantment.isCompatibleWith( otherEnchantment ) )
                    conflictBuilder.add( otherEnchantment );

            this.conflicts = conflictBuilder.build();
        }

        @Override
        public String toString()
        {
            int levels = ( enchantment.getMaxLevel() - enchantment.getMinLevel() ) + 1;
            String applicability = enchantmentTypeToApplicability( enchantment.type );
            String name = I18n.translateToLocal( enchantment.getName() );

            List< String > lines = new ArrayList<>();
            if( !name.equalsIgnoreCase( enchantment.getName() ) )
                lines.add( String.format( "%s (%s)" , enchantment.getRegistryName().toString() , name ) );
            else
                lines.add( enchantment.getRegistryName().toString() );

            if( levels > 1 )
                lines.add( String.format( "\t%d levels, %d-%d" , levels , enchantment.getMinLevel() , enchantment.getMaxLevel() ) );
            if( applicability != null )
                lines.add( String.format( "\tApplies to %s" , applicability ) );
            if( !enchantment.isAllowedOnBooks() )
                lines.add( "\tNot allowed on book" );
            if( enchantment.isTreasureEnchantment() )
                lines.add( "\tTreasure enchantment" );
            if( enchantment.isCurse() )
                lines.add( "\tCurse" );
            if( conflicts.size() > 0 )
                lines.add( "\tConflicts with " + StringUtil.join( ", " , conflicts , x -> x.getRegistryName().toString() ) );

            return StringUtil.join( "\n" , lines , x -> x );
        }
    }
}
