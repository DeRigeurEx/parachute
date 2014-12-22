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

import net.minecraft.block.Block;
import net.minecraftforge.fml.common.FMLLog;
import org.apache.logging.log4j.Logger;

public class CommonProxyParachute {
	
	private static final Logger logger = FMLLog.getLogger();
	
	public void registerRenderer() {}
	
	public void registerHandlers() {}

	public int addArmor(String armorName)
	{
		return 0;
	}
	
	public void info(String s)
	{
		logger.info(s);
	}
	
	public void warn(String s)
	{
		logger.warn(s);
	}
	
	public void registerResources() {}
	
    public void registerBlockTexture(final Block block, final String blockName) {}

    public void registerBlockTexture(final Block block, final String blockName, int meta) {}

}
