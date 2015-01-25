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

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ParachuteCommonProxy {

	private static final Logger logger = LogManager.getLogger(Parachute.modid);
	private final int entityID = EntityRegistry.findGlobalUniqueEntityId();
	static ItemArmor.ArmorMaterial NYLON = EnumHelper.addArmorMaterial("nylon", "", 15, new int[] {2, 5, 4, 1}, 12); // same as CHAIN
	static Item.ToolMaterial RIPSTOP = EnumHelper.addToolMaterial("ripstop", 0, 59, 2.0F, 0, 15); // same as WOOD
	private static final int armorType = 1; // armor type: 0 = helmet, 1 = chestplate, 2 = leggings, 3 = boots
	public static final int armorSlot = 2;  // armor slot: 3 = helmet, 2 = chestplate, 1 = leggings, 0 = boots    
	public static final String parachuteName = "parachute";
	public static final String packName = "pack";
	private static boolean deployed = false;
	private static boolean wasOnParachute = false;

	public void preInit()
	{
		EntityRegistry.registerModEntity(EntityParachute.class, parachuteName, entityID, Parachute.instance, 80, 20, true);

		Parachute.parachuteItem = (ItemParachute) (new ItemParachute(RIPSTOP)).setUnlocalizedName(parachuteName);
		GameRegistry.registerItem(Parachute.parachuteItem, parachuteName);
		
		final int renderIndex = 0;
		Parachute.packItem = (ItemParachutePack) (new ItemParachutePack(NYLON, renderIndex, armorType)).setUnlocalizedName(packName);
		GameRegistry.registerItem(Parachute.packItem, packName);

		PacketHandler.init();
	}

	public void Init()
	{
		FMLCommonHandler.instance().bus().register(Parachute.instance);
		FMLCommonHandler.instance().bus().register(new PlayerTickEventHandler());

		// recipe to craft the parachute
		GameRegistry.addRecipe(new ItemStack(Parachute.parachuteItem, 1), new Object[] {
			"###", "X X", " L ", '#', Blocks.wool, 'X', Items.string, 'L', Items.leather
		});
	}

	public void postInit()
	{
		// nothing to see here...
	}

	public void info(String s)
	{
		logger.info(s);
	}

	public void warn(String s)
	{
		logger.warn(s);
	}

	public static boolean isFalling(EntityPlayer entity)
	{
		return (entity.fallDistance > 0.0F && !entity.onGround && !entity.isOnLadder());
	}

	public static boolean onParachute(EntityPlayer entity)
	{
		return entity.isRiding() && deployed;
	}

	public static boolean getDeployed()
	{
		return deployed;
	}

	public static void setDeployed(boolean isDeployed)
	{
		deployed = isDeployed;
	}
	
	public static boolean playerWasOnParachute()
	{
		return wasOnParachute;
	}
	
	public static void setWasOnParachute(boolean on)
	{
		wasOnParachute = on;
	}
}
