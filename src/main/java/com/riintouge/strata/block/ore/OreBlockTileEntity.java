package com.riintouge.strata.block.ore;

import com.riintouge.strata.block.MetaResourceLocation;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class OreBlockTileEntity extends TileEntity
{
    private Boolean isActive = false;
    private MetaResourceLocation hostRock = UnlistedPropertyHostRock.DEFAULT;

    public OreBlockTileEntity()
    {
        // Nothing to do
    }

    public boolean isActive()
    {
        return isActive;
    }

    public void setActive( boolean active )
    {
        if( !world.isRemote && isActive != active )
        {
            isActive = active;
            update();
        }
    }

    public MetaResourceLocation getHostRock()
    {
        return hostRock;
    }

    public void searchForAdjacentHostRock()
    {
        if( world.isRemote )
            return; // Clients should only get this property from the server

        if( !hostRock.equals( UnlistedPropertyHostRock.DEFAULT ) )
            return; // Host has been determined

        hostRock = UnlistedPropertyHostRock.findHost( world , pos );
        if( hostRock.equals( UnlistedPropertyHostRock.DEFAULT ) )
        {
            //System.out.println( "Unable to determine host" );

            // FIXME: We can't exactly "timeout". Consider an ore block that has a single,
            // adjacent stone in a neighbouring, but unloaded, chunk. Can a chunk load
            // trigger a block update? Should we remove the block and pretend like it never existed?

            world.scheduleBlockUpdate( pos , this.blockType , 20 , 10 );
            return;
        }

        update();
    }

    protected void update()
    {
        IBlockState oldState = world.getBlockState( pos );
        OreBlock oreBlock = (OreBlock)oldState.getBlock();
        IBlockState newState = oreBlock.getCompleteExtendedState( this , oldState , world , pos );
        world.setBlockState( pos , newState );

        // We must explicitly call checkLight because ExtendedBlockStorage caches IExtendedBlockState.getClean().
        // This means the tile entity (us) must be queried for getLightValue() and will return the same value for
        // both old and new states. Because there is no difference in light values, the update to world lighting
        // is skipped. Unfortunately, this also means OreBlock cannot be optimized to always use extended state
        // information and requires the tile entity to be resolved each time.
        world.profiler.startSection( "checkLight" );
        world.checkLight( pos );
        world.profiler.endSection();

        if( world.isRemote )
            markDirty();
    }

    // TileEntity overrides

    @Override
    public boolean shouldRefresh( World world , BlockPos pos , IBlockState oldState , IBlockState newSate )
    {
        // newSate? Does Forge have code reviewers?
        return oldState.getBlock() != newSate.getBlock();
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

    @Override
    public SPacketUpdateTileEntity getUpdatePacket()
    {
        // What does 3 mean here?
        return new SPacketUpdateTileEntity( this.pos , 3 , this.getUpdateTag() );
    }

    @Override
    public NBTTagCompound getUpdateTag()
    {
        NBTTagCompound tag = super.getUpdateTag();

        if( !hostRock.equals( UnlistedPropertyHostRock.DEFAULT ) )
            tag.setString( UnlistedPropertyHostRock.PROPERTY.getName() , hostRock.toString() );
        tag.setBoolean( UnlistedPropertyActiveState.PROPERTY.getName() , isActive );

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

        // The server should ignore any updates to this block from clients (if even possible)
        if( !world.isRemote )
            return;

        boolean hasUpdates = false;
        boolean hostChanged = false;

        if( tag.hasKey( UnlistedPropertyHostRock.PROPERTY.getName() ) )
        {
            String propertyString = tag.getString( UnlistedPropertyHostRock.PROPERTY.getName() );
            MetaResourceLocation host = new MetaResourceLocation( propertyString );
            if( !host.equals( hostRock ) )
            {
                hostRock = host;
                hasUpdates = true;
                hostChanged = true;
            }
        }

        if( tag.hasKey( UnlistedPropertyActiveState.PROPERTY.getName() ) )
        {
            Boolean active = tag.getBoolean( UnlistedPropertyActiveState.PROPERTY.getName() );
            if( !active && hostChanged )
            {
                // Don't interpret a scheduled host update tick as a random tick to deactivate,
                // but do allow activation if it happened at the same time.
            }
            else if( active != isActive )
            {
                isActive = active;
                hasUpdates = true;
            }
        }

        if( hasUpdates )
            update();
    }
}
