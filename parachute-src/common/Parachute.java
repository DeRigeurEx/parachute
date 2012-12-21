package parachute.common;

//
// Copyright 2011 Michael Sheppard (crackedEgg)
//

import java.util.*;

import parachute.common.EntityParachute;
import net.minecraft.block.Block;
import net.minecraft.item.EnumToolMaterial;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.Configuration;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.Init;
import cpw.mods.fml.common.Mod.PreInit;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.Mod.Instance;

import cpw.mods.fml.common.registry.*;
import cpw.mods.fml.common.SidedProxy;

// mod info, version
interface ModInfo {
	public final static String version = "1.4.6";
	public final static String channel = "Parachute";
}

@Mod (
	modid = "ParachuteMod",
	name = "Parachute Mod",
	version = ModInfo.version
)

@NetworkMod (
	clientSideRequired = true,
	serverSideRequired = true,
	packetHandler = ParachutePacketHandler.class,
	connectionHandler = ParachutePacketHandler.class,
	channels = {ModInfo.channel}
)

public class Parachute
{
	private int heightLimit;
	private int chuteColor;
	private boolean thermals;
	private boolean autoDeploy;
	private int fallDistance;
	private static int itemID;
	private int entityID = EntityRegistry.findGlobalUniqueEntityId();

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
	
	@PreInit
    public void preLoad(FMLPreInitializationEvent event) {
		String comments = " Parachute Mod Config\n"
				+ " Michael Sheppard (crackedEgg)\n\n"
				+ " itemID - customize the ItemID (2500)\n"
				+ " heightLimit  - 0 (zero) disables altitude limiting (225)\n"
				+ " thermals - true|false enable/disable thermals (true)\n"
				+ " autoDeploy - true|false enable/disable auto parachute deployment (false)\n"
				+ " fallDistance - maximum falling distance before auto deploy (2 - 20) (5)\n"
				+ "\n"
				+ " Color index numbers:\n" + " random     - -1\n"
				+ " black      -  0\n" + " red        -  1\n"
				+ " green      -  2\n" + " brown      -  3\n"
				+ " blue       -  4\n" + " purple     -  5\n"
				+ " cyan       -  6\n" + " light grey -  7\n"
				+ " dark grey  -  8\n" + " magenta    -  9\n"
				+ " lime       - 10\n" + " yellow     - 11\n"
				+ " light blue - 12\n" + " pink       - 13\n"
				+ " orange     - 14\n" + " white      - 15\n";
		
		Configuration config = new Configuration(event.getSuggestedConfigurationFile());
		config.load();
		
		heightLimit = config.get(Configuration.CATEGORY_GENERAL, "heightLimit", 225).getInt();
		chuteColor = config.get(Configuration.CATEGORY_GENERAL, "chuteColor", -1).getInt();
		thermals = config.get(Configuration.CATEGORY_GENERAL, "allowThermals", true).getBoolean(true);
		autoDeploy = config.get(Configuration.CATEGORY_GENERAL, "autoDeploy", false).getBoolean(false);
		fallDistance = config.get(Configuration.CATEGORY_GENERAL, "fallDistance", 5).getInt();
		itemID = config.get(Configuration.CATEGORY_GENERAL, "itemID", 2500).getInt();
		
		// fix fallDistance  (2 < fallDistance < 20)
		fallDistance = (fallDistance < 2) ? 2 : (fallDistance > 20) ? 20 : fallDistance;
		config.addCustomCategoryComment(Configuration.CATEGORY_GENERAL, comments);
		
		config.save();
		
		proxy.registerRenderTextures();
		proxy.registerRenderer();
		proxy.registerKeyHandler(); // for keyboard control of parachute
		proxy.registerServerTickHandler(); // for auto deployment feature
    }

	@Init
	public void load(FMLInitializationEvent event) {
		parachuteItem = (new ItemParachute(itemID, EnumToolMaterial.WOOD)).setIconIndex(0).setItemName("Parachute");
		EntityRegistry.registerModEntity(EntityParachute.class, "Parachute", entityID, this, 64, 10, true);

		GameRegistry.addRecipe(new ItemStack(parachuteItem, 1), new Object[] {
			"###", "X X", " L ", '#', Block.cloth, 'X', Item.silk, 'L', Item.leather
		});

		LanguageRegistry.addName(parachuteItem, "Parachute");
		NetworkRegistry.instance().registerConnectionHandler(new ParachutePacketHandler());
		instance = this;
	}
	
	public String getVersion() {
		return ModInfo.version;
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

	public static int getItemID() {
		return itemID;
	}
}
