package com.riintouge.strata.block;

import com.riintouge.strata.GenericTileSetRegistry;
import com.riintouge.strata.Strata;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;

import java.util.Random;

public class GenericStoneBlock extends Block
{
    public final String CobbleSuffix = "_cobble";
    public final String BrickSuffix = "_brick";

    protected IGenericStoneTileSetInfo tileSetInfo;

    public GenericStoneBlock( IGenericStoneTileSetInfo tileSetInfo , StoneBlockType blockType )
    {
        super( tileSetInfo.material() );
        this.tileSetInfo = tileSetInfo;

        setHardness( tileSetInfo.hardness() );

        String blockName = tileSetInfo.stoneName();
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

        setRegistryName( Strata.modid + ":" + blockName );
        setUnlocalizedName( Strata.modid + ":" + blockName );

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
            return tileSet.tiles.get( StoneBlockType.COBBLE ).getItem();
        }
        catch( NullPointerException e )
        {
            return super.getItemDropped( state , rand , fortune );
        }
    }
}
