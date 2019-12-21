package com.riintouge.strata.block.geo.tileset;

import com.riintouge.strata.block.GenericTileSetRegistry;
import com.riintouge.strata.Strata;
import com.riintouge.strata.block.geo.TileType;
import com.riintouge.strata.block.geo.info.IGenericStoneTileSetInfo;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;

import java.util.Random;

public class GenericStoneBlock extends Block
{
    public final String CobbleSuffix = "_cobble";
    public final String BrickSuffix = "_brick";

    protected IGenericStoneTileSetInfo tileSetInfo;

    public GenericStoneBlock( IGenericStoneTileSetInfo tileSetInfo , TileType blockType )
    {
        super( tileSetInfo.material() );
        this.tileSetInfo = tileSetInfo;

        setHardness( tileSetInfo.hardness() );

        ResourceLocation registryName = tileSetInfo.registryName();
        String blockName = registryName.getResourcePath();
        switch( blockType )
        {
            case COBBLE:
                blockName += CobbleSuffix;
                setHardness( tileSetInfo.hardness() + 0.5f );
                break;
            case BRICK:
                blockName += BrickSuffix;
                break;
            default: { }
        }

        ResourceLocation targetRegistryName = new ResourceLocation( registryName.getResourceDomain() , blockName );
        setRegistryName( targetRegistryName );
        setUnlocalizedName( targetRegistryName.toString() );

        setHarvestLevel( tileSetInfo.harvestTool() , tileSetInfo.harvestLevel() );
        setSoundType( tileSetInfo.soundType() );
        setResistance( tileSetInfo.explosionResistance() );

        setCreativeTab( Strata.BLOCK_TAB );
    }

    // Block overrides

    @Override
    public Item getItemDropped( IBlockState state , Random rand , int fortune )
    {
        try
        {
            String tileSetName = getRegistryName().getResourcePath();
            GenericStoneTileSet tileSet = GenericTileSetRegistry.INSTANCE.find( tileSetName , GenericStoneTileSet.class );
            return tileSet.tiles.get( TileType.COBBLE ).getItem();
        }
        catch( NullPointerException e )
        {
            return super.getItemDropped( state , rand , fortune );
        }
    }
}
