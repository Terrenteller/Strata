package com.riintouge.strata;

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

@Mod(
    modid = Strata.MOD_ID,
    name = Strata.NAME,
    version = Strata.INTERNAL_VERSION,
    guiFactory = "com.riintouge.strata.gui.StrataGuiFactory" )
public class Strata
{
    // Values here MUST stay in sync with mcmod.info where not replaced by build.gradle!
    public static final String MOD_ID = "strata";
    public static final String NAME = "Strata";
    // These versions are distinct in GuiModList when useMetadata on the Mod annotation is false (the default)
    public static final String DISPLAY_VERSION = "BUILD_DISPLAY_VERSION"; // Actually sourced from mcmod.info
    public static final String INTERNAL_VERSION = "BUILD_INTERNAL_VERSION"; // Internal according to FMLModContainer
    public static final Logger LOGGER = LogManager.getLogger( MOD_ID );

    @Mod.Instance( MOD_ID )
    public static Strata INSTANCE = new Strata(); // Cannot be final because of the annotation

    @SidedProxy(
        serverSide = "com.riintouge.strata.proxy.ServerProxy",
        clientSide = "com.riintouge.strata.proxy.ClientProxy" )
    public static CommonProxy PROXY;

    @EventHandler
    public void preInit( FMLPreInitializationEvent event )
    {
        PROXY.preInit( event );
    }

    @EventHandler
    public void init( FMLInitializationEvent event )
    {
        PROXY.init( event );
    }

    @EventHandler
    public void postInit( FMLPostInitializationEvent event )
    {
        PROXY.postInit( event );
    }

    // Statics

    public static ResourceLocation resource( String resourcePath )
    {
        return new ResourceLocation( MOD_ID , resourcePath );
    }
}
