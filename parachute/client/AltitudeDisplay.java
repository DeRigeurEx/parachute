//  
//  =====GPL=============================================================
//  This program is free software; you can redistribute it and/or modify
//  it under the terms of the GNU General Public License as published by
//  the Free Software Foundation; version 2 dated June, 1991.
// 
//  This program is distributed in the hope that it will be useful, 
//  but WITHOUT ANY WARRANTY; without even the implied warranty of
//  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//  GNU General Public License for more details.
// 
//  You should have received a copy of the GNU General Public License
//  along with this program;  if not, write to the Free Software
//  Foundation, Inc., 675 Mass Ave., Cambridge, MA 02139, USA.
//  =====================================================================
//
//
// Copyright 2011-2015 Michael Sheppard (crackedEgg)
//
package com.parachute.client;

import com.parachute.common.ParachuteCommonProxy;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class AltitudeDisplay {
	
	public static String altitude = "";
	private final Minecraft mc = Minecraft.getMinecraft();
    private int screenX;
	private int screenY;
	private final int colorRed = 0xffcc0000;
	private final int colorGreen = 0xff00cc00;
	private final int nextX = 36;
	
	public AltitudeDisplay()
	{
		super();
		ScaledResolution sr = new ScaledResolution(mc, mc.displayWidth, mc.displayHeight);
		screenX = sr.getScaledWidth();
        screenY = sr.getScaledHeight();
	}
	
	// the altitude display is placed where the food bar is
	// the food bar is removed when riding boats, parachutes, etc.
	public void updateWindowScale()
	{
		ScaledResolution sr = new ScaledResolution(mc, mc.displayWidth, mc.displayHeight);
		screenX = (sr.getScaledWidth() / 2) + 10;
        screenY = sr.getScaledHeight() - 38;
	}
	
	@SubscribeEvent()
    public void onRender(RenderGameOverlayEvent event) {
        if (event.isCancelable() || mc.gameSettings.showDebugInfo || mc.thePlayer.onGround) {
			return;
		}
		
		if (mc.inGameHasFocus && event.type == RenderGameOverlayEvent.ElementType.ALL) {
			if (ParachuteCommonProxy.onParachute(mc.thePlayer)) {
				updateWindowScale();
				mc.fontRendererObj.drawStringWithShadow("Altitude: ", screenX, screenY, colorRed);
				mc.fontRendererObj.drawStringWithShadow(altitude, screenX + nextX, screenY, colorGreen);
			}
		}
	}
	
	public static void setAltitudeString(String text)
	{
		altitude = text;
	}
}
