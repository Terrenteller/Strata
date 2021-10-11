package com.riintouge.strata.gui.config;

import com.riintouge.strata.StrataConfig;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.GuiConfigEntries;
import net.minecraftforge.fml.client.config.IConfigElement;

import javax.annotation.Nonnull;

public class ServerConfigCategoryEntry extends BaseConfigCategoryEntry
{
    public ServerConfigCategoryEntry( GuiConfig owningScreen , GuiConfigEntries owningEntryList , IConfigElement prop )
    {
        super( owningScreen , owningEntryList , prop );
    }

    // BaseConfigCategoryEntry overrides

    @Nonnull
    @Override
    public String category()
    {
        return StrataConfig.CATEGORY_SERVER;
    }
}
