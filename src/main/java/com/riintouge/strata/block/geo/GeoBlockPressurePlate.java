package com.riintouge.strata.block.geo;

import com.riintouge.strata.Strata;
import net.minecraft.block.BlockPressurePlate;
import net.minecraft.util.ResourceLocation;

public class GeoBlockPressurePlate extends BlockPressurePlate
{
    public GeoBlockPressurePlate( IGeoTileInfo info )
    {
        // Match vanilla stone pressure plate sensitivity
        super( info.material() , Sensitivity.MOBS );

        ResourceLocation registryName = info.type().pressurePlateType().registryName( info.tileSetName() );
        setRegistryName( registryName );
        setUnlocalizedName( registryName.toString() );
        setCreativeTab( Strata.MISC_BLOCK_TAB );

        // Vanilla sets a precedent with gold pressure plates.
        // Gold blocks require an iron pick or higher to mine but any level pick will break the plate.
        // This justification is extended to Strata buttons and levers as well.
        setHarvestLevel( info.harvestTool() , 0 );
        setSoundType( info.soundType() );
        setHardness( info.hardness() );
        setResistance( info.explosionResistance() );
    }
}
