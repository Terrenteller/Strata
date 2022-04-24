package com.riintouge.strata.block.ore;

import com.riintouge.strata.block.MetaResourceLocation;
import com.riintouge.strata.block.geo.GeoBlock;
import com.riintouge.strata.block.geo.HostRegistry;
import com.riintouge.strata.block.geo.IHostInfo;
import com.riintouge.strata.util.StateUtil;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.property.IExtendedBlockState;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OreBlockTileEntity extends TileEntity
{
    // HACK: OreBlockModel.getQuads() returns the host's quads in addition to the ore's quads. This is intentional.
    // When ForgeHooks.getDamageModel() collects quads to build a separate model on-the-fly
    // using the damage state texture for BlockRenderDispatcher.renderBlockDamage() to render,
    // we end up with multiple damage textures per face which overlap awkwardly.
    // The solution here is to only return the ore's quads from the upcoming call to OreBlockModel.getQuads().
    public static ThreadLocal< Integer > DAMAGE_MODEL_FACE_COUNT_HACK = ThreadLocal.withInitial( () -> 0 );

    private boolean updateOnLoad = false;
    private MetaResourceLocation hostRock = UnlistedPropertyHostRock.DEFAULT;
    private Boolean isActive = UnlistedPropertyActiveState.DEFAULT;

    public OreBlockTileEntity()
    {
        // Nothing to do, but required
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

    @Nonnull
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

        hostRock = findHost( world , pos );
        if( hostRock.equals( UnlistedPropertyHostRock.DEFAULT ) )
            return; // No host found. Wait for something to update us or try again later.

        update();
    }

    @Nonnull
    public MetaResourceLocation findHost( IBlockAccess worldIn , BlockPos pos )
    {
        IBlockState state = world.getBlockState( pos );
        OreBlock oreBlock = (OreBlock)state.getBlock();
        MetaResourceLocation forcedHost = oreBlock.getOreInfo().forcedHost();
        if( forcedHost != null && HostRegistry.INSTANCE.find( forcedHost ) != null )
            return forcedHost;

        Map< MetaResourceLocation , Float > hostWeight = new HashMap<>();
        List< MetaResourceLocation > hostAffinities = oreBlock.getOreInfo().hostAffinities();
        int mostAffinitizedHostIndex = -1;

        for( EnumFacing facing : EnumFacing.VALUES )
        {
            MetaResourceLocation host = getAdjacentHost( worldIn , pos , facing );
            IHostInfo hostInfo = host != null ? HostRegistry.INSTANCE.find( host ) : null;
            if( hostInfo == null )
                continue;

            if( hostAffinities != null )
            {
                int affinityIndex = hostAffinities.indexOf( host );
                if( affinityIndex >= 0 && ( affinityIndex < mostAffinitizedHostIndex || mostAffinitizedHostIndex == -1 ) )
                    mostAffinitizedHostIndex = affinityIndex; 
            }

            float weight = 1;

            if( host.equals( UnlistedPropertyHostRock.DEFAULT ) )
            {
                // Never prioritize the default (which should really be null)
                weight = 0;
            }
            else
            {
                // Give a bonus to horizontal facings since most rock is found in layers
                weight *= facing.getAxis().isHorizontal() ? 2 : 1;

                // Give a bonus for hosts which match the ore's material
                weight *= hostInfo.material() == oreBlock.oreInfo.material() ? 2.0f : 0.5f;
            }

            hostWeight.put( host , hostWeight.getOrDefault( host , 0.0f ) + weight );
        }
        
        if( mostAffinitizedHostIndex != -1 )
        {
            MetaResourceLocation mostAffinitizedHost = hostAffinities.get( mostAffinitizedHostIndex );
            if( HostRegistry.INSTANCE.find( mostAffinitizedHost ) != null )
                return mostAffinitizedHost;
        }

        Map.Entry< MetaResourceLocation , Float > bestEntry = null;
        for( Map.Entry< MetaResourceLocation , Float > entry : hostWeight.entrySet() )
            if( bestEntry == null || entry.getValue() > bestEntry.getValue() )
                if( HostRegistry.INSTANCE.find( entry.getKey() ) != null )
                    bestEntry = entry;

        return bestEntry != null ? bestEntry.getKey() : UnlistedPropertyHostRock.DEFAULT;
    }

    @Nullable
    public MetaResourceLocation getAdjacentHost( IBlockAccess worldIn , BlockPos pos , EnumFacing facing )
    {
        BlockPos adjPos = pos.offset( facing );
        IBlockState adjState = worldIn.getBlockState( adjPos );
        Block adjBlock = adjState.getBlock();
        ResourceLocation adjRegistryName = adjBlock.getRegistryName();
        // Strata uses the meta for direction and rotation, not a unique block variant
        int adjMeta = adjBlock instanceof GeoBlock ? 0 : adjBlock.getMetaFromState( adjState );

        return HostRegistry.INSTANCE.find( adjRegistryName , adjMeta ) != null
            ? new MetaResourceLocation( adjRegistryName , adjMeta )
            : StateUtil.getValue( adjState , worldIn , adjPos , UnlistedPropertyHostRock.PROPERTY , null );
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
    public boolean canRenderBreaking()
    {
        // We only know when this starts, so we must count instead of using a toggle.
        // OreBlockModel.getQuads() is called once for each face with no culling interference.
        DAMAGE_MODEL_FACE_COUNT_HACK.set( EnumFacing.VALUES.length );

        return false;
    }

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
                // The client may not know about the host as a host, but to get this far the client must know
                // about the block. The game would have hung in a nasty way otherwise (at least with 1.12.2).
                // Strata is visually functional in this state, but breaking blocks will likely cause de-syncs.

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
