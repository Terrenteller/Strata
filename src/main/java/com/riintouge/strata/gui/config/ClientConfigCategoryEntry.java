package com.riintouge.strata.gui.config;

import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.GuiConfigEntries;
import net.minecraftforge.fml.client.config.IConfigElement;

import static com.riintouge.strata.StrataConfig.CATEGORY_CLIENT;

public class ClientConfigCategoryEntry extends BaseConfigCategoryEntry
{
    public ClientConfigCategoryEntry( GuiConfig owningScreen , GuiConfigEntries owningEntryList , IConfigElement prop )
    {
        super( owningScreen , owningEntryList , prop );
    }

    // BaseConfigCategoryEntry overrides

    @Override
    public String category()
    {
        return CATEGORY_CLIENT;
    }
}
