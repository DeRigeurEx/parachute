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

import static com.parachute.common.Parachute.aadItem;
import static com.parachute.common.Parachute.hopnpopItem;
import static com.parachute.common.Parachute.parachuteItem;
import static com.parachute.common.Parachute.ripcordItem;
import java.io.File;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import org.apache.logging.log4j.Logger;

public class ParachuteCommonProxy {
	
	private static final Logger logger = FMLLog.getLogger();
	private final int entityID = EntityRegistry.findGlobalUniqueEntityId();
	static ItemArmor.ArmorMaterial NYLON = EnumHelper.addArmorMaterial("nylon", "", 15, new int[] {2, 5, 4, 1}, 12); // same as CHAIN
	static Item.ToolMaterial RIPSTOP = EnumHelper.addToolMaterial("ripstop", 0, 59, 2.0F, 0, 15); // same as WOOD
	private static final int armorType = 1; // armor type: 0 = helmet, 1 = chestplate, 2 = legs,       3 = boots
	public static final int armorSlot = 2;  // armor slot: 0 = ??,     1 = ??,         2 = chestplate, 3 = ??
	public static final String hopnpopName = "hop_and_pop";
	public static final String parachuteName = "parachute";
	public static final String ripcordName = "ripcord";
	public static final String aadName = "auto_activation_device";
	
	private String type = parachuteName; // defaults to the normal parachute
	private boolean singleUse = false; // applies to the hop and pop chute only
	private int heightLimit = 256;
	private String chuteColor = "random";
	private boolean thermals = true;
	private double AADAltitude = 15.0;
	private boolean smallCanopy = true;
	private boolean autoDismount = true;
	private double fallThreshold = 5.0;
	private boolean weatherAffectsDrift;
	private boolean lavaThermals;
	private double minLavaDistance;
	private double maxLavaDistance;
	private boolean allowTurbulence;
	private boolean showContrails;
	
	public void preInit()
	{
		readConfigInfo(Parachute.instance.getConfigFile());
		
		EntityRegistry.registerModEntity(EntityParachute.class, parachuteName, entityID, Parachute.instance, 80, 20, true);
		
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
	
	public void readConfigInfo(File configFile)
	{
		String generalComments = Parachute.name + " Config\nMichael Sheppard (crackedEgg)"
				+ " For Minecraft Version " + Parachute.modversion + "\n";
		String usageComment = "set to true for hop-n-pop single use (false)";
		String heightComment = "0 (zero) disables altitude limiting (256)";
		String thermalComment = "true|false enable/disable thermals (true)";
		String lavaThermalComment = "use lava heat to get thermals to rise up, disables space bar thermals (false)";
		String minLavaDistanceComment = "minimum distance from lava to grab thermals, if you\n"
				+ "go less than 3.0 you will most likely dismount in the lava! (3.0)";
		String maxLavaDistanceComment = "maximum distance to rise from lava thermals (48)";
		String aadAltitudeComment = "altitude (in meters) at which auto deploy occurs (10)";
		String fallThresholdComment = "player must have fallen this far to activate AAD (5.0)";
		String typeComment = "set to true to use the smaller 3 panel canopy, false for the\nlarger 4 panel canopy (true)";
		String autoComment = "If true the parachute will dismount the player automatically,\n"
				+ "if false the player has to use LSHIFT to dismount the parachute. (true)";
		String weatherComment = "set to false if you don't want the drift rate to be affected by bad weather (true)";
		String turbulenceComment = "set to true to feel the turbulent world of Minecraft (false)";
		String trailsComment = "set to true to show contrails from parachute (false)";
		String colorComment = "Parachute Colors Allowed:\n"
				+ "black\nblue\n"
				+ "brown\ncyan\n"
				+ "gray\ngreen\n"
				+ "light_blue\nlime\n"
				+ "magenta\norange\n"
				+ "pink\npurple\n"
				+ "red\nsilver\n"
				+ "white\nyellow\n"
				+ "random - allows randomly chosen color each time chute is opened\n"
				+ "custom[0-9] - allows use of a custom texture called 'custom' with a single number appended";

		Configuration config = new Configuration(configFile);
		config.load();

		singleUse = config.get(Configuration.CATEGORY_GENERAL, "singleUse", false, usageComment).getBoolean(false);
		heightLimit = config.get(Configuration.CATEGORY_GENERAL, "heightLimit", 256, heightComment).getInt();
		thermals = config.get(Configuration.CATEGORY_GENERAL, "allowThermals", true, thermalComment).getBoolean(true);
		lavaThermals = config.get(Configuration.CATEGORY_GENERAL, "lavaThermals", false, lavaThermalComment).getBoolean(false);
		minLavaDistance = config.get(Configuration.CATEGORY_GENERAL, "minLavaDistance", 3.0, minLavaDistanceComment).getDouble(3.0);
		maxLavaDistance = config.get(Configuration.CATEGORY_GENERAL, "maxLavaDistance", 48.0, maxLavaDistanceComment).getDouble(48.0);
		fallThreshold = config.get(Configuration.CATEGORY_GENERAL, "fallThreshold", 5.0, fallThresholdComment).getDouble(5.0);
		AADAltitude = config.get(Configuration.CATEGORY_GENERAL, "AADAltitude", 15.0, aadAltitudeComment).getDouble(15.0);
		smallCanopy = config.get(Configuration.CATEGORY_GENERAL, "smallCanopy", true, typeComment).getBoolean(true);
		autoDismount = config.get(Configuration.CATEGORY_GENERAL, "autoDismount", true, autoComment).getBoolean(true);
		chuteColor = config.get(Configuration.CATEGORY_GENERAL, "chuteColor", "random", colorComment).getString();
		weatherAffectsDrift = config.get(Configuration.CATEGORY_GENERAL, "weatherAffectsDrift", true, weatherComment).getBoolean(true);
		allowTurbulence = config.get(Configuration.CATEGORY_GENERAL, "allowTurbulence", false, turbulenceComment).getBoolean(false);
		showContrails = config.get(Configuration.CATEGORY_GENERAL, "showContrails", false, trailsComment).getBoolean(false);
		
		// if using lava thermals disable space bar thermals, clamp the minimum lava distance.
		if (lavaThermals) {
			thermals = false;
			minLavaDistance = minLavaDistance < 2.0 ? 2.0 : minLavaDistance;
		}
		// clamp the fallThreshold to a minimum of 2
		fallThreshold = fallThreshold < 2.0 ? 2.0 : fallThreshold;

		config.addCustomCategoryComment(Configuration.CATEGORY_GENERAL, generalComments);

		config.save();
	}
	
	public void info(String s)
	{
		logger.info(s);
	}
	
	public void warn(String s)
	{
		logger.warn(s);
	}
	
	public double getMaxAltitude()
	{
		return heightLimit;
	}

	public boolean getAllowThermals()
	{
		return thermals;
	}

	public String getChuteColor()
	{
		return chuteColor;
	}

	public double getAADAltitude()
	{
		return AADAltitude;
	}

	public double getFallThreshold()
	{
		return fallThreshold;
	}
	
	public boolean getAllowLavaThermals()
	{
		return lavaThermals;
	}
	
	public boolean getWeatherAffectsDrift()
	{
		return weatherAffectsDrift;
	}
	
	public double getMinLavaDistance()
	{
		return minLavaDistance;
	}
	
	public double getMaxLavaDistance()
	{
		return maxLavaDistance;
	}
	
	public boolean getAllowturbulence()
	{
		return allowTurbulence;
	}
	
	public boolean getShowContrails()
	{
		return showContrails;
	}

	public boolean isSmallCanopy()
	{
		return smallCanopy;
	}
	
	public boolean isAutoDismount()
	{
		return autoDismount;
	}

	public void setType(String type)
	{
		this.type = type;
		// force a small canopy for the hop-n-pop chute
		if (this.type.equals(hopnpopName)) {
			smallCanopy = true;
		}
	}

	public String getType()
	{
		return this.type;
	}

	public int getHopAndPopDamageAmount()
	{
		if (singleUse) {
			return hopnpopItem.getMaxDamage() + 1;
		}
		return 1;
	}
	
	public static boolean playerIsWearingParachute(EntityPlayer player)
	{
		ItemStack stack = player == null ? null : player.getCurrentArmor(armorSlot);
		if (stack != null) {
			Item item = stack.getItem();
			if (item != null && item instanceof ItemParachute) {
				return true;
			}
		}
		return false;
	}

	public static boolean isFalling(EntityPlayer entity)
	{
		return (entity.fallDistance > 0.0F && !entity.onGround && !entity.isOnLadder());
	}
}
