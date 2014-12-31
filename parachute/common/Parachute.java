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

import cpw.mods.fml.common.FMLCommonHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraft.item.Item;
import net.minecraft.item.Item.ToolMaterial;
import net.minecraft.item.ItemArmor.ArmorMaterial;
import net.minecraft.item.ItemStack;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.*;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.util.EnumHelper;


@Mod(
		modid = Parachute.modid,
		name = Parachute.name,
		version = Parachute.mcversion
)

public class Parachute {

	static ArmorMaterial NYLON = EnumHelper.addArmorMaterial("nylon", 15, new int[] {2, 5, 4, 1}, 12); // same as CHAIN
	static ToolMaterial RIPSTOP = EnumHelper.addToolMaterial("ripstop", 0, 59, 2.0F, 0, 15); // same as WOOD
//	public static final PacketPipeline packetPipeline = new PacketPipeline();

	public static final String modid = "parachutemod";
	public static final String mcversion = "1.7.10";
	public static final String channel = modid;
	public static final String name = "Parachute Mod";
	public static final String parachuteName = "Parachute";
	public static final String ripcordName = "Ripcord";
	public static final String aadName = "AutoActivationDevice";
	public static final String hopnpopName = "HopAndPop";

	private String type = parachuteName; // defaults to the normal parachute
	private boolean singleUse = false; // applies to the hop and pop chute only
	private int heightLimit = 256;
	private String chuteColor = "random";
	private boolean thermals = true;
	private double AADAltitude = 15.0;
	private boolean smallCanopy = true;
	private boolean AADActive = false;
	private boolean autoDismount = true;
	private double fallThreshold = 5.0;
	private boolean weatherAffectsDrift;
	private boolean lavaThermals;
	private double minLavaDistance;
	private double maxLavaDistance;
	private boolean allowTurbulence;
	private boolean showContrails;
	private final int entityID = EntityRegistry.findGlobalUniqueEntityId();
	private static final int armorType = 1; // armor type: 0 = helmet, 1 = chestplate, 2 = legs. 3 = boots
	public static final int armorSlot = 2;  // armor slot: 0 = ??, 1 = ??, 2 = chestplate, 3 = ??

	@SidedProxy(
			clientSide = "com.parachute.client.ClientProxyParachute",
			serverSide = "com.parachute.common.CommonProxyParachute"
	)
	public static CommonProxyParachute proxy;

	public static ItemParachute parachuteItem;
	public static ItemHopAndPop hopnpopItem;
	public static ItemRipCord ripcordItem;
	public static ItemAutoActivateDevice aadItem;

	@Mod.Instance(modid)
	public static Parachute instance;

	@EventHandler
	public void preInit(FMLPreInitializationEvent event)
	{
		String generalComments = Parachute.name + " Config\nMichael Sheppard (crackedEgg)"
				+ " For Minecraft Version " + Parachute.mcversion + "\n";
		String usageComment = "singleUse - set to true for hop n pop single use (false)";
		String heightComment = "heightLimit  - 0 (zero) disables altitude limiting (256)";
		String thermalComment = "allowThermals - true|false enable/disable thermals (true)";
		String lavaThermalComment = "use lava heat to get thermals to rise up, disables space bar thermals (false)";
		String minLavaDistanceComment = "minimum distance from lava to grab thermals, if you\n"
				+ "go less than 3.0 you will most likely dismount in the lava! (3.0)";
		String maxLavaDistanceComment = "maximum distance to rise from lava thermals (48)";
		String aadAltitudeComment = "AADAltitude - altitude (in meters) at which auto deploy occurs (10)";
		String fallThresholdComment = "fallThreshold - player must have fallen this far to activate AAD (5.0)";
		String aaDActiveComment = "AADActive - whether the AAD is active or not. default is inactive. (false)";
		String typeComment = "smallCanopy - set to true to use the smaller 3 panel canopy, false for the\nlarger 4 panel canopy (true)";
		String autoComment = "If true the parachute will dismount the player automatically,\n"
				+ "if false the player has to use LSHIFT to dismount the parachute.";
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

		Configuration config = new Configuration(event.getSuggestedConfigurationFile());
		config.load();

		singleUse = config.get(Configuration.CATEGORY_GENERAL, "singleUse", false, usageComment).getBoolean(false);
		heightLimit = config.get(Configuration.CATEGORY_GENERAL, "heightLimit", 256, heightComment).getInt();
		lavaThermals = config.get(Configuration.CATEGORY_GENERAL, "lavaThermals", false, lavaThermalComment).getBoolean(false);
		minLavaDistance = config.get(Configuration.CATEGORY_GENERAL, "minLavaDistance", 3.0, minLavaDistanceComment).getDouble(3.0);
		maxLavaDistance = config.get(Configuration.CATEGORY_GENERAL, "maxLavaDistance", 48.0, maxLavaDistanceComment).getDouble(48.0);
		thermals = config.get(Configuration.CATEGORY_GENERAL, "allowThermals", true, thermalComment).getBoolean(true);
		fallThreshold = config.get(Configuration.CATEGORY_GENERAL, "fallThreshold", 5.0, fallThresholdComment).getDouble(5.0);
		AADAltitude = config.get(Configuration.CATEGORY_GENERAL, "AADAltitude", 15.0, aadAltitudeComment).getDouble(15.0);
		AADActive = config.get(Configuration.CATEGORY_GENERAL, "AADActive", false, aaDActiveComment).getBoolean(false);
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

		config.addCustomCategoryComment(Configuration.CATEGORY_GENERAL, generalComments);

		config.save();

		// clamp the fallThreshold to a minimum of 2
		fallThreshold = fallThreshold < 2 ? 2 : fallThreshold;

		int chuteID = proxy.addArmor(parachuteName.toLowerCase());
		EntityRegistry.registerModEntity(EntityParachute.class, parachuteName, entityID, this, 64, 10, true);

		// create new items, set unlocalized names and register
		parachuteItem = (ItemParachute) (new ItemParachute(NYLON, chuteID, armorType)).setUnlocalizedName(parachuteName);
		parachuteItem.setTextureName(Parachute.modid + ":" + parachuteName);
		GameRegistry.registerItem(parachuteItem, parachuteName);

		ripcordItem = (ItemRipCord) (new ItemRipCord()).setUnlocalizedName(ripcordName);
		ripcordItem.setTextureName(Parachute.modid + ":" + ripcordName);
		GameRegistry.registerItem(ripcordItem, ripcordName);

		aadItem = (ItemAutoActivateDevice) (new ItemAutoActivateDevice()).setUnlocalizedName(aadName);
		aadItem.setTextureName(Parachute.modid + ":" + aadName);
		GameRegistry.registerItem(aadItem, aadName);

		hopnpopItem = (ItemHopAndPop) (new ItemHopAndPop(RIPSTOP)).setUnlocalizedName(hopnpopName);
		hopnpopItem.setTextureName(Parachute.modid + ":" + hopnpopName);
		GameRegistry.registerItem(hopnpopItem, hopnpopName);

		proxy.registerRenderer();
		PacketHandler.init();
	}

	@EventHandler
	public void Init(FMLInitializationEvent event)
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

		// used to repair the parachutes
		NYLON.customCraftingMaterial = Items.string;
		RIPSTOP.customCraftingMaterial = Items.string;

		proxy.registerHandlers();

		FMLCommonHandler.instance().bus().register(new AADTick());
		MinecraftForge.EVENT_BUS.register(new PlayerFallEvent());

//		packetPipeline.initialise();
//		packetPipeline.registerPacket(ParachutePacket.class);

		instance = this;
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent event)
	{
//		packetPipeline.postInitialise();
	}

	public String getVersion()
	{
		return Parachute.mcversion;
	}

	public double getMaxAltitude()
	{
		return heightLimit;
	}

	public boolean getAllowThermals()
	{
		return thermals;
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

	public String getChuteColor()
	{
		return chuteColor;
	}

	public double getAADAltitude()
	{
		return AADAltitude;
	}

	public boolean getAADActive()
	{
		return AADActive;
	}

	public double getFallThreshold()
	{
		return fallThreshold;
	}

	public void setAADActive(boolean active)
	{
		AADActive = active;
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
