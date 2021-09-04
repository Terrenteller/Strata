package com.riintouge.strata.block.geo;

import com.riintouge.strata.Strata;
import com.riintouge.strata.misc.InitializedThreadLocal;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFalling;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.item.Item;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.EnumPlantType;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Random;

public class GeoBlock extends BlockFalling
{
    public static InitializedThreadLocal< Boolean > canSustainPlantEventOverride = new InitializedThreadLocal<>( false );

    protected IGeoTileInfo info;
    protected Integer particleColour = null;

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

    public boolean canFall()
    {
        return info.type() == TileType.SAND;
    }

    // BlockFalling overrides

    @Override
    @SideOnly( Side.CLIENT )
    public int getDustColor( IBlockState state )
    {
        if( particleColour != null )
            return particleColour;

        ResourceLocation textureResourceLocation = info.modelTextureMap().get( EnumFacing.DOWN );
        TextureAtlasSprite texture = Minecraft.getMinecraft()
            .getTextureMapBlocks()
            .getTextureExtry( textureResourceLocation.toString() );

        if( texture != null )
        {
            // Use the first pixel of the smallest mipmap as the average color
            int[][] frameData = texture.getFrameTextureData( 0 );
            particleColour = frameData[ frameData.length - 1 ][ 0 ];
        }
        else
            particleColour = super.getDustColor( state );

        return particleColour;
    }

    @Override
    @SideOnly( Side.CLIENT )
    public void randomDisplayTick( IBlockState stateIn , World worldIn , BlockPos pos , Random rand )
    {
        if( canFall() )
            super.randomDisplayTick( stateIn , worldIn , pos , rand );
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
        Item item = null;

        try
        {
            item = info.type() == TileType.STONE
                ? Item.REGISTRY.getObject( TileType.COBBLE.registryName( info.tileSetName() ) )
                : Item.REGISTRY.getObject( GeoItemFragment.getResourceLocation( info ) );
        }
        catch( NullPointerException e )
        {
            // No special drop
        }

        return item != null ? item : super.getItemDropped( state , rand , fortune );
    }

    @Override
    public String getLocalizedName()
    {
        return info.localizedName();
    }

    @Override
    public void neighborChanged( IBlockState state , World worldIn , BlockPos pos , Block blockIn , BlockPos fromPos )
    {
        if( canFall() )
            super.neighborChanged( state , worldIn , pos , blockIn , fromPos );
    }

    @Override
    public void onBlockAdded( World worldIn , BlockPos pos , IBlockState state )
    {
        if( canFall() )
            super.onBlockAdded( worldIn , pos , state );
    }

    @Override
    public int quantityDropped( Random random )
    {
        return Item.REGISTRY.getObject( GeoItemFragment.getResourceLocation( info ) ) != null ? 4 : 1;
    }

    @Override
    public int tickRate( World worldIn )
    {
        // 10 is the default from Block. We can't call Block.tickRate() from here because Java
        return canFall() ? super.tickRate( worldIn ) : 10;
    }

    @Override
    public void updateTick( World worldIn , BlockPos pos , IBlockState state , Random rand )
    {
        if( canFall() )
            super.updateTick( worldIn , pos , state , rand );
    }
}
