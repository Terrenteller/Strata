package com.riintouge.strata.block.ore.tileset;

import com.riintouge.strata.Strata;
import com.riintouge.strata.block.ore.info.IOreInfo;
import net.minecraft.block.BlockFalling;

public class GenericSandOreBlock extends BlockFalling
{
    protected IOreInfo oreInfo;

    public GenericSandOreBlock( IOreInfo oreInfo )
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
