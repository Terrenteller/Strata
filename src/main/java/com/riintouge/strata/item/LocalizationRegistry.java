package com.riintouge.strata.item;

import net.minecraftforge.fml.common.SidedProxy;

import javax.annotation.Nullable;
import java.util.Map;

public abstract class LocalizationRegistry
{
    // A proxy is required because the client implements a SideOnly interface
    @SidedProxy(
        modId = "strata",
        serverSide = "com.riintouge.strata.item.ServerLocalizationRegistry",
        clientSide = "com.riintouge.strata.item.ClientLocalizationRegistry" )
    public static LocalizationRegistry INSTANCE;

    public abstract void register( String unlocalizedKey , Map< String , String > languageMap );

    @Nullable
    public abstract String get( String unlocalizedKey );
}
