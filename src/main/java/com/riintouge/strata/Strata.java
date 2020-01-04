package com.riintouge.strata;

import com.riintouge.strata.proxy.CommonProxy;
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
    public static final String version = "1.12.2-19.12.08";

    public static final StrataBlockTab BLOCK_TAB = new StrataBlockTab();
    public static final StrataItemTab ITEM_TAB = new StrataItemTab();

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
}
