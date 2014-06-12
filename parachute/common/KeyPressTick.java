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
// Copyright 2011-2014 Michael Sheppard (crackedEgg)
//
package com.parachute.common;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.EntityPlayer;
import org.lwjgl.input.Keyboard;

// intercept the space bar to make the parachute go up

@SideOnly(Side.CLIENT)
public class KeyPressTick {

	@SubscribeEvent
	public void onTick(TickEvent.PlayerTickEvent event)
	{
		if (event.phase.equals(TickEvent.Phase.START)) {
			if (Keyboard.getEventKey() == Keyboard.KEY_SPACE) {
				EntityPlayer player = event.player;
				if (player != null) {
					boolean keyPressed = Keyboard.isKeyDown(Keyboard.KEY_SPACE);
//					int playerID = player.getEntityId();
//					Parachute.packetPipeline.sendToServer(new ParachutePacket(Keyboard.KEY_SPACE, keyPressed, playerID));
					PacketHandler.INSTANCE.sendToServer(new KeyPressMessage(Keyboard.KEY_SPACE, keyPressed));
				}
			}
		}
	}
	
}
