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
// Copyright 2013 Michael Sheppard (crackedEgg)
//
package parachute.common;

import java.util.EnumSet;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.TickType;

public class AADTickHandler implements ITickHandler {
	@Override
	public void tickStart(EnumSet<TickType> type, Object... tickData) {
		if (type.equals(EnumSet.of(TickType.PLAYER))) {
            onPlayerTick((EntityPlayer)tickData[0]);
		}
	}
	
    // Handles the Automatic Activation Device
	// use a fallDistance greater than maxFallDistance to offset the unmount falling distance
	// and call ItemParachute.deployParachute if the player is falling
	private void onPlayerTick(EntityPlayer player) {
        PlayerInfo pi = PlayerManagerParachute.getInstance().getPlayerInfoFromPlayer(player);
        if (pi != null) {
            boolean auto = (pi.aad && !player.capabilities.isCreativeMode);
            int maxFallDistance = ItemAutoActivateDevice.getDelay();//Parachute.instance.getFallDistance();
            if (auto && player.fallDistance > maxFallDistance && !player.onGround && !player.isOnLadder()) {
                ItemStack aad = ItemAutoActivateDevice.inventoryContainsAAD(player.inventory);
                if (aad != null) {
                    if (Parachute.playerIsWearingParachute(player)) {
                        ItemStack parachute = player.getCurrentArmor(Parachute.armorSlot);
                        ((ItemParachute)parachute.getItem()).deployParachute(player.worldObj, player);
                    }
                } // else fall to death!
            }
        }
    }
	
	@Override
	public void tickEnd(EnumSet<TickType> type, Object... tickData) {

	}

	@Override
	public EnumSet<TickType> ticks() {
		return EnumSet.of(TickType.PLAYER, TickType.SERVER);
	}

	@Override
	public String getLabel() {
		return "Parachute AAD";
	}

}
