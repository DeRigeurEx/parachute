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

import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class PlayerTickEventHandler {

	@SubscribeEvent
	public void onTick(TickEvent.PlayerTickEvent event)
	{
		if (event.phase.equals(TickEvent.Phase.START) && event.side.isServer()) {
			togglePlayerParachutePack((EntityPlayer) event.player);
		}
	}

	// Check the players currently held item and if it is a 
	// parachuteItem set a packItem in the chestplate armor slot.
	// Remove the packItem if the player is no longer holding the parachuteItem
	// and if the player is not on the parachute. If there is an armor item in the
	// armor slot do nothing.
	private void togglePlayerParachutePack(EntityPlayer player)
	{
		if (player != null) {
			ItemStack armor = player.inventory.armorInventory[ParachuteCommonProxy.armorSlot];
			Item heldItem = player.getCurrentEquippedItem().getItem();
			boolean deployed = ParachuteCommonProxy.onParachute(player);
			if (armor != null && heldItem != null) {
				if (armor.getItem() instanceof ItemParachutePack && !(heldItem instanceof ItemParachute) && !deployed) {
					player.inventory.armorInventory[ParachuteCommonProxy.armorSlot] = (ItemStack) null;
				}
			} else {
				if (heldItem != null && heldItem instanceof ItemParachute) {
					player.inventory.armorInventory[ParachuteCommonProxy.armorSlot] = new ItemStack(Parachute.packItem);
				}
			}
		}
	}

}
