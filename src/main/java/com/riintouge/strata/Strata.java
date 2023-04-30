package com.riintouge.strata;

import com.riintouge.strata.gui.*;
import com.riintouge.strata.proxy.CommonProxy;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod( modid = Strata.modid , name = Strata.name , version = Strata.internalVersion , guiFactory = "com.riintouge.strata.gui.StrataGuiFactory" )
public class Strata
{
    // We're not using useMetadata so that display and internal versions remain separate.
    // Values here MUST stay in sync with mcmod.info where not replaced by build.gradle!
    public static final String modid = "strata";
    public static final String name = "Strata";
    // These two are distinct in GuiModList
    public static final String displayVersion = "DISPLAY_VERSION";
    public static final String internalVersion = "INTERNAL_VERSION"; // Internal according to FMLModContainer

    public static final StrataBlocksTab BLOCK_TAB = new StrataBlocksTab();
    public static final StrataBuildingBlocksTab BUILDING_BLOCK_TAB = new StrataBuildingBlocksTab();
    public static final StrataMiscObjectsTab MISC_BLOCK_TAB = new StrataMiscObjectsTab();
    public static final StrataBlockFragmentsTab BLOCK_FRAGMENT_TAB = new StrataBlockFragmentsTab();
    public static final StrataBlockSamplesTab BLOCK_SAMPLE_TAB = new StrataBlockSamplesTab();
    public static final StrataOreBlocksTab ORE_BLOCK_TAB = new StrataOreBlocksTab();
    public static final StrataOreItemsTab ORE_ITEM_TAB = new StrataOreItemsTab();
    public static final StrataOreSamplesTab ORE_SAMPLE_TAB = new StrataOreSamplesTab();

    public static Logger LOGGER = LogManager.getLogger( modid );

    @Mod.Instance( modid )
    public static Strata instance = new Strata();

    @SidedProxy( serverSide = "com.riintouge.strata.proxy.ServerProxy" , clientSide = "com.riintouge.strata.proxy.ClientProxy" )
    public static CommonProxy proxy;

    @EventHandler
    public void preInit( FMLPreInitializationEvent event )
    {
        LOGGER = event.getModLog();
        proxy.preInit( event );
    }

    @EventHandler
    public void init( FMLInitializationEvent event )
    {
        proxy.init( event );
    }

    @EventHandler
    public void postInit( FMLPostInitializationEvent event )
    {
        proxy.postInit( event );
    }

    public static ResourceLocation resource( String resourcePath )
    {
        return new ResourceLocation( modid , resourcePath );
    }
}
