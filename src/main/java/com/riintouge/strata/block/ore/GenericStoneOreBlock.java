package com.riintouge.strata.block.ore;

import com.riintouge.strata.GenericOreRegistry;
import com.riintouge.strata.GenericStoneRegistry;
import com.riintouge.strata.Strata;
import com.riintouge.strata.block.DynamicOreHostTileEntity;
import com.riintouge.strata.block.GenericBlockItemPair;
import com.riintouge.strata.block.GenericStoneTileSet;
import com.riintouge.strata.block.StoneBlockType;
import com.riintouge.strata.property.UnlistedPropertyHostRock;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
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

public class GenericStoneOreBlock extends Block
{
    protected IOreInfo oreInfo;

    public GenericStoneOreBlock( IOreInfo oreInfo )
    {
        super( Material.ROCK );
        this.oreInfo = oreInfo;

        setRegistryName( Strata.modid + ":" + oreInfo.oreName() );
        setUnlocalizedName( Strata.modid + ":" + oreInfo.oreName() );

        setHarvestLevel( "pickaxe" , oreInfo.stoneStrength().ordinal() );
        setHardness( 3f );
        setResistance( 5f );

        setCreativeTab( Strata.BLOCK_TAB );
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
    public void getDrops( NonNullList<ItemStack> drops , IBlockAccess world , BlockPos pos , IBlockState state , int fortune )
    {
        TileEntity tileEntity = world.getTileEntity( pos );
        if( !( tileEntity instanceof DynamicOreHostTileEntity ) )
            return;

        DynamicOreHostTileEntity oreTileEntity = (DynamicOreHostTileEntity)tileEntity;
        GenericStoneTileSet hostTileSet = GenericStoneRegistry.INSTANCE.find( oreTileEntity.getCachedHost() );
        if( hostTileSet != null )
        {
            GenericBlockItemPair hostCobble = hostTileSet.tiles.getOrDefault( StoneBlockType.COBBLE , null );
            if( hostCobble != null )
                drops.add( new ItemStack( hostCobble.getBlock().getItemDropped( state , RANDOM , fortune ) , 1 ) );
        }

        GenericOreTileSet oreTileSet = GenericOreRegistry.INSTANCE.find( oreInfo.oreName() );
        int fortuneBonus = fortune > 0 ? RANDOM.nextInt( fortune + 1 ) : 0;
        drops.add( new ItemStack( oreTileSet.blockItem , 1 + fortuneBonus ) );
    }

    @Override
    public Item getItemDropped( IBlockState state , Random rand , int fortune )
    {
        return net.minecraft.init.Items.AIR;
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
