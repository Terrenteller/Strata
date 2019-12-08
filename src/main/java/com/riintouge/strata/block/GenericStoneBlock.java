package com.riintouge.strata.block;

import com.riintouge.strata.GenericStoneRegistry;
import com.riintouge.strata.Strata;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
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
        super( Material.ROCK );
        this.tileSetInfo = tileSetInfo;

        String blockName = tileSetInfo.stoneName();
        switch( blockType )
        {
            case COBBLE:
                blockName += CobbleSuffix;
                break;
            case BRICK:
                blockName += BrickSuffix;
                break;
            default: { }
        }

        setRegistryName( Strata.modid + ":" + blockName );
        setUnlocalizedName( Strata.modid + ":" + blockName );

        setHarvestLevel( "pickaxe" , blockType == StoneBlockType.STONE ? tileSetInfo.stoneStrength().ordinal() : 0 );
        setHardness( 3f );
        setResistance( 5f );

        setCreativeTab( Strata.BLOCK_TAB );
    }

    // Block overrides

    @Override
    public Item getItemDropped( IBlockState state , Random rand , int fortune )
    {
        try
        {
            String stoneName = getRegistryName().getResourcePath();
            GenericStoneTileSet tileSet = GenericStoneRegistry.INSTANCE.find( stoneName );
            return tileSet.tiles.get( StoneBlockType.COBBLE ).getItem();
        }
        catch( NullPointerException e )
        {
            return super.getItemDropped( state , rand , fortune );
        }
    }
}
