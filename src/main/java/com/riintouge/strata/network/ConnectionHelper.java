package com.riintouge.strata.network;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiDisconnected;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiMultiplayer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.realms.RealmsBridge;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ConnectionHelper
{
    @SideOnly( Side.CLIENT )
    public static void disconnectBecause( String translationKey , Object ... args )
    {
        Minecraft minecraft = Minecraft.getMinecraft();
        GuiScreen disconnectScreen = new GuiDisconnected(
            minecraft.isIntegratedServerRunning() ? new GuiMainMenu() : new GuiMultiplayer( new GuiMainMenu() ),
            "strata.multiplayer.disconnect.closed",
            new TextComponentTranslation( translationKey , args ) );

        // FIXME: Doing this here hangs the client. Without, we'll disconnect locally but timeout on the server.
        // The optimal solution is to have the server sanity check the client on connect
        // and either provide temporary overrides or kick the client for irreconcilable differences.
        //minecraft.world.sendQuittingDisconnectingPacket();
        //minecraft.loadWorld( (WorldClient)null );

        if( minecraft.isConnectedToRealms() )
        {
            RealmsBridge realmsbridge = new RealmsBridge();
            realmsbridge.switchToRealms( disconnectScreen );
        }
        else
            minecraft.displayGuiScreen( disconnectScreen );
    }
}
