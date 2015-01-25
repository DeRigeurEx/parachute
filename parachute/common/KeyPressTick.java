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
package com.parachute.common;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraft.entity.player.EntityPlayer;
import org.lwjgl.input.Keyboard;

// intercept the ascend key to make the parachute go up
// the ascend key defaults to the space bar or jump key.
public class KeyPressTick {
	private final int ascendKey = ParachuteCommonProxy.getAscendKey();

	@SubscribeEvent
	public void onTick(TickEvent.PlayerTickEvent event)
	{
		if (event.phase.equals(TickEvent.Phase.START)) {
			if (Keyboard.getEventKey() == ascendKey) {
				EntityPlayer player = event.player;
				if (player != null) {
					boolean keyPressed = Keyboard.isKeyDown(ascendKey);
					PacketHandler.network.sendToServer(new KeyPressMessage(ascendKey, keyPressed));
				}
			}
		}
	}

}
