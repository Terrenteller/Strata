package com.riintouge.strata.item.geo;

import com.riintouge.strata.block.SpecialBlockPropertyFlags;
import com.riintouge.strata.block.geo.GeoTileSetRegistry;
import com.riintouge.strata.block.geo.IGeoTileInfo;
import com.riintouge.strata.block.geo.IGeoTileSet;
import com.riintouge.strata.gui.StrataCreativeTabs;
import com.riintouge.strata.util.FlagUtil;
import com.riintouge.strata.item.ItemHelper;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;

public class GeoItemFragment extends Item
{
    protected IGeoTileInfo tileInfo;

    public GeoItemFragment( IGeoTileInfo tileInfo )
    {
        this.tileInfo = tileInfo;

        ResourceLocation resource = GeoItemFragment.fragmentRegistryName( tileInfo );
        assert resource != null;
        setCreativeTab( StrataCreativeTabs.BLOCK_FRAGMENT_TAB );
        setRegistryName( resource );
        setUnlocalizedName( resource.toString() );
    }

    // Item overrides

    @Override
    public int getItemBurnTime( ItemStack itemStack )
    {
        return tileInfo.fragmentBurnTime();
    }

    @Override
    public String getItemStackDisplayName( ItemStack stack )
    {
        String name = tileInfo.localizedName();
        return name != null ? name : tileInfo.registryName().toString();
    }

    @Override
    public String getUnlocalizedName()
    {
        // Strata localization doesn't make a distinction between blocks and items.
        // Fragments will also use their block's name. This is consistent with vanilla clay.
        return tileInfo.registryName().toString();
    }

    @Override
    public String getUnlocalizedName( ItemStack stack )
    {
        return getUnlocalizedName();
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
    public EnumActionResult onItemUse( EntityPlayer player , World worldIn , BlockPos pos , EnumHand hand , EnumFacing facing , float hitX , float hitY , float hitZ )
    {
        IGeoTileSet geoTileSet = GeoTileSetRegistry.INSTANCE.find( tileInfo.tileSetName() );
        Block sampleBlock = geoTileSet != null ? geoTileSet.getSampleBlock() : null;
        if( sampleBlock == null )
            return EnumActionResult.PASS;

        // The sample and fragment are equivalent in "units" of material but that does not make the fragment
        // the item block of the sample. Fragments may not exist and the sample already has its own item block.
        return ItemHelper.onItemUseWithStatisticsFix(
            player,
            hand,
            () -> ItemHelper.placeItemAsBlock( this , sampleBlock , player , worldIn , pos , hand , facing , hitX , hitY , hitZ ) );
    }

    // Statics

    @Nullable
    public static ResourceLocation fragmentRegistryName( IGeoTileInfo tileInfo )
    {
        return tileInfo.tileType().fragmentRegistryName( tileInfo.tileSetName() );
    }
}
