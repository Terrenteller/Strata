package com.riintouge.strata.gui.config;

import com.riintouge.strata.StrataConfig;
import com.riintouge.strata.gui.StrataGuiFactory;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.GuiConfigEntries;
import net.minecraftforge.fml.client.config.IConfigElement;

public abstract class BaseConfigCategoryEntry extends GuiConfigEntries.CategoryEntry
{
    public BaseConfigCategoryEntry( GuiConfig owningScreen , GuiConfigEntries owningEntryList , IConfigElement prop )
    {
        super( owningScreen , owningEntryList , prop );
    }

    public abstract String category();

    // GuiConfigEntries.CategoryEntry overrides

    @Override
    protected GuiScreen buildChildScreen()
    {
        return new GuiConfig(
            this.owningScreen,
            ( new ConfigElement( StrataConfig.INSTANCE.getConfig().getCategory( category() ) ) ).getChildElements(),
            this.owningScreen.modID,
            category(),
            this.configElement.requiresWorldRestart() || this.owningScreen.allRequireWorldRestart,
            this.configElement.requiresMcRestart() || this.owningScreen.allRequireMcRestart,
            String.join( " > " , this.owningScreen.title , I18n.format( this.configElement.getLanguageKey() ) ),
            StrataGuiFactory.getAbridgedConfigPath( StrataConfig.INSTANCE.getConfig().toString() ) );
    }
}
