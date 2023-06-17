package com.riintouge.strata.network;

import com.riintouge.strata.Strata;
import com.riintouge.strata.StrataConfig;
import com.riintouge.strata.block.host.HostRegistry;
import com.riintouge.strata.block.ICommonBlockProperties;
import com.riintouge.strata.block.host.IHostInfo;
import com.riintouge.strata.misc.MetaResourceLocation;
import com.riintouge.strata.block.SpecialBlockPropertyFlags;
import com.riintouge.strata.block.geo.*;
import com.riintouge.strata.block.ore.IOreInfo;
import com.riintouge.strata.block.ore.IOreTileSet;
import com.riintouge.strata.block.ore.OreRegistry;
import com.riintouge.strata.util.EnumUtil;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.*;

public final class BlockPropertiesResponseMessage extends ZipMessage
{
    private final long synchronizedBlockPropertyFlagsMask = SpecialBlockPropertyFlags.ACTIVATABLE;
    private int mismatches = 0;
    private String firstMismatch = null;

    public BlockPropertiesResponseMessage()
    {
        // Nothing to do, but required
    }

    private void writeBlockProperties( ICommonBlockProperties properties , DataOutputStream stream ) throws IOException
    {
        // We can't serialize the material. Try our best for the important parts.
        stream.writeBoolean( properties.material().isReplaceable() );
        stream.writeBoolean( properties.material().isToolNotRequired() );
        stream.writeUTF( properties.harvestTool() );
        stream.writeInt( properties.harvestLevel() );
        stream.writeFloat( properties.hardness() );
        stream.writeFloat( properties.explosionResistance() );
        stream.writeLong( properties.specialBlockPropertyFlags() & synchronizedBlockPropertyFlagsMask );

        if( properties instanceof IOreInfo )
        {
            IOreInfo oreInfo = (IOreInfo)properties;
            String proxyBlockRegistryName = "";
            IBlockState proxyBlockState = oreInfo.proxyBlockState();
            if( proxyBlockState != null )
            {
                MetaResourceLocation proxyBlockMetaResource = new MetaResourceLocation( proxyBlockState );
                proxyBlockRegistryName = proxyBlockMetaResource.toString();
            }
            stream.writeUTF( proxyBlockRegistryName );
        }
    }

    private boolean readThenCompareCommonBlockProperties( ICommonBlockProperties properties , DataInputStream stream ) throws IOException
    {
        boolean isReplaceable = stream.readBoolean();
        boolean toolNotRequired = stream.readBoolean();
        String harvestTool = stream.readUTF();
        int harvestLevel = stream.readInt();
        float hardness = stream.readFloat();
        float explosionResistance = stream.readFloat();
        long specialBlockPropertyFlags = stream.readLong();

        if( properties == null )
            return false;

        boolean equivalent = true;
        equivalent &= isReplaceable == properties.material().isReplaceable();
        equivalent &= toolNotRequired == properties.material().isToolNotRequired();
        equivalent &= harvestTool.equalsIgnoreCase( properties.harvestTool() );
        equivalent &= harvestLevel == properties.harvestLevel();
        equivalent &= hardness == properties.hardness();
        equivalent &= explosionResistance == properties.explosionResistance();
        equivalent &= specialBlockPropertyFlags == ( properties.specialBlockPropertyFlags() & synchronizedBlockPropertyFlagsMask );

        return equivalent;
    }

    private boolean readThenCompareCommonBlockPropertiesWithOre( IOreInfo properties , DataInputStream stream ) throws IOException
    {
        boolean isReplaceable = stream.readBoolean();
        boolean toolNotRequired = stream.readBoolean();
        String harvestTool = stream.readUTF();
        int harvestLevel = stream.readInt();
        float hardness = stream.readFloat();
        float explosionResistance = stream.readFloat();
        long specialBlockPropertyFlags = stream.readLong();
        String proxyBlockRegistryName = stream.readUTF();

        if( properties == null )
            return false;

        String ourProxyBlockRegistryName = "";
        IBlockState proxyBlockState = properties.proxyBlockState();
        if( proxyBlockState != null )
        {
            MetaResourceLocation proxyBlockMetaResource = new MetaResourceLocation( proxyBlockState );
            ourProxyBlockRegistryName = proxyBlockMetaResource.toString();
        }

        boolean equivalent = true;
        equivalent &= isReplaceable == properties.material().isReplaceable();
        equivalent &= toolNotRequired == properties.material().isToolNotRequired();
        equivalent &= specialBlockPropertyFlags == ( properties.specialBlockPropertyFlags() & synchronizedBlockPropertyFlagsMask );

        if( !proxyBlockRegistryName.isEmpty() && proxyBlockRegistryName.compareToIgnoreCase( ourProxyBlockRegistryName ) == 0 )
        {
            // Both sides agree the ore block is a proxy for the same thing.
            // Skip checking properties we'd defer to the target.
        }
        else
        {
            equivalent &= harvestTool.equalsIgnoreCase( properties.harvestTool() );
            equivalent &= harvestLevel == properties.harvestLevel();
            equivalent &= hardness == properties.hardness();
            equivalent &= explosionResistance == properties.explosionResistance();
        }

        return equivalent;
    }

    private void logMismatch( String registryName )
    {
        mismatches++;

        if( firstMismatch == null )
            firstMismatch = registryName;
    }

    // IMessage overrides

    @Override
    public void toBytes( DataOutputStream stream ) throws IOException
    {
        // Hosts
        {
            Set< Pair< MetaResourceLocation , IHostInfo > > hostInfos = new HashSet<>();
            for( Map.Entry< ResourceLocation , IHostInfo[] > entry : HostRegistry.INSTANCE.allHosts().entrySet() )
                for( int index = 0 ; index < entry.getValue().length ; index++ )
                    if( entry.getValue()[ index ] != null )
                        hostInfos.add( new ImmutablePair<>(
                            new MetaResourceLocation( entry.getKey() , index ),
                            entry.getValue()[ index ] ) );

            stream.writeInt( hostInfos.size() );
            for( Pair< MetaResourceLocation , IHostInfo > hostInfoPair : hostInfos )
            {
                IHostInfo hostInfo = hostInfoPair.getValue();
                stream.writeUTF( hostInfoPair.getKey().toString() );
                writeBlockProperties( hostInfo , stream );
            }
        }

        // Ores
        {
            Collection< IOreTileSet > oreTileSets = OreRegistry.INSTANCE.all();
            stream.writeInt( oreTileSets.size() );

            for( IOreTileSet oreTileSet : oreTileSets )
            {
                IOreInfo oreInfo = oreTileSet.getInfo();
                stream.writeUTF( oreInfo.oreName() );
                writeBlockProperties( oreInfo , stream );
            }
        }

        // Tile sets
        {
            Set< String > tileSetNames = GeoTileSetRegistry.INSTANCE.tileSetNames();
            stream.writeInt( tileSetNames.size() );

            for( String tileSetName : tileSetNames )
            {
                stream.writeUTF( tileSetName );

                List< IGeoTileInfo > tileInfos = new ArrayList<>();
                IGeoTileSet geoTileSet = GeoTileSetRegistry.INSTANCE.find( tileSetName );

                if( geoTileSet != null )
                {
                    for( TileType tileType : TileType.values() )
                    {
                        IGeoTileInfo geoTileInfo = geoTileSet.getInfo( tileType );
                        if( geoTileInfo != null )
                            tileInfos.add( geoTileInfo );
                    }
                }

                stream.writeInt( tileInfos.size() );

                for( IGeoTileInfo tileInfo : tileInfos )
                {
                    stream.writeUTF( tileInfo.tileType().toString() );
                    writeBlockProperties( tileInfo , stream );
                }
            }
        }
    }

    @Override
    public void fromBytes( DataInputStream stream ) throws IOException
    {
        // Hosts
        {
            int hostCount = stream.readInt();
            for( int index = 0 ; index < hostCount ; index++ )
            {
                MetaResourceLocation metaResource = new MetaResourceLocation( stream.readUTF() );
                IHostInfo hostInfo = HostRegistry.INSTANCE.find( metaResource );
                boolean equivalent = readThenCompareCommonBlockProperties( hostInfo , stream );
                if( hostInfo != null && !equivalent )
                    logMismatch( metaResource.toString() );
            }
        }

        // Ores
        {
            int oreCount = stream.readInt();
            for( int index = 0 ; index < oreCount ; index++ )
            {
                String oreName = stream.readUTF();
                IOreTileSet oreTileSet = OreRegistry.INSTANCE.find( oreName );
                IOreInfo oreInfo = oreTileSet != null ? oreTileSet.getInfo() : null;
                boolean equivalent = readThenCompareCommonBlockPropertiesWithOre( oreInfo , stream );
                if( oreInfo != null && !equivalent )
                {
                    // Use the item because the block has a special registry name suffix
                    logMismatch( oreTileSet.getItem().getRegistryName().toString() );
                }
            }
        }

        // Tile sets
        {
            int tileSetNameCount = stream.readInt();
            for( int tileSetIndex = 0 ; tileSetIndex < tileSetNameCount ; tileSetIndex++ )
            {
                String tileSetName = stream.readUTF();
                int tileInfoCount = stream.readInt();

                for( int tileInfoIndex = 0 ; tileInfoIndex < tileInfoCount ; tileInfoIndex++ )
                {
                    String tileTypeName = stream.readUTF();
                    TileType tileType = EnumUtil.valueOfOrNull( TileType.class , tileTypeName );
                    IGeoTileInfo tileInfo = tileType != null
                        ? GeoTileSetRegistry.INSTANCE.findTileInfo( tileSetName , tileType )
                        : null;
                    boolean equivalent = readThenCompareCommonBlockProperties( tileInfo , stream );
                    if( tileInfo != null && !equivalent )
                        logMismatch( tileType.registryName( tileSetName ).toString() );
                }
            }
        }
    }

    // Nested classes

    public static final class Handler extends ObservableMessageHandler< BlockPropertiesResponseMessage , IMessage >
    {
        public static final Handler INSTANCE = new Handler();

        private Handler()
        {
            // Nothing to do
        }

        // ObservableMessageHandler overrides

        @Override
        public IMessage onMessage( BlockPropertiesResponseMessage message , MessageContext ctx )
        {
            Strata.LOGGER.trace( "BlockPropertiesResponseMessage::Handler::onMessage()" );

            if( message.caughtException != null )
            {
                notifyObservers( message , ctx , message.caughtException );
                return null;
            }
            else if( message.mismatches == 0 )
            {
                notifyObservers( message , ctx , true );
                return null;
            }

            // Why does TextComponentTranslation not localize here? Is it because we're server-side?
            TextComponentString text = new TextComponentString(
                String.format(
                    net.minecraft.util.text.translation.I18n.translateToLocal(
                        StrataConfig.enforceClientSynchronization
                            ? "strata.multiplayer.disconnect.unsynchronizedProperties"
                            : "strata.multiplayer.warning.unsynchronizedProperties" ),
                    message.firstMismatch,
                    message.mismatches - 1 ) );

            if( StrataConfig.enforceClientSynchronization )
                ctx.getServerHandler().player.connection.disconnect( text );
            else
                ctx.getServerHandler().player.sendMessage( text );

            notifyObservers( message , ctx , !StrataConfig.enforceClientSynchronization );
            return null;
        }
    }
}
