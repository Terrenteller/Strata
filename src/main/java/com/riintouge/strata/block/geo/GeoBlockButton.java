package com.riintouge.strata.block.geo;

import com.riintouge.strata.Strata;
import net.minecraft.block.BlockButtonStone;
import net.minecraft.util.ResourceLocation;

public class GeoBlockButton extends BlockButtonStone
{
    public GeoBlockButton( IGeoTileInfo info )
    {
        ResourceLocation registryName = info.type().buttonType().registryName( info.tileSetName() );
        setRegistryName( registryName );
        setUnlocalizedName( registryName.toString() );
        setCreativeTab( Strata.MISC_BLOCK_TAB );

        setHarvestLevel( info.harvestTool() , 0 );
        setSoundType( info.soundType() );
        setHardness( info.hardness() );
        setResistance( info.explosionResistance() );
    }
}
