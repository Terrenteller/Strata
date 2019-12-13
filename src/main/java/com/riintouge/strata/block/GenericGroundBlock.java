package com.riintouge.strata.block;

import com.riintouge.strata.Strata;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;

public class GenericGroundBlock extends Block
{
    protected IGenericTileSetInfo tileSetInfo;

    public GenericGroundBlock( IGenericTileSetInfo tileSetInfo )
    {
        super( Material.GROUND );
        this.tileSetInfo = tileSetInfo;

        String blockName = tileSetInfo.stoneName();
        setRegistryName( Strata.modid + ":" + blockName );
        setUnlocalizedName( Strata.modid + ":" + blockName );

        setHarvestLevel( "shovel" , tileSetInfo.stoneStrength().ordinal() );
        setSoundType( SoundType.GROUND );
        setHardness( 3f );
        setResistance( 5f );

        setCreativeTab( Strata.BLOCK_TAB );
    }
}
