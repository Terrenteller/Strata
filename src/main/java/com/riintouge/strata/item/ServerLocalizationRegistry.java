package com.riintouge.strata.item;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.Map;

@SideOnly( Side.SERVER )
public final class ServerLocalizationRegistry extends LocalizationRegistry
{
    public void register( String unlocalizedKey , Map< String , String > languageMap )
    {
        // Nothing to do
    }

    @Nullable
    public String get( String unlocalizedKey )
    {
        return unlocalizedKey;
    }
}
