package com.riintouge.strata.block.geo;

import com.riintouge.strata.Strata;
import com.riintouge.strata.block.SpecialBlockPropertyFlags;
import com.riintouge.strata.util.FlagUtil;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
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
        setRegistryName( resource );
        setUnlocalizedName( resource.toString() );

        setCreativeTab( Strata.BLOCK_FRAGMENT_TAB );
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

    // Statics

    @Nullable
    public static ResourceLocation fragmentRegistryName( IGeoTileInfo tileInfo )
    {
        return tileInfo.type().fragmentRegistryName( tileInfo.tileSetName() );
    }
}
