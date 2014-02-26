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
import net.minecraft.entity.player.EntityPlayer;
import org.lwjgl.input.Keyboard;

// used to intercept the space bar to make the parachute go up
// ridin' the thermals
public class KeyPressTick {

	@SubscribeEvent
	public void onTick(TickEvent.PlayerTickEvent event)
	{
		onPlayerTick(event.player);
	}

	private void onPlayerTick(EntityPlayer p)
	{
		PlayerInfo pi = ParachutePlayerManager.instance().getPlayerInfoFromPlayer(p);
		if (pi != null) {
			if (Keyboard.isKeyDown(Keyboard.KEY_SPACE)) { // grab space bar key while riding parachute
				Parachute.packetPipeline.sendToServer(new ParachutePacket(Keyboard.KEY_SPACE, true));
			} else {
				Parachute.packetPipeline.sendToServer(new ParachutePacket(Keyboard.KEY_SPACE, false));
			}
		}
	}

}
