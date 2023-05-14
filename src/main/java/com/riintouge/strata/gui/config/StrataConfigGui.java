package com.riintouge.strata.gui.config;

import com.riintouge.strata.Strata;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.fml.client.config.DummyConfigElement;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.IConfigElement;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class StrataConfigGui extends GuiConfig
{
    public StrataConfigGui( GuiScreen parentScreen )
    {
        super( parentScreen , getConfigElements() , Strata.MOD_ID , false , false , I18n.format( "strata.config.main" ) );
    }

    // Statics

    @Nonnull
    private static List< IConfigElement > getConfigElements()
    {
        List< IConfigElement > list = new ArrayList<>();
        list.add( new DummyConfigElement.DummyCategoryElement( "strataClientConfig" , "strata.config.clientCategory" , ClientConfigCategoryEntry.class ) );
        list.add( new DummyConfigElement.DummyCategoryElement( "strataServerConfig" , "strata.config.serverCategory" , ServerConfigCategoryEntry.class ) );

        return list;
    }
}
