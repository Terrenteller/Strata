package com.riintouge.strata.item;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.IdentityHashMap;
import java.util.Map;

@SideOnly( Side.SERVER )
public final class ServerLocalizationRegistry extends LocalizationRegistry
{
    private Map< Object , Map< String , String > > objectLocalizationMaps = new IdentityHashMap<>();
    private Map< Object , String > unlocalizedKeys = new IdentityHashMap<>();

    public void register( Object object , String unlocalizedKey , Map< String , String > languageMap )
    {
        objectLocalizationMaps.put( object , languageMap );
        unlocalizedKeys.put( object , unlocalizedKey );
    }

    @Nullable
    public String get( Object object )
    {
        return unlocalizedKeys.getOrDefault( object , null );
    }
}
