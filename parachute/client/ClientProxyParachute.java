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
package com.parachute.client;

//import com.parachute.common.AADTick;
import com.parachute.common.CommonProxyParachute;
import com.parachute.common.EntityParachute;
import com.parachute.common.KeyPressTick;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
//import cpw.mods.fml.client.FMLClientHandler;
//import cpw.mods.fml.common.event.FMLMissingMappingsEvent;

public class ClientProxyParachute extends CommonProxyParachute {

	@Override
	public void registerRenderer()
	{
		RenderingRegistry.registerEntityRenderingHandler(EntityParachute.class, new RenderParachute());
	}

	@Override
	public int addArmor(String armorName)
	{
		return RenderingRegistry.addNewArmourRendererPrefix(armorName);
	}
	
	@Override
	public void registerHandlers()
	{
		FMLCommonHandler.instance().bus().register(new KeyPressTick());
		
		// allow this mod to load if there are missing mappings
//		FMLClientHandler.instance().setDefaultMissingAction(FMLMissingMappingsEvent.Action.IGNORE);
	}

}
