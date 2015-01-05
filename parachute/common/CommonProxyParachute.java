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

import static com.parachute.common.Parachute.NYLON;
import static com.parachute.common.Parachute.RIPSTOP;
import static com.parachute.common.Parachute.aadItem;
//import static com.parachute.common.Parachute.aadName;
import static com.parachute.common.Parachute.hopnpopItem;
//import static com.parachute.common.Parachute.hopnpopName;
import static com.parachute.common.Parachute.parachuteItem;
//import static com.parachute.common.Parachute.parachuteName;
import static com.parachute.common.Parachute.ripcordItem;
//import static com.parachute.common.Parachute.ripcordName;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import org.apache.logging.log4j.Logger;

public class CommonProxyParachute {
	
	private static final Logger logger = FMLLog.getLogger();
	private final int entityID = EntityRegistry.findGlobalUniqueEntityId();
	private static final int armorType = 1; // armor type: 0 = helmet, 1 = chestplate, 2 = legs,       3 = boots
	public static final String hopnpopName = "hop_and_pop";
	public static final String parachuteName = "parachute";
	public static final String ripcordName = "ripcord";
	public static final String aadName = "auto_activation_device";
	
	public void preInit()
	{
		Parachute.instance.readConfigInfo(Parachute.instance.getConfigFile());
		
		EntityRegistry.registerModEntity(EntityParachute.class, parachuteName, entityID, Parachute.instance, 80, 3, true);
		
		final int renderIndex = 0;
		parachuteItem = (ItemParachute) (new ItemParachute(NYLON, renderIndex, armorType)).setUnlocalizedName(parachuteName);
		GameRegistry.registerItem(parachuteItem, parachuteName);

		ripcordItem = (ItemRipCord) (new ItemRipCord()).setUnlocalizedName(ripcordName);
		GameRegistry.registerItem(ripcordItem, ripcordName);

		aadItem = (ItemAutoActivateDevice) (new ItemAutoActivateDevice()).setUnlocalizedName(aadName);
		GameRegistry.registerItem(aadItem, aadName);

		hopnpopItem = (ItemHopAndPop) (new ItemHopAndPop(RIPSTOP)).setUnlocalizedName(hopnpopName);
		GameRegistry.registerItem(hopnpopItem, hopnpopName);
		
		PacketHandler.init();
	}
	
	public void Init()
	{
		// recipes to craft the parachutes, ripcord and AAD
		GameRegistry.addRecipe(new ItemStack(parachuteItem, 1), new Object[] {
			"###", "X X", " L ", '#', Blocks.wool, 'X', Items.string, 'L', Items.leather
		});

		GameRegistry.addRecipe(new ItemStack(hopnpopItem, 1), new Object[] {
			"###", "X X", " X ", '#', Blocks.wool, 'X', Items.string
		});

		GameRegistry.addRecipe(new ItemStack(ripcordItem, 1), new Object[] {
			"#  ", " # ", "  *", '#', Items.string, '*', Items.iron_ingot
		});

		GameRegistry.addRecipe(new ItemStack(aadItem, 1), new Object[] {
			" * ", " % ", " # ", '*', Items.comparator, '%', Items.redstone, '#', ripcordItem});
		
		FMLCommonHandler.instance().bus().register(new AADTick());
		MinecraftForge.EVENT_BUS.register(new PlayerFallEvent());
	}
	
	public void postInit()
	{
		
	}
	
	public void info(String s)
	{
		logger.info(s);
	}
	
	public void warn(String s)
	{
		logger.warn(s);
	}
}
