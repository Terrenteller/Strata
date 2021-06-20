package com.riintouge.strata.block.geo;

import com.riintouge.strata.Strata;
import com.riintouge.strata.block.MetaResourceLocation;
import com.riintouge.strata.misc.InitializedThreadLocal;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.EnumPlantType;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import java.util.Random;

public class GeoBlock extends Block
{
    public static InitializedThreadLocal< Boolean > canSustainPlantEventOverride = new InitializedThreadLocal<>( false );

    protected IGeoTileInfo info;

    public GeoBlock( IGeoTileInfo info )
    {
        super( info.material() );
        this.info = info;

        ResourceLocation registryName = info.registryName();
        setRegistryName( registryName );
        setUnlocalizedName( registryName.toString() );
        if( info.type().isPrimary || info.type() == TileType.COBBLE )
            setCreativeTab( Strata.BLOCK_TAB );
        else
            setCreativeTab( Strata.BUILDING_BLOCK_TAB );

        setHarvestLevel( info.harvestTool() , info.harvestLevel() );
        setSoundType( info.soundType() );
        setHardness( info.hardness() );
        setResistance( info.explosionResistance() );
    }

    // Block overrides

    @Override
    public boolean canSustainPlant( IBlockState state , IBlockAccess world , BlockPos pos , EnumFacing direction , IPlantable plantable )
    {
        // Because worldgen may swap an otherwise valid block with us (such as hardened clay with limestone in a mesa),
        // we cannot accurately determine validity here without knowing worldgen implementation details. As such,
        // allow anything so existing plants don't drop their items in the world. Prefer strange placement over litter.
        // Event handlers must enforce actual restrictions.
        if( !canSustainPlantEventOverride.get() )
            return true;
        else if( !info.type().isPrimary )
            return false;

        // FIXME: What if there is already a plant at pos which is replaceable?
        EnumPlantType plantType = plantable.getPlantType( world , pos );
        if( info.sustainedPlantTypes().contains( plantType ) )
            return true;

        for( IBlockState otherState : info.sustainsPlantsSustainedBy() )
            if( otherState.getBlock().canSustainPlant( otherState , world , pos , direction , plantable ) )
                return true;

        return false;
    }

    @Override
    public Item getItemDropped( IBlockState state , Random rand , int fortune )
    {
        try
        {
            switch( info.type() )
            {
                case STONE:
                    return Item.REGISTRY.getObject( TileType.COBBLE.registryName( info.tileSetName() ) );
                case CLAY:
                    // TODO: Drop clay globs
                default: { }
            }
        }
        catch( NullPointerException e )
        {
            // No special drop
        }

        return super.getItemDropped( state , rand , fortune );
    }
}
