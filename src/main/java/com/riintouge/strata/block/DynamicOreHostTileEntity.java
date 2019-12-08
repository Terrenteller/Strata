package com.riintouge.strata.block;

import com.riintouge.strata.property.UnlistedPropertyHostRock;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.property.IExtendedBlockState;

import javax.annotation.Nullable;

public class DynamicOreHostTileEntity extends TileEntity
{
    private String cachedHost = UnlistedPropertyHostRock.DEFAULT;

    public DynamicOreHostTileEntity()
    {
        // Nothing to do
    }

    @Override
    public void onLoad()
    {
        super.onLoad();

        // TODO: Can we save the stone type as part of NBT data? May help cut down on corner cases.

        // Only the server should poll
        if( !world.isRemote )
            world.scheduleBlockUpdate( pos , this.getBlockType() , 20 , 10 );
    }

    public void pollHost()
    {
        if( world.isRemote )
            return; // Clients should only get this property from the server

        IBlockState state = world.getBlockState( pos );
        if( !( state instanceof IExtendedBlockState ) )
            return;

        if( !cachedHost.equalsIgnoreCase( UnlistedPropertyHostRock.DEFAULT ) )
            return; // Host has been determined. Stop polling

        cachedHost = UnlistedPropertyHostRock.determineHostAdjacent( world , pos );
        if( cachedHost.equalsIgnoreCase( UnlistedPropertyHostRock.DEFAULT ) )
        {
            // Unable to determine host. Try again later
            //System.out.println( "Unable to determine host" );

            // FIXME: We can't exactly "timeout". Consider an ore block that has a single,
            // adjacent stone in the neighbouring, but unloaded, chunk. Can a chunk load
            // trigger a block update?

            world.scheduleBlockUpdate( pos , this.getBlockType() , 20 , 10 );
            return;
        }

        world.markBlockRangeForRenderUpdate( pos , pos );
        // TODO: Dig up the documentation for flags 1 and 2
        world.notifyBlockUpdate( pos , state , state , 3 );
        markDirty();
    }

    public String getCachedHost()
    {
        return cachedHost;
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

        if( !cachedHost.equalsIgnoreCase( UnlistedPropertyHostRock.DEFAULT ) )
            tag.setString( UnlistedPropertyHostRock.PROPERTY.getName() , cachedHost );

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
        String host = tag.getString( UnlistedPropertyHostRock.PROPERTY.getName() );
        if( host.equalsIgnoreCase( UnlistedPropertyHostRock.DEFAULT ) )
            return; // The server doesn't know either

        cachedHost = host;
        world.notifyBlockUpdate( pos , state , state , 3 );
    }
}
