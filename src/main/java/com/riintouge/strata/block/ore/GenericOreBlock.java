package com.riintouge.strata.block.ore;

import com.riintouge.strata.GenericOreRegistry;
import com.riintouge.strata.GenericTileSetRegistry;
import com.riintouge.strata.Strata;
import com.riintouge.strata.block.*;
import com.riintouge.strata.property.UnlistedPropertyHostRock;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.property.IExtendedBlockState;

import javax.annotation.Nullable;
import java.util.Random;

import static com.riintouge.strata.property.UnlistedPropertyHostRock.DEFAULT;

public class GenericOreBlock extends Block
{
    protected IOreInfo oreInfo;

    public GenericOreBlock( IOreInfo oreInfo )
    {
        super( oreInfo.material() );
        this.oreInfo = oreInfo;

        // TODO: Do NOT set the registry name here because it cannot then be set by derivations or new instances!
        setRegistryName( Strata.modid + ":" + oreInfo.oreName() );
        setUnlocalizedName( Strata.modid + ":" + oreInfo.oreName() );

        setHarvestLevel( oreInfo.harvestTool() , oreInfo.harvestLevel() );
        setSoundType( oreInfo.soundType() );
        setHardness( 3f );
        setResistance( 5f );

        setCreativeTab( Strata.ITEM_TAB );
    }

    // Block overrides

    @Override
    protected boolean canSilkHarvest()
    {
        return false;
    }

    @Override
    public boolean canSilkHarvest( World world , BlockPos pos , IBlockState state , EntityPlayer player )
    {
        return false;
    }

    @Override
    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer.Builder( this )
            .add( UnlistedPropertyHostRock.PROPERTY )
            .build();
    }

    @Override
    public TileEntity createTileEntity( World world , IBlockState state )
    {
        return new DynamicOreHostTileEntity();
    }

    @Deprecated
    public float getBlockHardness( IBlockState blockState , World worldIn , BlockPos pos )
    {
        return super.getBlockHardness( blockState , worldIn , pos );
    }

    @Override
    public void getDrops( NonNullList<ItemStack> drops , IBlockAccess world , BlockPos pos , IBlockState state , int fortune )
    {
        String hostRock = StateUtil.getValue( state , world , pos , UnlistedPropertyHostRock.PROPERTY , DEFAULT );
        IGenericTileSet hostTileSet = GenericTileSetRegistry.INSTANCE.find( hostRock );
        if( hostTileSet instanceof GenericClayTileSet )
        {
            GenericClayTileSet clayTileSet = (GenericClayTileSet)hostTileSet;
            clayTileSet.getClayBlock().getDrops( drops , world , pos , state , fortune );
        }
        else if( hostTileSet instanceof GenericStoneTileSet )
        {
            GenericStoneTileSet stoneTileSet = (GenericStoneTileSet)hostTileSet;
            GenericBlockItemPair hostCobble = stoneTileSet.tiles.getOrDefault( StoneBlockType.COBBLE , null );
            if( hostCobble != null )
                drops.add( new ItemStack( hostCobble.getBlock().getItemDropped( state , RANDOM , fortune ) , 1 ) );
        }

        IOreTileSet oreTileSet = GenericOreRegistry.INSTANCE.find( oreInfo.oreName() );
        IOreInfo oreInfo = oreTileSet.getInfo();
        if( oreInfo instanceof IProxyOreInfo )
        {
            ( (IProxyOreInfo)oreInfo ).getProxyBlock().getDrops( drops , world , pos , state , fortune );
        }
        else
        {
            int fortuneBonus = fortune > 0 ? RANDOM.nextInt( fortune + 1 ) : 0;
            drops.add( new ItemStack( oreTileSet.getItem() , 1 + fortuneBonus ) );
        }
    }

    @Override
    public int getExpDrop( IBlockState state , IBlockAccess world , BlockPos pos , int fortune )
    {
        return oreInfo instanceof IProxyOreInfo
            ? ( (IProxyOreInfo)oreInfo ).getProxyBlock().getExpDrop( state , world , pos , fortune )
            : 0;
    }

    @Override
    public IBlockState getExtendedState( IBlockState state , IBlockAccess world , BlockPos pos )
    {
        IExtendedBlockState extendedState = (IExtendedBlockState)state;
        TileEntity entity = world.getTileEntity( pos );
        if( entity instanceof DynamicOreHostTileEntity )
        {
            String cachedHost = ( (DynamicOreHostTileEntity)entity ).getCachedHost();
            extendedState = extendedState.withProperty( UnlistedPropertyHostRock.PROPERTY , cachedHost );
        }

        return extendedState;
    }

    @Override
    public Item getItemDropped( IBlockState state , Random rand , int fortune )
    {
        return net.minecraft.init.Items.AIR;
    }

    @Deprecated
    public Material getMaterial( IBlockState state)
    {
        return super.getMaterial( state );
    }

    @Override
    public SoundType getSoundType( IBlockState state , World world , BlockPos pos , @Nullable Entity entity )
    {
        return super.getSoundType( state , world , pos , entity );
    }

    @Override
    public void harvestBlock( World world, EntityPlayer player , BlockPos pos , IBlockState state , @Nullable TileEntity entity , ItemStack tool )
    {
        super.harvestBlock( world , player , pos , state , entity , tool );

        world.setBlockToAir( pos );
    }

    @Override
    public boolean hasTileEntity( IBlockState state )
    {
        return true;
    }

    @Override
    public boolean removedByPlayer( IBlockState state , World world , BlockPos pos , EntityPlayer player , boolean willHarvest )
    {
        // See BlockFlowerPot for details about this logic.
        // tldr: If it will harvest, delay deletion of the block until after harvestBlock.
        // BlockFlowerPot says getDrops, but that does not appear to be called.
        return willHarvest || super.removedByPlayer( state , world , pos , player , willHarvest );
    }

    @Override
    public void updateTick( World world , BlockPos pos , IBlockState state , Random rand )
    {
        super.updateTick( world , pos , state , rand );

        // Only the server should poll
        if( !world.isRemote )
            ( (DynamicOreHostTileEntity)world.getTileEntity( pos ) ).pollHost();
    }
}
