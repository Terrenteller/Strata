package com.riintouge.strata.block.ore;

import com.riintouge.strata.block.MetaResourceLocation;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.property.IExtendedBlockState;

import javax.annotation.Nullable;

public class OreBlockTileEntity extends TileEntity
{
    private MetaResourceLocation cachedHost = UnlistedPropertyHostRock.DEFAULT;

    public OreBlockTileEntity()
    {
        // Nothing to do
    }

    public MetaResourceLocation getCachedHost()
    {
        return cachedHost;
    }

    public void pollHost()
    {
        if( world.isRemote )
            return; // Clients should only get this property from the server

        IBlockState state = world.getBlockState( pos );
        if( !( state instanceof IExtendedBlockState ) )
            return;

        if( !cachedHost.equals( UnlistedPropertyHostRock.DEFAULT ) )
            return; // Host has been determined. Stop polling

        cachedHost = UnlistedPropertyHostRock.findHost( world , pos );
        if( cachedHost.equals( UnlistedPropertyHostRock.DEFAULT ) )
        {
            //System.out.println( "Unable to determine host" );

            // FIXME: We can't exactly "timeout". Consider an ore block that has a single,
            // adjacent stone in the neighbouring, but unloaded, chunk. Can a chunk load
            // trigger a block update? Should we remove the block and pretend like it never existed?

            // WARNING: getBlockType() documentation claims it's client side but is not annotated as such
            world.scheduleBlockUpdate( pos , this.getBlockType() , 20 , 10 );
            return;
        }

        world.markBlockRangeForRenderUpdate( pos , pos );
        // 1 << 0 will cause a block update
        // 1 << 1 will send the change to clients
        // 1 << 2 will prevent the block from being re-rendered if this is a client world
        // 1 << 3 will force any re-renders to run on the main thread instead of the worker pool, if this is a client world and flag 3 is clear
        // 1 << 4 will prevent observers from seeing this change
        world.notifyBlockUpdate( pos , state , state , 3 );
        markDirty();
    }

    // TileEntity overrides

    @Override
    public void onLoad()
    {
        super.onLoad();

        // TODO: Can we save the stone type as part of NBT data? May help cut down on corner cases.

        // Only the server should poll
        if( !world.isRemote )
            world.scheduleBlockUpdate( pos , this.getBlockType() , 20 , 10 );
    }

    @Override
    @Nullable
    public SPacketUpdateTileEntity getUpdatePacket()
    {
        return new SPacketUpdateTileEntity( this.pos , 3 , this.getUpdateTag() );
    }

    @Override
    public NBTTagCompound getUpdateTag()
    {
        NBTTagCompound tag = new NBTTagCompound();

        if( !cachedHost.equals( UnlistedPropertyHostRock.DEFAULT ) )
            tag.setString( UnlistedPropertyHostRock.PROPERTY.getName() , cachedHost.toString() );

        return this.writeToNBT( tag );
    }

    @Override
    public void onDataPacket( NetworkManager net , SPacketUpdateTileEntity packet )
    {
        super.onDataPacket( net , packet );

        handleUpdateTag( packet.getNbtCompound() );
    }

    @Override
    public void handleUpdateTag( NBTTagCompound tag )
    {
        super.readFromNBT( tag );

        // The server should ignore any updates to this block from clients
        if( !world.isRemote )
            return;

        IBlockState state = world.getBlockState( pos );
        String hostString = tag.getString( UnlistedPropertyHostRock.PROPERTY.getName() );
        if( !hostString.isEmpty() )
        {
            MetaResourceLocation host = new MetaResourceLocation( hostString );
            if( host.equals( UnlistedPropertyHostRock.DEFAULT ) )
                return; // The server doesn't know either

            cachedHost = host;
            world.notifyBlockUpdate( pos , state , state , 3 );
        }
    }
}
