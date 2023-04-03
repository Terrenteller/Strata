package com.riintouge.strata.recipe;

import com.riintouge.strata.Strata;
import net.minecraft.potion.PotionType;
import net.minecraftforge.common.brewing.BrewingRecipeRegistry;
import net.minecraftforge.common.brewing.IBrewingRecipe;
import net.minecraftforge.common.brewing.VanillaBrewingRecipe;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class BrewingRecipeReplicator
{
    @SubscribeEvent( priority = EventPriority.LOWEST )
    public static void registerPotionTypes( RegistryEvent.Register< PotionType > event )
    {
        Strata.LOGGER.trace( "BrewingRecipeReplicator::registerPotionTypes()" );

        // Some mods, like Cyclic, add recipes directly via PotionHelper. We can't get the information we need
        // back out the same way because it's buried in private fields with no accessors and whose types are
        // indistinguishable from other fields in the same class. It's difficult to reflect without light.
        // JEI takes a brute-force approach by testing and caching every item that's a potion ingredient.
        // Because Strata's involvement in brewing recipes is minimal and our ingredients should never be seen
        // (our redstone is a proxy ore for vanilla redstone) we are deferring the bulk of this feature.
        // Unfortunately, JEI doesn't detect what we do have.

        for( IBrewingRecipe brewingRecipe : BrewingRecipeRegistry.getRecipes() )
        {
            if( brewingRecipe instanceof VanillaBrewingRecipe )
            {
                BrewingRecipeRegistry.addRecipe( new BrewingRecipeWrapper( brewingRecipe ) );
                break;
            }
        }
    }
}
