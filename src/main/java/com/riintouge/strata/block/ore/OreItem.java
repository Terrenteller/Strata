package com.riintouge.strata.block.ore;

import com.riintouge.strata.Strata;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

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
        ItemStack equivalentItemStack = oreInfo.equivalentItemStack();
        if( equivalentItemStack != null )
            return equivalentItemStack.getItem().getEntityLifespan( equivalentItemStack , world );

        return super.getEntityLifespan( itemStack , world );
    }

    @SideOnly( Side.CLIENT )
    public void addInformation( ItemStack stack , @Nullable World worldIn , List< String > tooltip , ITooltipFlag flagIn )
    {
        ItemStack equivalentItemStack = oreInfo.equivalentItemStack();
        if( equivalentItemStack != null )
        {
            equivalentItemStack.getItem().addInformation( equivalentItemStack , worldIn , tooltip , flagIn );
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
        ItemStack equivalentItemStack = oreInfo.equivalentItemStack();
        if( equivalentItemStack != null )
        {
            // A proxy may defer to "vanilla logic" with a value of -1. We obviously don't play a part in that.
            int burnTime = equivalentItemStack.getItem().getItemBurnTime( equivalentItemStack );
            if( burnTime != -1 )
                return burnTime;
        }

        return oreInfo.burnTime();
    }

    @Override
    public String getItemStackDisplayName( ItemStack stack )
    {
        ItemStack equivalentItemStack = oreInfo.equivalentItemStack();
        if( equivalentItemStack != null )
            return equivalentItemStack.getItem().getItemStackDisplayName( equivalentItemStack );

        String localizedName = oreInfo.localizedName();
        return localizedName != null ? localizedName : getRegistryName().toString();
    }

    @Override
    public String getUnlocalizedName()
    {
        ItemStack equivalentItemStack = oreInfo.equivalentItemStack();
        if( equivalentItemStack != null )
            return equivalentItemStack.getItem().getUnlocalizedName();

        // Strata localization doesn't make a distinction between blocks and items
        return super.getUnlocalizedName().replaceFirst( "item." , "" );
    }

    @Override
    public String getUnlocalizedName( ItemStack stack )
    {
        ItemStack equivalentItemStack = oreInfo.equivalentItemStack();
        if( equivalentItemStack != null )
            return equivalentItemStack.getItem().getUnlocalizedName( equivalentItemStack );

        return getUnlocalizedName();
    }

    @Override
    public boolean isBeaconPayment( ItemStack stack )
    {
        ItemStack equivalentItemStack = oreInfo.equivalentItemStack();
        if( equivalentItemStack != null )
            return equivalentItemStack.getItem().isBeaconPayment( equivalentItemStack );

        return super.isBeaconPayment( stack );
    }
}
