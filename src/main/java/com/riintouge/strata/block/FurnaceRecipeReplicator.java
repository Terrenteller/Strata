package com.riintouge.strata.block;

import com.riintouge.strata.Strata;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class FurnaceRecipeReplicator
{
    public static void replicateTargetRecipeOrCreateNew(
        @Nonnull Item source,
        @Nullable MetaResourceLocation targetMetaResource,
        @Nullable Float experience )
    {
        if( targetMetaResource == null )
            return;

        ItemStack furnaceResult = targetMetaResource.toItemStack();
        if( furnaceResult != null )
        {
            Float exp = experience != null ? experience : FurnaceRecipes.instance().getSmeltingExperience( furnaceResult );
            GameRegistry.addSmelting( source , furnaceResult , exp );
        }
    }

    @SubscribeEvent( priority = EventPriority.LOWEST )
    public static void registerRecipes( RegistryEvent.Register< IRecipe > event )
    {
        System.out.println( "FurnaceRecipeReplicator::registerRecipes()" );

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
                    // Respect specialized furnace recipes
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
