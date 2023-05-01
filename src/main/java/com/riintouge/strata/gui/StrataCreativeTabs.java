package com.riintouge.strata.gui;

import com.riintouge.strata.Strata;
import com.riintouge.strata.util.DebugUtil;
import com.riintouge.strata.util.ReflectionUtil;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.lang.reflect.Field;

@SideOnly( Side.CLIENT )
public class StrataCreativeTabs extends CreativeTabs
{
    public static StrataCreativeTabs BLOCK_TAB          = new StrataCreativeTabs( "strataBlocksTab"         , Strata.resource( "gneiss" )               );
    public static StrataCreativeTabs BUILDING_BLOCK_TAB = new StrataCreativeTabs( "strataBuildingBlocksTab" , Strata.resource( "gneiss_stonewall" )     );
    public static StrataCreativeTabs MISC_BLOCK_TAB     = new StrataCreativeTabs( "strataMiscBlocksTab"     , Strata.resource( "gneiss_pressureplate" ) );
    public static StrataCreativeTabs BLOCK_FRAGMENT_TAB = new StrataCreativeTabs( "strataBlockFragmentsTab" , Strata.resource( "gneiss_rock" )          );
    public static StrataCreativeTabs BLOCK_SAMPLE_TAB   = new StrataCreativeTabs( "strataBlockSamplesTab"   , Strata.resource( "gneiss_sample" )        );
    public static StrataCreativeTabs ORE_BLOCK_TAB      = new StrataCreativeTabs( "strataOreBlocksTab"      , Strata.resource( "banded_iron_ore" )      );
    public static StrataCreativeTabs ORE_ITEM_TAB       = new StrataCreativeTabs( "strataOreItemsTab"       , Strata.resource( "cinnabar" )             );
    public static StrataCreativeTabs ORE_SAMPLE_TAB     = new StrataCreativeTabs( "strataOreSamplesTab"     , Strata.resource( "galena_sample" )        );

    private static int TAB_COUNT;
    private static CreativeTabs FIRST_TAB;
    private static CreativeTabs LAST_TAB;
    protected final ResourceLocation itemStackResourceLocation;

    StrataCreativeTabs( String label , ResourceLocation itemStackResourceLocation )
    {
        super( label );
        this.itemStackResourceLocation = itemStackResourceLocation;

        if( FIRST_TAB == null )
            FIRST_TAB = this;

        TAB_COUNT++;
        LAST_TAB = this;
    }

    @Override
    public ItemStack getTabIconItem()
    {
        return new ItemStack( Item.REGISTRY.getObject( itemStackResourceLocation ) );
    }

    // Statics

    public static int getTabCount()
    {
        return TAB_COUNT;
    }

    private static Field findTabIndexField() throws Exception
    {
        Field tabIndexField = null;

        for( Field intField : ReflectionUtil.findFieldsByType( CreativeTabs.class , int.class , false ) )
        {
            boolean fieldWasAccessible = intField.isAccessible();
            intField.setAccessible( true );

            boolean isTabIndexField = false;
            for( int index = 0 ; index < CreativeTabs.CREATIVE_TAB_ARRAY.length ; index++ )
            {
                CreativeTabs tab = CreativeTabs.CREATIVE_TAB_ARRAY[ index ];
                if( tab.getTabIndex() != intField.getInt( tab ) )
                    break;
                else if( index == ( CreativeTabs.CREATIVE_TAB_ARRAY.length - 1 ) )
                    isTabIndexField = true;
            }

            if( isTabIndexField )
            {
                tabIndexField = intField;
                ReflectionUtil.unfinalizeField( tabIndexField );
            }
            else
                intField.setAccessible( fieldWasAccessible );
        }

        return tabIndexField;
    }

    public static void moveStrataTabsToBeforeOtherModTabs()
    {
        int firstStrataTabIndex = FIRST_TAB.getTabIndex();
        int firstModTabIndex = CreativeTabs.INVENTORY.getTabIndex() + 1;
        if( firstStrataTabIndex == firstModTabIndex )
            return;

        try
        {
            Field tabIndexField = findTabIndexField();
            if( tabIndexField == null )
                return;

            int lastStrataTabIndex = LAST_TAB.getTabIndex();
            int backwardsOffset = getTabCount();
            int forwardOffset = firstStrataTabIndex - firstModTabIndex;
            CreativeTabs[] currentTabs = CreativeTabs.CREATIVE_TAB_ARRAY;
            CreativeTabs[] rearrangedTabs = new CreativeTabs[ CreativeTabs.CREATIVE_TAB_ARRAY.length ];

            for( int index = 0 ; index < currentTabs.length ; index++ )
            {
                int targetIndex;

                if( index < firstModTabIndex || index > lastStrataTabIndex )
                    targetIndex = index;
                else if( index < firstStrataTabIndex )
                    targetIndex = index + backwardsOffset;
                else
                    targetIndex = index - forwardOffset;

                rearrangedTabs[ targetIndex ] = currentTabs[ index ];
                tabIndexField.setInt( currentTabs[ index ] , targetIndex );
            }

            CreativeTabs.CREATIVE_TAB_ARRAY = rearrangedTabs;
        }
        catch( Exception e )
        {
            Strata.LOGGER.error( DebugUtil.prettyPrintException( e , "Caught %s while rearranging creative tabs" ) );
        }
    }
}
