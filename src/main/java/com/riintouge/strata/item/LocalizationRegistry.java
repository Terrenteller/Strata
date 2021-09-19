package com.riintouge.strata.item;

import net.minecraftforge.fml.common.SidedProxy;

import javax.annotation.Nullable;
import java.util.Map;

public abstract class LocalizationRegistry
{
    @SidedProxy(
        modId = "strata",
        serverSide = "com.riintouge.strata.item.ServerLocalizationRegistry",
        clientSide = "com.riintouge.strata.item.ClientLocalizationRegistry" )
    public static LocalizationRegistry INSTANCE;

    public abstract void register( Object object , String unlocalizedKey , Map< String , String > languageMap );

    @Nullable
    public abstract String get( Object object );
}
