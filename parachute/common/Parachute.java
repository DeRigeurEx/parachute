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

import java.io.File;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;


@Mod( modid = Parachute.modid, name = Parachute.name, version = Parachute.mcversion )

public class Parachute {
	
	public static final String modid = "parachutemod";
	public static final String mcversion = "1.8.0";
	public static final String name = "Parachute Mod";
	
	private File configFile;

	@SidedProxy(
			clientSide = "com.parachute.client.ParachuteClientProxy",
			serverSide = "com.parachute.common.ParachuteServerProxy"
	)
	public static ParachuteCommonProxy proxy;

	public static ItemParachute parachuteItem;
	public static ItemHopAndPop hopnpopItem;
	public static ItemRipCord ripcordItem;
	public static ItemAutoActivateDevice aadItem;

	@Mod.Instance(modid)
	public static Parachute instance;

	@EventHandler
	public void preInit(FMLPreInitializationEvent event)
	{
		configFile = event.getSuggestedConfigurationFile();
		instance = this;
		proxy.preInit();
	}

	@EventHandler
	public void Init(FMLInitializationEvent event)
	{
		proxy.Init();
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent event)
	{
		proxy.postInit();
	}
	
	public File getConfigFile()
	{
		return configFile;
	}
	
	public String getVersion()
	{
		return Parachute.mcversion;
	}

}
