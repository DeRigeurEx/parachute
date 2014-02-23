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
import net.minecraft.item.ItemStack;

public class AADTick {
	
	@SubscribeEvent
	public void onTick(TickEvent.PlayerTickEvent event){
		onPlayerTick(event.player);
	}

    // Handles the Automatic Activation Device
    // deploy the parachute if the player is at an altitude of Parachute.getAADAltitude()
    // and deactivate the AAD, consider it a one shot, you must re-activate it.
    private void onPlayerTick(EntityPlayer player) {
		if (Parachute.playerIsWearingParachute(player)) {
			ItemStack parachute = player.getCurrentArmor(Parachute.armorSlot);
			ItemStack aad = ItemAutoActivateDevice.inventoryContainsAAD(player.inventory);
			if (aad != null) {
				boolean auto = (!player.capabilities.isCreativeMode);
				boolean autoAltitudeReached = ItemAutoActivateDevice.getAutoActivateAltitude(player);
				if (auto && autoAltitudeReached && !player.onGround && !player.isOnLadder()) {
					((ItemParachute) parachute.getItem()).deployParachute(player.worldObj, player);
				}
			} // else fall to death!
		}
    }

}
