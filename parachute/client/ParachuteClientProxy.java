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

import com.parachute.common.ParachuteCommonProxy;
import com.parachute.common.EntityParachute;
import com.parachute.common.KeyPressTick;
import static com.parachute.common.Parachute.aadItem;
//import static com.parachute.common.Parachute.aadName;
import static com.parachute.common.Parachute.hopnpopItem;
//import static com.parachute.common.Parachute.hopnpopName;
import static com.parachute.common.Parachute.modid;
import static com.parachute.common.Parachute.parachuteItem;
//import static com.parachute.common.Parachute.parachuteName;
import static com.parachute.common.Parachute.ripcordItem;
//import static com.parachute.common.Parachute.ripcordName;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemModelMesher;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.FMLCommonHandler;

public class ParachuteClientProxy extends ParachuteCommonProxy {
	
	@Override
	public void preInit()
	{
		super.preInit();
		info(modid + " ConbinedClient preInit is complete.");
	}
	
	@Override
	public void Init()
	{
		super.Init();
		RenderManager rm = Minecraft.getMinecraft().getRenderManager();
		RenderingRegistry.registerEntityRenderingHandler(EntityParachute.class, new RenderParachute(rm));
		
		FMLCommonHandler.instance().bus().register(new KeyPressTick());
		
		ItemModelMesher mm = Minecraft.getMinecraft().getRenderItem().getItemModelMesher();
		mm.register(parachuteItem, 0, new ModelResourceLocation(modid + ":" + parachuteName, "inventory"));
		mm.register(ripcordItem, 0, new ModelResourceLocation(modid + ":" + ripcordName, "inventory"));
		mm.register(hopnpopItem, 0, new ModelResourceLocation(modid + ":" + hopnpopName, "inventory"));
		
		ModelBakery.addVariantName(aadItem, new String[] {modid + ":" + aadName, modid + ":" + aadName + "_off"});
		mm.register(aadItem, 1, new ModelResourceLocation(modid + ":" + aadName, "inventory"));
		mm.register(aadItem, 0, new ModelResourceLocation(modid + ":" + aadName + "_off", "inventory"));
		
		info(modid + " ConbinedClient Init is complete.");
	}
	
	@Override
	public void postInit()
	{
		info(modid + " ConbinedClient postInit is complete.");
	}

}
