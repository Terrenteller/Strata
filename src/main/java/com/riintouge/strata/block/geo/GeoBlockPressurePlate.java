package com.riintouge.strata.block.geo;

import com.riintouge.strata.Strata;
import net.minecraft.block.BlockPressurePlate;
import net.minecraft.util.ResourceLocation;

public class GeoBlockPressurePlate extends BlockPressurePlate
{
    public GeoBlockPressurePlate( IGeoTileInfo info )
    {
        super( info.material() , Sensitivity.MOBS );

        ResourceLocation registryName = info.type().pressurePlateType().registryName( info.tileSetName() );
        setRegistryName( registryName );
        setUnlocalizedName( registryName.toString() );
        setCreativeTab( Strata.MISC_ITEM_TAB );

        setHarvestLevel( info.harvestTool() , info.harvestLevel() );
        setSoundType( info.soundType() );
        setHardness( info.hardness() );
        setResistance( info.explosionResistance() );
    }
}
