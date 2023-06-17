package com.riintouge.strata.item.geo;

import com.riintouge.strata.block.SpecialBlockPropertyFlags;
import com.riintouge.strata.block.geo.IGeoTileInfo;
import com.riintouge.strata.util.FlagUtil;
import com.riintouge.strata.item.ItemHelper;
import net.minecraft.block.BlockSlab;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemSlab;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class GeoItemBlockSlab extends ItemSlab
{
    protected IGeoTileInfo tileInfo;

    public GeoItemBlockSlab( IGeoTileInfo tileInfo , BlockSlab singleSlab , BlockSlab doubleSlab )
    {
        super( singleSlab , singleSlab , doubleSlab );
        this.tileInfo = tileInfo;

        String blockRegistryName = block.getRegistryName().toString();
        setRegistryName( blockRegistryName );
        setUnlocalizedName( blockRegistryName );
    }

    // ItemBlock overrides

    @Override
    public String getUnlocalizedName()
    {
        return this.block.getUnlocalizedName().replaceAll( "tile." , "" );
    }

    @Override
    public String getUnlocalizedName( ItemStack stack )
    {
        return getUnlocalizedName();
    }

    // Item overrides

    @Override
    public String getItemStackDisplayName( ItemStack stack )
    {
        String name = tileInfo.localizedName();
        return name != null ? name : tileInfo.registryName().toString();
    }

    @Override
    @SideOnly( Side.CLIENT )
    public boolean hasEffect( ItemStack stack )
    {
        ItemStack equivalentItemStack = tileInfo.equivalentItemStack();
        return ( equivalentItemStack != null && equivalentItemStack.hasEffect() )
            || FlagUtil.check( tileInfo.specialBlockPropertyFlags() , SpecialBlockPropertyFlags.HAS_EFFECT )
            || super.hasEffect( stack );
    }

    @Override
    public EnumActionResult onItemUse(
        EntityPlayer player,
        World worldIn,
        BlockPos pos,
        EnumHand hand,
        EnumFacing facing,
        float hitX,
        float hitY,
        float hitZ )
    {
        return ItemHelper.onItemUseWithStatisticsFix(
            player,
            hand,
            () -> super.onItemUse( player , worldIn , pos , hand , facing , hitX , hitY , hitZ ) );
    }
}
