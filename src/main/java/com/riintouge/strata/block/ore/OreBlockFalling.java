package com.riintouge.strata.block.ore;

import com.riintouge.strata.Strata;
import net.minecraft.block.BlockFalling;

public class OreBlockFalling extends BlockFalling
{
    protected IOreInfo oreInfo;

    public OreBlockFalling( IOreInfo oreInfo )
    {
        super( oreInfo.material() );
        this.oreInfo = oreInfo;

        setRegistryName( Strata.modid + ":" + oreInfo.oreName() );
        setUnlocalizedName( Strata.modid + ":" + oreInfo.oreName() );

        setHarvestLevel( oreInfo.harvestTool() , oreInfo.harvestLevel() );
        setSoundType( oreInfo.soundType() );
        setHardness( oreInfo.hardness() );
        setResistance( oreInfo.explosionResistance() );

        setCreativeTab( Strata.ITEM_TAB );
    }
}
