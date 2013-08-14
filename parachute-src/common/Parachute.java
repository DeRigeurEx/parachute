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
// Copyright 2013 Michael Sheppard (crackedEgg)
//
package parachute.common;

import net.minecraft.block.Block;
//import net.minecraft.item.EnumToolMaterial;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.EnumHelper;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.registry.*;
import cpw.mods.fml.common.SidedProxy;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumArmorMaterial;

@Mod (
	modid = Parachute.modid,
	name = Parachute.name,
	version = Parachute.version
)

@NetworkMod (
	versionBounds = "[" + Parachute.version + "]",
	clientSideRequired = true,
	serverSideRequired = false,
	packetHandler = ParachutePacketHandler.class,
	connectionHandler = ParachutePacketHandler.class,
	channels = {Parachute.channel}
)

public class Parachute {
	
    static EnumArmorMaterial NYLON = EnumHelper.addArmorMaterial("nylon", 5, new int[]{1, 1, 1, 1}, 15);
	
	public static final String modid = "ParachuteMod";
	public static final String version = "1.6.2";
	public static final String channel = modid;
	public static final String name = "Parachute Mod";
	public static final String entityName = "Parachute";
    public static final String ripcordName = "Ripcord";
    public static final String aadName = "AutoActivationDevice";
	
	private int heightLimit;
	private int chuteColor;
	private boolean thermals;
	private int fallDistance;
	private boolean smallCanopy;
	private static int parachuteID;
    private static int ripcordID;
    private static int aadID;
	private int entityID = EntityRegistry.findGlobalUniqueEntityId();
    private static final int armorType = 1; // armor type: 0 = helmet, 1 = chestplate, 2 = legs. 3 = boots
    public static final int armorSlot = 2;  // armor slot: 0 = ??, 1 = ??, 2 = chestplate, 3 = ??

	@SidedProxy (
		clientSide = "parachute.client.ClientProxyParachute",
		serverSide = "parachute.common.CommonProxyParachute"
	)
	public static CommonProxyParachute proxy;

	public static ItemParachute parachuteItem;
    public static ItemRipCord ripcordItem;
    public static ItemAutoActivateDevice aadItem;
	
	@Instance
	public static Parachute instance;

	public Parachute() {

	}
	
	@EventHandler
    public void preInit(FMLPreInitializationEvent event) {
		String generalComments = Parachute.name + " Config\nMichael Sheppard (crackedEgg)";
		String itemComment = "itemID - customize the Item ID (2500)";
        String cordComment = "ripcordID - customize the Ripcord Item ID (2501)";
        String aadComment = "auto activation device ID - customize the AAD Item ID (2502)";
		String heightComment = "heightLimit  - 0 (zero) disables altitude limiting (225)";
		String thermalComment = "allowThermals - true|false enable/disable thermals (true)";
		String fallComment = "fallDistance - maximum falling distance before auto deploy (2 - 20) (5)";
		String typeComment = "smallCanopy - set to true to use the smaller 3 panel canopy, false for the\n"
							+ "larger 4 panel canopy (false)";
		String colorComment = "Color index numbers:\n"
							+ "black        -  0\nblue         -  1\n"
							+ "brown        -  2\ncyan         -  3\n"
							+ "gray         -  4\ngreen        -  5\n"
							+ "light blue   -  6\nlime         -  7\n"
							+ "magneta      -  8\norange       -  9\n"
							+ "pink         - 10\npurple       - 11\n"
							+ "red          - 12\nsilver       - 13\n"
							+ "white        - 14\nyellow       - 15\n"
							+ "blue/white   - 16\nred/white    - 17\n"
							+ "yellow/green - 18";
		
		Configuration config = new Configuration(event.getSuggestedConfigurationFile());
		config.load();
		
		heightLimit = config.get(Configuration.CATEGORY_GENERAL, "heightLimit", 225, heightComment).getInt();
		chuteColor = config.get(Configuration.CATEGORY_GENERAL, "chuteColor", 18, colorComment).getInt();
		thermals = config.get(Configuration.CATEGORY_GENERAL, "allowThermals", true, thermalComment).getBoolean(true);
		fallDistance = config.get(Configuration.CATEGORY_GENERAL, "fallDistance", 5, fallComment).getInt();
		parachuteID = config.get(Configuration.CATEGORY_GENERAL, "itemID", 2500, itemComment).getInt();
        ripcordID = config.get(Configuration.CATEGORY_GENERAL, "ripcordID", 2501, cordComment).getInt();
        ripcordID = config.get(Configuration.CATEGORY_GENERAL, "ripcordID", 2501, cordComment).getInt();
        aadID = config.get(Configuration.CATEGORY_GENERAL, "aadID", 2502, aadComment).getInt();
		smallCanopy = config.get(Configuration.CATEGORY_GENERAL, "smallCanopy", false, typeComment).getBoolean(false);
		
		// fix fallDistance must be between 2 and 20
		fallDistance = (fallDistance < 2) ? 2 : (fallDistance > 20) ? 20 : fallDistance;
		config.addCustomCategoryComment(Configuration.CATEGORY_GENERAL, generalComments);
		
		config.save();
		
		proxy.registerRenderer();
		proxy.registerKeyHandler(); // for keyboard control of parachute
		proxy.registerServerTickHandler(); // for auto deployment feature
		
    }

	@EventHandler
	public void Init(FMLInitializationEvent event) {
        int chuteID = proxy.addArmor("parachute");
		EntityRegistry.registerModEntity(EntityParachute.class, entityName, entityID, this, 64, 10, true);
        parachuteItem = (ItemParachute)(new ItemParachute(parachuteID, NYLON, chuteID, armorType)).func_111206_d("parachutemod:Parachute");
        
        // used to repair the parachute
        NYLON.customCraftingMaterial = Item.silk;
        
        ripcordItem = (ItemRipCord)(new ItemRipCord(ripcordID)).setUnlocalizedName(ripcordName);
        aadItem = (ItemAutoActivateDevice)(new ItemAutoActivateDevice(aadID)).setUnlocalizedName(aadName);
		
        GameRegistry.addRecipe(new ItemStack(parachuteItem, 1), new Object[] {
			"###", "X X", " L ", '#', Block.cloth, 'X', Item.silk, 'L', Item.leather
		});
        
        GameRegistry.addRecipe(new ItemStack(ripcordItem, 1), new Object[] {
			"#  ", " # ", "  *", '#', Item.silk, '*', Item.ingotIron
		});
        
        GameRegistry.addRecipe(new ItemStack(aadItem, 1), new Object[] {
			" * ", " % ", " # ", '*', Item.comparator, '%', Item.redstone, '#', ripcordItem,
		});
		
		LanguageRegistry.addName(parachuteItem, entityName);
        LanguageRegistry.addName(ripcordItem, ripcordName);
        LanguageRegistry.addName(aadItem, aadName);
        
		NetworkRegistry.instance().registerConnectionHandler(new ParachutePacketHandler());
		
		instance = this;
	}
	
	public String getVersion() {
		return Parachute.version;
	}
	
	public double getMaxAltitude() {
		return heightLimit;
	}
	
	public boolean getAllowThermals() {
		return thermals;
	}
	
	public int getChuteColor() {
		return chuteColor;
	}
	
	public int getFallDistance() {
		return fallDistance;
	}
	
	public boolean getCanopyType() {
		return smallCanopy;
	}
	
	public static int getItemID() {
		return parachuteID;
	}
    
    public static boolean playerIsWearingParachute(EntityPlayer player) {
    	ItemStack stack = player == null ? null : player.getCurrentArmor(armorSlot);
        if (stack != null) {
        	Item item = stack.getItem();
        	if (item != null && item instanceof ItemParachute) {
            	return true;
            }
        }
        return false;
    }
    
}
