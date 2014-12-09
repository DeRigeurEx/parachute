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

//import net.minecraft.client.Minecraft;
//import net.minecraft.client.renderer.ItemModelMesher;
//import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraft.item.Item;
import net.minecraft.item.Item.ToolMaterial;
import net.minecraft.item.ItemArmor.ArmorMaterial;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.*;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraftforge.common.config.Configuration;
//import net.minecraftforge.common.util.EnumHelper;


@Mod(
		modid = Parachute.modid,
		name = Parachute.name,
		version = Parachute.mcversion
)

public class Parachute {

	static ArmorMaterial NYLON = ArmorMaterial.CHAIN; //addArmorMaterial("nylon", 15, new int[] {2, 5, 4, 1}, 12); // same as CHAIN
	static ToolMaterial RIPSTOP = ToolMaterial.WOOD; //EnumHelper.addToolMaterial("ripstop", 0, 59, 2.0F, 0, 15); // same as WOOD
//	public static final PacketPipeline packetPipeline = new PacketPipeline();

	public static final String modid = "parachutemod";
	public static final String mcversion = "1.8.0";
	public static final String channel = modid;
	public static final String name = "Parachute Mod";
	public static final String parachuteName = "parachute";
	public static final String ripcordName = "ripcord";
	public static final String aadName = "auto_activation_device";
	public static final String hopnpopName = "hop_and_pop";

	private String type = parachuteName; // defaults to the normal parachute
	private boolean singleUse = false; // applies to the hop and pop chute only
	private int heightLimit = 256;
	private String chuteColor = "random";
	private boolean thermals = true;
	private static double AADAltitude = 15.0;
	private boolean smallCanopy = true;
	private static boolean AADActive = false;
	private static boolean autoDismount = true;
	private static double fallThreshold = 5.0;
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
		String aadAltitudeComment = "AADAltitude - altitude (in meters) at which auto deploy occurs (10)";
		String fallThresholdComment = "fallThreshold - player must have fallen this far to activate AAD (5.0)";
		String aaDActiveComment = "AADActive - whether the AAD is active or not. default is inactive. (false)";
		String typeComment = "smallCanopy - set to true to use the smaller 3 panel canopy, false for the\nlarger 4 panel canopy (true)";
		String autoComment = "If true the parachute will dismount the player automatically,\nif false the player has to use LSHIFT to dismount the arachute.";
		String colorComment = "Parachute Colors Allowed:\n"
				+ "black\nblue\n"
				+ "brown\ncyan\n"
				+ "gray\ngreen\n"
				+ "light_blue\nlime\n"
				+ "magenta\norange\n"
				+ "pink\npurple\n"
				+ "red\nsilver\n"
				+ "white\nyellow\n"
				+ "random - allows randomly chosen color each time chute is opened\n";

		Configuration config = new Configuration(event.getSuggestedConfigurationFile());
		config.load();

		singleUse = config.get(Configuration.CATEGORY_GENERAL, "singleUse", false, usageComment).getBoolean(false);
		heightLimit = config.get(Configuration.CATEGORY_GENERAL, "heightLimit", 256, heightComment).getInt();
		thermals = config.get(Configuration.CATEGORY_GENERAL, "allowThermals", true, thermalComment).getBoolean(true);
		fallThreshold = config.get(Configuration.CATEGORY_GENERAL, "fallThreshold", 5.0, fallThresholdComment).getDouble(5.0);
		AADAltitude = config.get(Configuration.CATEGORY_GENERAL, "AADAltitude", 15.0, aadAltitudeComment).getDouble(15.0);
		AADActive = config.get(Configuration.CATEGORY_GENERAL, "AADActive", false, aaDActiveComment).getBoolean(false);
		smallCanopy = config.get(Configuration.CATEGORY_GENERAL, "smallCanopy", true, typeComment).getBoolean(true);
		autoDismount = config.get(Configuration.CATEGORY_GENERAL, "autoDismount", true, autoComment).getBoolean(true);
		chuteColor = config.get(Configuration.CATEGORY_GENERAL, "chuteColor", "random", colorComment).getString();

		config.addCustomCategoryComment(Configuration.CATEGORY_GENERAL, generalComments);

		config.save();

		// clamp the fallThreshold to a minimum of 2
		fallThreshold = fallThreshold < 2 ? 2 : fallThreshold;

		int chuteID = proxy.addArmor(parachuteName.toLowerCase());
		EntityRegistry.registerModEntity(EntityParachute.class, parachuteName, entityID, this, 80, 3, true); // orig: 64, 10
		
		// create new items, set unlocalized names and register
		parachuteItem = (ItemParachute) (new ItemParachute(NYLON, chuteID, armorType)).setUnlocalizedName(parachuteName);
		GameRegistry.registerItem(parachuteItem, parachuteName);

		ripcordItem = (ItemRipCord) (new ItemRipCord()).setUnlocalizedName(ripcordName);
		GameRegistry.registerItem(ripcordItem, ripcordName);

		aadItem = (ItemAutoActivateDevice) (new ItemAutoActivateDevice()).setUnlocalizedName(aadName);
		GameRegistry.registerItem(aadItem, aadName);

		hopnpopItem = (ItemHopAndPop) (new ItemHopAndPop(RIPSTOP)).setUnlocalizedName(hopnpopName);
		GameRegistry.registerItem(hopnpopItem, hopnpopName);

//		proxy.registerRenderer();
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
		
		proxy.registerRenderer();

		proxy.registerHandlers();

		FMLCommonHandler.instance().bus().register(new AADTick());
		MinecraftForge.EVENT_BUS.register(new PlayerFallEvent());
		
		proxy.registerResources();
		
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

	public String getChuteColor()
	{
		return chuteColor;
	}

	public static double getAADAltitude()
	{
		return AADAltitude;
	}

	public static boolean getAADActive()
	{
		return AADActive;
	}

	public static double getFallThreshold()
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
	
//	public static ArmorMaterial addArmorMaterial(String name, int durability, int enchantability)
//	{
//        return EnumHelper.addEnum(ArmorMaterial.class, name, durability/14, new int[]{0, 0, 0, 0}, enchantability);
//    }
	
}
