package com.riintouge.strata;

import com.riintouge.strata.proxy.CommonProxy;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod( modid = Strata.modid , name = Strata.name , version = Strata.version )
public class Strata
{
    public static final String modid = "strata";
    public static final String name = "Strata";
    // TODO: Can we modify this value at build time?
    public static final String version = "1.12.2-19.12.08";

    public static final StrataBlocksTab BLOCK_TAB = new StrataBlocksTab();
    public static final StrataBuildingBlocksTab BUILDING_BLOCK_TAB = new StrataBuildingBlocksTab();
    public static final StrataOreBlocksTab ORE_BLOCK_TAB = new StrataOreBlocksTab();
    public static final StrataOreItemsTab ORE_ITEM_TAB = new StrataOreItemsTab();
    public static final StrataMiscItemsTab MISC_ITEM_TAB = new StrataMiscItemsTab();

    @Mod.Instance( modid )
    public static Strata instance = new Strata();

    @SidedProxy( serverSide = "com.riintouge.strata.proxy.ServerProxy" , clientSide = "com.riintouge.strata.proxy.ClientProxy" )
    public static CommonProxy proxy;

    @EventHandler
    public void preInit( FMLPreInitializationEvent event )
    {
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
