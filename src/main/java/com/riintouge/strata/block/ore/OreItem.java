package com.riintouge.strata.block.ore;

import com.riintouge.strata.Strata;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class OreItem extends Item
{
    protected IOreInfo oreInfo;

    public OreItem( IOreInfo oreInfo )
    {
        this.oreInfo = oreInfo;

        ResourceLocation resource = Strata.resource( oreInfo.oreName() );
        setRegistryName( resource );
        setUnlocalizedName( resource.toString() );

        setCreativeTab( Strata.ORE_ITEM_TAB );
    }

    // Item overrides

    @Override
    public int getEntityLifespan( ItemStack itemStack , World world )
    {
        ItemStack proxyDrop = oreInfo.proxyDrop();
        if( proxyDrop != null )
            return proxyDrop.getItem().getEntityLifespan( proxyDrop , world );

        return super.getEntityLifespan( itemStack , world );
    }

    public void addInformation( ItemStack stack , @Nullable World worldIn , List< String > tooltip , ITooltipFlag flagIn )
    {
        ItemStack proxyItemStack = oreInfo.proxyItemStack();
        if( proxyItemStack != null )
        {
            proxyItemStack.getItem().addInformation( proxyItemStack , worldIn , tooltip , flagIn );
            return;
        }

        super.addInformation( stack , worldIn , tooltip , flagIn );

        List< String > tooltipLines = oreInfo.localizedTooltip();
        if( tooltipLines != null )
            tooltip.addAll( tooltipLines );
    }

    @Override
    public int getItemBurnTime( ItemStack itemStack )
    {
        ItemStack proxyDrop = oreInfo.proxyDrop();
        if( proxyDrop != null )
        {
            int burnTime = proxyDrop.getItem().getItemBurnTime( proxyDrop );
            if( burnTime != -1 )
                return burnTime;
        }

        // A proxy may defer to "vanilla logic" with a value of -1. We obviously don't play a part in that.
        return oreInfo.burnTime();
    }

    @Override
    public String getItemStackDisplayName( ItemStack stack )
    {
        ItemStack proxyDrop = oreInfo.proxyDrop();
        if( proxyDrop != null )
            return proxyDrop.getItem().getItemStackDisplayName( proxyDrop );

        String localizedName = oreInfo.localizedName();
        return localizedName != null ? localizedName : getRegistryName().toString();
    }

    @Override
    public float getSmeltingExperience( ItemStack item )
    {
        ItemStack proxyDrop = oreInfo.proxyDrop();
        if( proxyDrop != null )
        {
            float smeltingExp = proxyDrop.getItem().getSmeltingExperience( proxyDrop );
            if( smeltingExp != -1 )
                return smeltingExp;
        }

        // A proxy may defer to "vanilla logic" with a value of -1. We obviously don't play a part in that.
        return oreInfo.furnaceExp() != null ? oreInfo.furnaceExp() : -1;
    }

    @Override
    public String getUnlocalizedName()
    {
        ItemStack proxyDrop = oreInfo.proxyDrop();
        if( proxyDrop != null )
            return proxyDrop.getItem().getUnlocalizedName();

        // Strata localization doesn't make a distinction between blocks and items
        return super.getUnlocalizedName().replaceFirst( "item." , "" );
    }

    @Override
    public String getUnlocalizedName( ItemStack stack )
    {
        ItemStack proxyDrop = oreInfo.proxyDrop();
        if( proxyDrop != null )
            return proxyDrop.getItem().getUnlocalizedName( proxyDrop );

        return getUnlocalizedName();
    }

    @Override
    public boolean isBeaconPayment( ItemStack stack )
    {
        ItemStack proxyDrop = oreInfo.proxyDrop();
        if( proxyDrop != null )
            return proxyDrop.getItem().isBeaconPayment( proxyDrop );

        return super.isBeaconPayment( stack );
    }
}
