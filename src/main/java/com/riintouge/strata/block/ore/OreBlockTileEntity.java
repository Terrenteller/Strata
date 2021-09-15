package com.riintouge.strata.block.ore;

import com.riintouge.strata.block.MetaResourceLocation;
import com.riintouge.strata.block.geo.HostRegistry;
import com.riintouge.strata.util.StateUtil;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.property.IExtendedBlockState;

public class OreBlockTileEntity extends TileEntity
{
    private boolean updateOnLoad = false;
    private MetaResourceLocation hostRock = UnlistedPropertyHostRock.DEFAULT;
    private Boolean isActive = UnlistedPropertyActiveState.DEFAULT;

    public OreBlockTileEntity()
    {
        // Nothing to do
    }

    public OreBlockTileEntity( IBlockState state )
    {
        if( state == null )
            return;

        hostRock = StateUtil.getValue( state , UnlistedPropertyHostRock.PROPERTY );
        if( hostRock == null )
        {
            hostRock = UnlistedPropertyHostRock.DEFAULT;
            updateOnLoad = true;
            return;
        }

        isActive = StateUtil.getValue( state , UnlistedPropertyActiveState.PROPERTY , UnlistedPropertyActiveState.DEFAULT );
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

    public void setHostRock( MetaResourceLocation hostMetaLocation )
    {
        if( hostMetaLocation != null && !world.isRemote && !hostRock.equals( hostMetaLocation ) )
        {
            hostRock = hostMetaLocation;
            update();
        }
    }

    public void searchForAdjacentHostRock()
    {
        if( world.isRemote )
            return; // Clients should only get this property from the server

        if( !hostRock.equals( UnlistedPropertyHostRock.DEFAULT ) )
            return; // Host has been determined

        hostRock = UnlistedPropertyHostRock.findHost( world , pos );
        if( hostRock.equals( UnlistedPropertyHostRock.DEFAULT ) )
            return; // No host found. Wait for something to update us or try again later.

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
        // both old and new states. Because there is no difference in values, the lighting update is skipped.
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
        return !( newSate instanceof IExtendedBlockState )
            || !hostRock.equals( StateUtil.getValue( newSate , UnlistedPropertyHostRock.PROPERTY , UnlistedPropertyHostRock.DEFAULT ) )
            || isActive != StateUtil.getValue( newSate , UnlistedPropertyActiveState.PROPERTY , false );

    }

    @Override
    public void onLoad()
    {
        super.onLoad();

        // Only the server should poll
        if( !world.isRemote && updateOnLoad )
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
                if( HostRegistry.INSTANCE.find( hostRock ) == null )
                {
                    // The client must have known about the block to get this far, but not that it was a host.
                    // Visual discrepancies aside, the ore will not know the real properties bestowed upon it.
                    throw new IllegalStateException( String.format( "Server reported unknown host '%s'" , host.toString() ) );
                }

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

    @Override
    public void readFromNBT( NBTTagCompound compound )
    {
        super.readFromNBT( compound );

        try
        {
            if( !compound.hasKey( UnlistedPropertyHostRock.PROPERTY.getName() ) )
            {
                updateOnLoad = true;
                return;
            }

            hostRock = new MetaResourceLocation( compound.getString( UnlistedPropertyHostRock.PROPERTY.getName() ) );
            if( HostRegistry.INSTANCE.find( hostRock ) == null )
            {
                hostRock = UnlistedPropertyHostRock.DEFAULT;
                updateOnLoad = true;
                return;
            }

            isActive = compound.getBoolean( UnlistedPropertyActiveState.PROPERTY.getName() );

        }
        catch( Exception e )
        {
            updateOnLoad = true;
        }
    }

    @Override
    public NBTTagCompound writeToNBT( NBTTagCompound compound )
    {
        super.writeToNBT( compound );

        compound.setString( UnlistedPropertyHostRock.PROPERTY.getName() , hostRock.toString() );
        compound.setBoolean( UnlistedPropertyActiveState.PROPERTY.getName() , isActive );

        return compound;
    }
}
