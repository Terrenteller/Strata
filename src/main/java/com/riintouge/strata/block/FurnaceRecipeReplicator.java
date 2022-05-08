package com.riintouge.strata.block;

import com.riintouge.strata.Strata;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.oredict.OreDictionary;

import java.util.ArrayList;
import java.util.List;

public class FurnaceRecipeReplicator
{
    @SubscribeEvent( priority = EventPriority.LOWEST )
    public static void registerRecipes( RegistryEvent.Register< IRecipe > event )
    {
        Strata.LOGGER.trace( "FurnaceRecipeReplicator::registerRecipes()" );

        FurnaceRecipes furnaceRecipes = FurnaceRecipes.instance();
        for( String oreName : OreDictionary.getOreNames() )
        {
            List< ItemStack > strataOres = new ArrayList<>();
            List< ItemStack > otherOres = new ArrayList<>();

            for( ItemStack ore : OreDictionary.getOres( oreName , false ) )
            {
                String domain = ore.getItem().getRegistryName().getResourceDomain();

                if( domain.equalsIgnoreCase( Strata.modid ) )
                {
                    // Ore registration may have already added a smelting recipe
                    if( furnaceRecipes.getSmeltingResult( ore ) == ItemStack.EMPTY )
                        strataOres.add( ore );
                }
                else if( domain.equalsIgnoreCase( "minecraft" ) )
                    otherOres.add( 0 , ore ); // Prioritize vanilla
                else
                    otherOres.add( ore );
            }

            if( strataOres.size() == 0 )
                continue;

            for( ItemStack otherOre : otherOres )
            {
                ItemStack result = furnaceRecipes.getSmeltingResult( otherOre );
                if( !result.isEmpty() )
                {
                    float exp = furnaceRecipes.getSmeltingExperience( result );
                    for( ItemStack strataOre : strataOres )
                        furnaceRecipes.addSmeltingRecipe( strataOre , result , exp );

                    break;
                }
            }
        }
    }
}
