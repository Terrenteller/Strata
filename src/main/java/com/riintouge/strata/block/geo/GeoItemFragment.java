package com.riintouge.strata.block.geo;

import com.riintouge.strata.Strata;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.lang3.NotImplementedException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class GeoItemFragment extends Item
{
    public static final String Ball = "ball";

    protected IGeoTileInfo tileInfo;

    public GeoItemFragment( IGeoTileInfo tileInfo )
    {
        this.tileInfo = tileInfo;

        String type = getTypeForMaterial( tileInfo.material() );
        if( type == null )
            throw new NotImplementedException( "No fragment for material " + tileInfo.material().toString() );

        ResourceLocation resource = getResourceLocation( tileInfo );
        setRegistryName( resource );
        setUnlocalizedName( resource.toString() );

        setCreativeTab( Strata.BLOCK_FRAGMENT_TAB );
    }

    // Item overrides

    @Override
    public String getItemStackDisplayName( ItemStack stack )
    {
        return tileInfo.localizedName();
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

    // Statics

    @Nullable
    public static Material getMaterialForType( String type )
    {
        if( type.equals( Ball ) )
            return Material.CLAY;

        return null;
    }

    @Nonnull
    public static ResourceLocation getResourceLocation( IGeoTileInfo tileInfo )
    {
        return Strata.resource( String.format( "%s_%s" , tileInfo.tileSetName() , getTypeForMaterial( tileInfo.material() ) ) );
    }

    @Nullable
    public static String getTypeForMaterial( Material material )
    {
        if( material == Material.CLAY )
            return Ball;

        return null;
    }
}
