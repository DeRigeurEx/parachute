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

package parachute.common;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.item.EnumToolMaterial;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.EnumHelper;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.registry.*;
import cpw.mods.fml.common.SidedProxy;

@Mod (
	modid = Parachute.ID,
	name = Parachute.name,
	version = Parachute.VER
)

@NetworkMod (
	versionBounds = "[1.6.2]",
	clientSideRequired = true,
	serverSideRequired = false,
	packetHandler = ParachutePacketHandler.class,
	connectionHandler = ParachutePacketHandler.class,
	channels = {Parachute.CHANNEL}
)

public class Parachute {
	
	static EnumToolMaterial NYLON = EnumHelper.addToolMaterial("nylon", 0, 30, 2.0F, 0, 15);
	
	public static final String ID = "ParachuteMod";
	public static final String VER = "1.6.2";
	public static final String CHANNEL = ID;
	public static final String name = "Parachute Mod";
	public static final String entityName = "Parachute";
	
	private int heightLimit;
	private int chuteColor;
	private boolean thermals;
	private boolean autoDeploy;
	private int fallDistance;
	private boolean smallCanopy;
//	private boolean useTexturePack;
	private static int itemID;
	private int entityID = EntityRegistry.findGlobalUniqueEntityId();
	private Minecraft mcinstance;

	@SidedProxy (
		clientSide = "parachute.client.ClientProxyParachute",
		serverSide = "parachute.common.CommonProxyParachute"
	)
	public static CommonProxyParachute proxy;

	public static Item parachuteItem;
	
	@Instance
	public static Parachute instance;

	public Parachute() {

	}
	
	@EventHandler
    public void preLoad(FMLPreInitializationEvent event) {
		String generalComments = Parachute.name + " Config\nMichael Sheppard (crackedEgg)";
//		String textureComment = "useTexturePack - use the texture pack textures instead of the\n"
//				+ "built in textures (false)";
		String itemComment = "itemID - customize the ItemID (2500)";
		String heightComment = "heightLimit  - 0 (zero) disables altitude limiting (225)";
		String thermalComment = "allowThermals - true|false enable/disable thermals (true)";
		String deployComment = "autoDeploy - true|false enable/disable auto parachute deployment (false)";
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
		autoDeploy = config.get(Configuration.CATEGORY_GENERAL, "autoDeploy", false, deployComment).getBoolean(false);
		fallDistance = config.get(Configuration.CATEGORY_GENERAL, "fallDistance", 5, fallComment).getInt();
		itemID = config.get(Configuration.CATEGORY_GENERAL, "itemID", 2500, itemComment).getInt();
		smallCanopy = config.get(Configuration.CATEGORY_GENERAL, "smallCanopy", false, typeComment).getBoolean(false);
//		useTexturePack = config.get(Configuration.CATEGORY_GENERAL, "useTexturepack", false, textureComment).getBoolean(false);
		
		// fix fallDistance  (2 > fallDistance < 20)
		fallDistance = (fallDistance < 2) ? 2 : (fallDistance > 20) ? 20 : fallDistance;
		config.addCustomCategoryComment(Configuration.CATEGORY_GENERAL, generalComments);
		
		config.save();
		
		proxy.registerRenderer();
		proxy.registerKeyHandler(); // for keyboard control of parachute
		
		// only register tick handler if autoDeploy is enabled
		if (autoDeploy) {
			proxy.registerServerTickHandler(); // for auto deployment feature
		}
		
    }

	@EventHandler
	public void load(FMLInitializationEvent event) {
		EntityRegistry.registerModEntity(EntityParachute.class, entityName, entityID, this, 64, 10, true);
		parachuteItem = new ItemParachute(itemID, NYLON).setUnlocalizedName(entityName);
		parachuteItem.func_111206_d("parachute");
		
		GameRegistry.addRecipe(new ItemStack(parachuteItem, 1), new Object[] {
			"###", "X X", " L ", '#', Block.cloth, 'X', Item.silk, 'L', Item.leather
		});
		
		LanguageRegistry.addName(parachuteItem, entityName);
		NetworkRegistry.instance().registerConnectionHandler(new ParachutePacketHandler());
		
		mcinstance = FMLClientHandler.instance().getClient();
		
		instance = this;
	}
	
	public String getVersion() {
		return Parachute.VER;
	}
	
	public double getMaxAltitude() {
		return heightLimit;
	}
	
	public boolean getAllowThermals() {
		return thermals;
	}
	
	public boolean getAutoDeploy() {
		return autoDeploy;
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
	
//	public boolean getTextureRule() {
//		return useTexturePack;
//	}

	public static int getItemID() {
		return itemID;
	}
}
