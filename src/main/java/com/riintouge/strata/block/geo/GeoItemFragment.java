package com.riintouge.strata.block.geo;

import com.riintouge.strata.Strata;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.lang3.NotImplementedException;

public class GeoItemFragment extends Item
{
    public static final String Ball = "ball";

    protected IGeoTileInfo geoTileInfo;

    public GeoItemFragment( IGeoTileInfo geoTileInfo )
    {
        this.geoTileInfo = geoTileInfo;

        String type = getTypeForMaterial( geoTileInfo.material() );
        if( type == null )
            throw new NotImplementedException( "No fragment for material " + geoTileInfo.material().toString() );

        ResourceLocation resource = getResourceLocation( geoTileInfo );
        setRegistryName( resource );
        setUnlocalizedName( resource.toString() );

        setCreativeTab( Strata.BLOCK_FRAGMENT_TAB );
    }

    // Item overrides

    @Override
    public String getItemStackDisplayName( ItemStack stack )
    {
        return geoTileInfo.localizedName();
    }

    @Override
    public String getUnlocalizedName()
    {
        // Strata localization doesn't make a distinction between blocks and items.
        // Fragments will also use their block's name. This is consistent with vanilla clay.
        return geoTileInfo.registryName().toString();
    }

    @Override
    public String getUnlocalizedName( ItemStack stack )
    {
        return getUnlocalizedName();
    }

    // Statics

    public static Material getMaterialForType( String type )
    {
        if( type.equals( Ball ) )
            return Material.CLAY;

        return Material.AIR;
    }

    public static ResourceLocation getResourceLocation( IGeoTileInfo info )
    {
        return Strata.resource( String.format( "%s_%s" , info.tileSetName() , getTypeForMaterial( info.material() ) ) );
    }

    public static String getTypeForMaterial( Material material )
    {
        if( material == Material.CLAY )
            return Ball;

        return null;
    }
}
