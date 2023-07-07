package com.riintouge.strata.command;

import com.riintouge.strata.Strata;
import com.riintouge.strata.misc.MetaResourceLocation;
import com.riintouge.strata.util.DebugUtil;
import com.riintouge.strata.util.Util;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nullable;
import java.text.NumberFormat;
import java.util.*;

public class CountBlocksCommand extends CommandBase
{
    public enum FilterMode
    {
        FILTER_OUT,
        FILTER_TO
    }

    public CountBlocksCommand()
    {
        // Nothing to do
    }

    public void gatherBlockData( Map< IBlockState , BlockData > blockDataMap , Chunk chunk )
    {
        for( int chunkPartIndex = 0 ; chunkPartIndex < 16 ; chunkPartIndex++ )
        {
            ExtendedBlockStorage chunkPart = chunk.getBlockStorageArray()[ chunkPartIndex ];
            if( chunkPart == null )
                continue; // Assume no blocks in this volume is not an error

            for( int x = 0 ; x < 16 ; x++ )
            {
                for( int y = 0 ; y < 16 ; y++ )
                {
                    for( int z = 0 ; z < 16 ; z++ )
                    {
                        IBlockState blockState = chunkPart.get( x , y , z );
                        BlockData blockData = blockDataMap.computeIfAbsent( blockState , data ->
                        {
                            Block block = blockState.getBlock();
                            BlockData newData = new BlockData();
                            newData.blockState = blockState;
                            newData.resourceLocation = block.getRegistryName();
                            newData.meta = block.getMetaFromState( blockState );
                            return newData;
                        } );

                        blockData.count++;
                    }
                }
            }
        }
    }

    public void applyFilter(
        Map< IBlockState , BlockData > blockDataMap,
        FilterMode filterMode,
        Set< Pair< ResourceLocation , Integer > > filter )
    {
        Set< Block > blockFilter = new HashSet<>();
        Set< IBlockState > blockStateFilter = new HashSet<>();

        for( Pair< ResourceLocation , Integer > resourceLocationAndMeta : filter )
        {
            Block block = Block.getBlockFromName( resourceLocationAndMeta.getLeft().toString() );
            Integer meta = resourceLocationAndMeta.getRight();

            if( meta != null )
                blockStateFilter.add( block.getStateFromMeta( meta ) );
            else
                blockFilter.add( block );
        }

        Set< IBlockState > nonMatches = new HashSet<>();
        if( filterMode == FilterMode.FILTER_OUT )
        {
            for( IBlockState blockState : blockDataMap.keySet() )
                if( blockStateFilter.contains( blockState ) || blockFilter.contains( blockState.getBlock() ) )
                    nonMatches.add( blockState );
        }
        else
        {
            for( IBlockState blockState : blockDataMap.keySet() )
                if( !blockStateFilter.contains( blockState ) && !blockFilter.contains( blockState.getBlock() ) )
                    nonMatches.add( blockState );
        }

        for( IBlockState blockState : nonMatches )
            blockDataMap.remove( blockState );
    }

    public String displayNameForVerbosity( IBlockState blockState , int meta , int verbosity )
    {
        Block block = blockState.getBlock();

        switch( verbosity )
        {
            case 0:
            {
                Item item = Item.getItemFromBlock( block );
                if( item == Items.AIR )
                {
                    // There is a chance this will be wrong for variants but there is nothing we can do about it
                    return block.getLocalizedName();
                }

                ItemStack itemStack = new ItemStack( block , 1 , meta );
                return TextFormatting.getTextWithoutFormattingCodes( item.getItemStackDisplayName( itemStack ) );
            }
            case 1:
                return block.getRegistryName().toString();
            case 2:
                return new MetaResourceLocation( blockState ).toString();
            default:
                return blockState.toString();
        }
    }

    // CommandBase overrides

    @Override
    public void execute( MinecraftServer server , ICommandSender sender , String[] args ) throws CommandException
    {
        if( args.length < 2 )
        {
            notifyCommandListener( sender , this , getUsage( sender ) );
            return;
        }

        try
        {
            World world = sender.getEntityWorld();
            Chunk centerChunk = world.getChunkFromBlockCoords( sender.getPosition() );
            int radius = Integer.parseInt( args[ 0 ] );
            int verbosity = Util.clamp( 0 , parseInt( args[ 1 ] ) , 3 );

            Set< Pair< ResourceLocation , Integer > > filter = new HashSet<>();
            if( args.length > 2 )
                for( int index = 2 ; index < args.length ; index++ )
                    filter.add( MetaResourceLocation.parseIntoPair( args[ index ] ) );

            Map< IBlockState , BlockData > blockDataMap = new HashMap<>();
            int xMinChunk = centerChunk.getPos().x - radius;
            int xMaxChunk = centerChunk.getPos().x + radius;
            int zMinChunk = centerChunk.getPos().z - radius;
            int zMaxChunk = centerChunk.getPos().z + radius;
            int chunksTotal = ( ( xMaxChunk - xMinChunk ) + 1 ) * ( ( zMaxChunk - zMinChunk ) + 1 );
            int chunksNotLoaded = 0;

            for( int xChunk = xMinChunk ; xChunk <= xMaxChunk ; xChunk++ )
            {
                for( int zChunk = zMinChunk ; zChunk <= zMaxChunk ; zChunk++ )
                {
                    // We never want to cause chunks to generate to accommodate delayed/deferred world-gen updates
                    Chunk chunk = world.getChunkProvider().getLoadedChunk( xChunk , zChunk );
                    if( chunk != null )
                        gatherBlockData( blockDataMap , chunk );
                    else
                        chunksNotLoaded++;
                }
            }

            blockDataMap.remove( Blocks.AIR.getDefaultState() );
            if( filter.size() > 0 )
                applyFilter( blockDataMap , FilterMode.FILTER_TO , filter );

            // Multiple block states may resolve to the same name depending on verbosity
            Map< String , Integer > countAggregationMap = new HashMap<>();
            int totalBlocks = 0;
            for( BlockData data : blockDataMap.values() )
            {
                String name = displayNameForVerbosity( data.blockState , data.meta , verbosity );
                int currentCount = countAggregationMap.computeIfAbsent( name , x -> 0 );
                countAggregationMap.put( name , currentCount + data.count );
                totalBlocks += data.count;
            }

            List< Pair< Integer , String > > countList = new ArrayList<>();
            for( String name : countAggregationMap.keySet() )
                countList.add( new MutablePair<>( countAggregationMap.get( name ) , name ) );

            countList.sort( ( pairA , pairB ) ->
            {
                if( pairA.getLeft() > pairB.getLeft() )
                    return -1;
                else if( pairA.getLeft() < pairB.getLeft() )
                    return 1;

                return pairA.getRight().compareTo( pairB.getRight() );
            } );

            NumberFormat numberFormatter = NumberFormat.getIntegerInstance();
            String baseMessage = String.format(
                "Found %s of %s unique block(s) in %s of %s chunk(s)",
                numberFormatter.format( totalBlocks ),
                numberFormatter.format( countAggregationMap.keySet().size() ),
                numberFormatter.format( chunksTotal - chunksNotLoaded ),
                numberFormatter.format( chunksTotal ) );

            StringBuilder logMessage = new StringBuilder( baseMessage );
            for( Pair< Integer , String > countAndName : countList )
            {
                logMessage.append(
                    String.format(
                        "\n%s x %s",
                        numberFormatter.format( countAndName.getLeft() ),
                        countAndName.getRight() ) );
            }
            Strata.LOGGER.info( logMessage.toString() );

            String chatMessage = String.format( "%s.\nCheck the game log for a complete breakdown." , baseMessage );
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
        return "countBlocks";
    }

    @Override
    public List< String > getTabCompletions( MinecraftServer server , ICommandSender sender , String[] args , @Nullable BlockPos targetPos )
    {
        if( args.length > 2 )
            return getListOfStringsMatchingLastWord( args , Block.REGISTRY.getKeys() );

        return Collections.emptyList();
    }

    @Override
    public String getUsage( ICommandSender sender )
    {
        return "/strata countBlocks <radius:int> <verbosity:int> [ResourceLocation1[:<meta>] ... ResourceLocationN[:<meta>]]";
    }

    // Nested classes

    public class BlockData
    {
        public IBlockState blockState;
        public ResourceLocation resourceLocation;
        public int meta;
        public int count;
    }
}
