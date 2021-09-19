package com.riintouge.strata.gui;

import com.riintouge.strata.gui.config.StrataConfigGui;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.client.IModGuiFactory;

import java.io.File;
import java.util.Set;

public class StrataGuiFactory implements IModGuiFactory
{
    @Override
    public void initialize( Minecraft minecraftInstance )
    {
        // Nothing to do
    }

    @Override
    public boolean hasConfigGui()
    {
        return true;
    }

    @Override
    public GuiScreen createConfigGui( GuiScreen parentScreen )
    {
        return new StrataConfigGui( parentScreen );
    }

    @Override
    public Set< RuntimeOptionCategoryElement > runtimeGuiCategories()
    {
        return null;
    }

    // Statics

    // Forge has a method by the same name that makes bad assumptions
    public static String getAbridgedConfigPath( String path )
    {
        String homeDir = System.getProperty( "user.home" );
        if( homeDir.endsWith( File.separator ) )
            homeDir = homeDir.substring( 0 , homeDir.length() - 1 );

        return path.startsWith( homeDir )
            ? String.format( "~%s" , path.substring( homeDir.length() ) )
            : path;
    }
}
