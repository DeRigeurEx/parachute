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

import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.living.LivingFallEvent;

public class PlayerFallEvent {

	public PlayerFallEvent()
	{
		Parachute.proxy.info("PlayerFallEvent ctor");
	}

	@SubscribeEvent
	public void onFallEvent(LivingFallEvent event)
	{
		EntityLivingBase rider = event.entityLiving;
		if (rider instanceof EntityPlayer && ParachuteCommonProxy.playerWasOnParachute()) {
			ParachuteCommonProxy.setWasOnParachute(false);
			rider.fallDistance = 0.0f;
			rider.isCollided = false;
			event.setCanceled(true);
		}
	}
}
