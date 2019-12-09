package com.riintouge.strata.block.ore;

import com.riintouge.strata.Strata;
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

        setHarvestLevel( "shovel" , oreInfo.stoneStrength().ordinal() );
        setSoundType( oreInfo.soundType() );
        setHardness( 3f );
        setResistance( 5f );

        setCreativeTab( Strata.ITEM_TAB );
    }
}
