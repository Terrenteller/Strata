package com.riintouge.strata.block.ore;

import com.riintouge.strata.Strata;
import com.riintouge.strata.misc.MetaResourceLocation;
import com.riintouge.strata.block.geo.GeoBlock;
import com.riintouge.strata.block.host.HostRegistry;
import com.riintouge.strata.block.host.IHostInfo;
import com.riintouge.strata.util.FlagUtil;
import com.riintouge.strata.util.StateUtil;
import com.riintouge.strata.util.StringUtil;
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

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OreBlockTileEntity extends TileEntity
{
    public static ResourceLocation REGISTRY_NAME = Strata.resource( "ore_tile_entity" );
    // HACK: OreBlockModel.getQuads() returns the host's quads in addition to the ore's quads. This is intentional.
    // When ForgeHooks.getDamageModel() collects quads to build a separate model on-the-fly
    // using the damage state texture for BlockRenderDispatcher.renderBlockDamage() to render,
    // we end up with two damage textures per facing (one for each model) which overlap additively or awkwardly.
    // The solution here is to only return the ore's quads from the upcoming call to OreBlockModel.getQuads().
    private static final ThreadLocal< Integer > DAMAGE_MODEL_FACE_COUNT_HACK = ThreadLocal.withInitial( () -> 0 );

    private boolean updateOnLoad = false;
    private MetaResourceLocation hostRock = null;
    private byte flags = UnlistedPropertyOreFlags.DEFAULT;

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
            updateOnLoad = true;
            return;
        }

        flags = StateUtil.getValue( state , UnlistedPropertyOreFlags.PROPERTY , UnlistedPropertyOreFlags.DEFAULT );
    }

    public byte getFlags()
    {
        return flags;
    }

    public boolean isActive()
    {
        return FlagUtil.check( flags , UnlistedPropertyOreFlags.ACTIVE );
    }

    public void setActive( boolean active )
    {
        if( !world.isRemote && active != isActive() )
        {
            flags = FlagUtil.set( flags , UnlistedPropertyOreFlags.ACTIVE , active );
            update();
        }
    }

    @Nullable
    public MetaResourceLocation getHostRock()
    {
        return hostRock;
    }

    public void updateHostRock()
    {
        if( world.isRemote || hostRock != null )
            return;

        hostRock = findHost( world , pos );
        if( hostRock != null )
            update();
    }

    @Nullable
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
            IHostInfo hostInfo = HostRegistry.INSTANCE.find( host );
            if( hostInfo == null )
                continue;

            if( hostAffinities != null )
            {
                int affinityIndex = hostAffinities.indexOf( host );
                if( affinityIndex >= 0 && ( affinityIndex < mostAffinitizedHostIndex || mostAffinitizedHostIndex == -1 ) )
                    mostAffinitizedHostIndex = affinityIndex; 
            }

            // Give a bonus to horizontal facings since most rock is found in layers
            float weight = facing.getAxis().isHorizontal() ? 2.0f : 1.0f;
            // Give a bonus for hosts which match the ore's material
            weight *= hostInfo.material() == oreBlock.oreInfo.material() ? 2.0f : 0.5f;

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

        return bestEntry != null ? bestEntry.getKey() : null;
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
        // newSate? Really?
        if( !( newSate instanceof IExtendedBlockState ) )
            return true;

        MetaResourceLocation newHostRock = StateUtil.getValue( newSate , UnlistedPropertyHostRock.PROPERTY );
        if( newHostRock != null && !newHostRock.equals( hostRock ) )
            return true;

        return flags != StateUtil.getValue( newSate , UnlistedPropertyOreFlags.PROPERTY , UnlistedPropertyOreFlags.DEFAULT );
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
        return new SPacketUpdateTileEntity( this.pos , 0 , this.getUpdateTag() );
    }

    @Override
    public NBTTagCompound getUpdateTag()
    {
        NBTTagCompound tag = super.getUpdateTag();

        tag.setString( UnlistedPropertyHostRock.PROPERTY.getName() , hostRock != null ? hostRock.toString() : "" );
        tag.setByte( UnlistedPropertyOreFlags.PROPERTY.getName() , flags );

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
            String rawHostRock = tag.getString( UnlistedPropertyHostRock.PROPERTY.getName() );
            MetaResourceLocation tagHostRock = !rawHostRock.isEmpty() ? new MetaResourceLocation( rawHostRock ) : null;
            if( tagHostRock != null && !tagHostRock.equals( hostRock ) )
            {
                // The client may not know about the host as a host, but to get this far the client must know
                // about the block. The game would have hung in a nasty way otherwise (at least with 1.12.2).
                // Strata is visually functional in this state, but breaking blocks will likely cause de-syncs.

                hostRock = tagHostRock;
                hasUpdates = true;
                hostChanged = true;
            }
        }

        if( tag.hasKey( UnlistedPropertyOreFlags.PROPERTY.getName() ) )
        {
            byte tagFlags = tag.getByte( UnlistedPropertyOreFlags.PROPERTY.getName() );
            boolean active = FlagUtil.check( tagFlags , UnlistedPropertyOreFlags.ACTIVE );

            if( !active && hostChanged )
            {
                // Don't interpret a scheduled host update tick as a random tick to deactivate,
                // but do allow activation if it happened at the same time.
            }
            else if( active != isActive() )
            {
                flags = FlagUtil.set( flags , UnlistedPropertyOreFlags.ACTIVE , active );
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
            String rawHostRock = compound.getString( UnlistedPropertyHostRock.PROPERTY.getName() );
            if( StringUtil.isNullOrEmpty( rawHostRock ) )
            {
                updateOnLoad = true;
                return;
            }

            hostRock = new MetaResourceLocation( rawHostRock );
            if( HostRegistry.INSTANCE.find( hostRock ) == null )
            {
                hostRock = null;
                updateOnLoad = true;
                return;
            }

            flags = compound.getByte( UnlistedPropertyOreFlags.PROPERTY.getName() );
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

        compound.setString( UnlistedPropertyHostRock.PROPERTY.getName() , hostRock != null ? hostRock.toString() : "" );
        compound.setByte( UnlistedPropertyOreFlags.PROPERTY.getName() , flags );

        return compound;
    }

    // Statics

    public static boolean getQuadsOnlyForDamageModel()
    {
        int damageModelFaceCountHackValue = DAMAGE_MODEL_FACE_COUNT_HACK.get();
        if( damageModelFaceCountHackValue > 0 )
        {
            DAMAGE_MODEL_FACE_COUNT_HACK.set( damageModelFaceCountHackValue - 1 );
            return true;
        }

        return false;
    }
}
