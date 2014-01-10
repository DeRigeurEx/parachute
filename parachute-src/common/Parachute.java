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
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.EnumHelper;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.registry.*;
import cpw.mods.fml.common.SidedProxy;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumArmorMaterial;
import net.minecraft.item.EnumToolMaterial;

@Mod(
        modid = Parachute.modid,
        name = Parachute.name,
        version = Parachute.mcversion
)

@NetworkMod(
        versionBounds = "[" + Parachute.mcversion + "]",
        clientSideRequired = true,
        serverSideRequired = false,
        packetHandler = ParachutePacketHandler.class,
        connectionHandler = ParachutePacketHandler.class,
        channels = {Parachute.channel}
)

public class Parachute {

    static EnumArmorMaterial NYLON = EnumHelper.addArmorMaterial("nylon", 15, new int[]{2, 5, 4, 1}, 12); // same as CHAIN

    public static final String modid = "ParachuteMod";
    public static final String mcversion = "1.6.4";
    public static final String channel = modid;
    public static final String name = "Parachute Mod";
    public static final String entityName = "Parachute";
    public static final String ripcordName = "Ripcord";
    public static final String aadName = "AutoActivationDevice";
    public static final String hopnpopName = "HopAndPop";

    private int heightLimit;
    private int chuteColor;
    private boolean thermals;
    private static double AADAltitude;
    private boolean smallCanopy;
    private static int parachuteID;
    private static int ripcordID;
    private static int aadID;
    private static int popID;
    private static boolean AADActive;
    private static double fallThreshold;
    private final int entityID = EntityRegistry.findGlobalUniqueEntityId();
    private static final int armorType = 1; // armor type: 0 = helmet, 1 = chestplate, 2 = legs. 3 = boots
    public static final int armorSlot = 2;  // armor slot: 0 = ??, 1 = ??, 2 = chestplate, 3 = ??

    @SidedProxy(
            clientSide = "parachute.client.ClientProxyParachute",
            serverSide = "parachute.common.CommonProxyParachute"
    )
    public static CommonProxyParachute proxy;

    public static ItemParachute parachuteItem;
    public static ItemHopAndPop hopnpopItem;
    public static ItemRipCord ripcordItem;
    public static ItemAutoActivateDevice aadItem;

    @Instance
    public static Parachute instance;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        String generalComments = Parachute.name + " Config\nMichael Sheppard (crackedEgg)";
        String itemComment = "itemID - customize the Item ID (2500)";
        String cordComment = "ripcordID - customize the Ripcord Item ID (2501)";
        String aadComment = "auto activation device ID - customize the AAD Item ID (2502)";
        String popComment = "popID - customize the hop-n-pop Item ID (2503)";
        String heightComment = "heightLimit  - 0 (zero) disables altitude limiting (256)";
        String thermalComment = "allowThermals - true|false enable/disable thermals (true)";
        String aadAltitudeComment = "AADAltitude - altitude (in meters) at which auto deploy occurs (10)";
        String fallThresholdComment = "fallThreshold - player must have fallen this far to activate AAD (5.0)";
        String aaDActiveComment = "AADActive - whether the AAD is active or not. default is inactive. (false)";
        String typeComment = "smallCanopy - set to true to use the smaller 3 panel canopy, false for the\n"
                + "larger 4 panel canopy (true)";
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

        heightLimit = config.get(Configuration.CATEGORY_GENERAL, "heightLimit", 256, heightComment).getInt();
        chuteColor = config.get(Configuration.CATEGORY_GENERAL, "chuteColor", 18, colorComment).getInt();
        thermals = config.get(Configuration.CATEGORY_GENERAL, "allowThermals", true, thermalComment).getBoolean(true);
        fallThreshold = config.get(Configuration.CATEGORY_GENERAL, "fallThreshold", 5.0, fallThresholdComment).getDouble(5.0);
        AADAltitude = config.get(Configuration.CATEGORY_GENERAL, "AADAltitude", 15.0, aadAltitudeComment).getDouble(15.0);
        AADActive = config.get(Configuration.CATEGORY_GENERAL, "AADActive", false, aaDActiveComment).getBoolean(false);
        parachuteID = config.get(Configuration.CATEGORY_GENERAL, "itemID", 2500, itemComment).getInt();
        ripcordID = config.get(Configuration.CATEGORY_GENERAL, "ripcordID", 2501, cordComment).getInt();
        aadID = config.get(Configuration.CATEGORY_GENERAL, "aadID", 2502, aadComment).getInt();
        popID = config.get(Configuration.CATEGORY_GENERAL, "popID", 2503, popComment).getInt();
        smallCanopy = config.get(Configuration.CATEGORY_GENERAL, "smallCanopy", true, typeComment).getBoolean(true);

        config.addCustomCategoryComment(Configuration.CATEGORY_GENERAL, generalComments);

        config.save();

        // clamp the fallThreshold to a minimum of 2
        fallThreshold = fallThreshold < 2 ? 2 : fallThreshold;

        proxy.registerRenderer();
        proxy.registerServerTickHandler(); // for auto deployment feature
        proxy.registerPlayerTickHandler();
        proxy.registerConnectionHandler();
    }

    @EventHandler
    public void Init(FMLInitializationEvent event) {
        int chuteID = proxy.addArmor("parachute");
        EntityRegistry.registerModEntity(EntityParachute.class, entityName, entityID, this, 64, 10, true);
        parachuteItem = (ItemParachute) (new ItemParachute(parachuteID, NYLON, chuteID, armorType));
        parachuteItem.setTextureName(Parachute.modid.toLowerCase() + ":Parachute");
        parachuteItem.setUnlocalizedName(entityName);

        // used to repair the parachute
        NYLON.customCraftingMaterial = Item.silk;

        ripcordItem = (ItemRipCord) (new ItemRipCord(ripcordID)).setUnlocalizedName(ripcordName);
        aadItem = (ItemAutoActivateDevice) (new ItemAutoActivateDevice(aadID)).setUnlocalizedName(aadName);
        hopnpopItem = (ItemHopAndPop) (new ItemHopAndPop(popID, EnumToolMaterial.WOOD)).setUnlocalizedName(hopnpopName);

        // recipes to craft the parachutes, ripcord and AAD
        GameRegistry.addRecipe(new ItemStack(parachuteItem, 1), new Object[]{
            "###", "X X", " L ", '#', Block.cloth, 'X', Item.silk, 'L', Item.leather
        });
        
        GameRegistry.addRecipe(new ItemStack(hopnpopItem, 1), new Object[]{
            "###", "X X", " X ", '#', Block.cloth, 'X', Item.silk
        });

        GameRegistry.addRecipe(new ItemStack(ripcordItem, 1), new Object[]{
            "#  ", " # ", "  *", '#', Item.silk, '*', Item.ingotIron
        });

        GameRegistry.addRecipe(new ItemStack(aadItem, 1), new Object[]{
            " * ", " % ", " # ", '*', Item.comparator, '%', Item.redstone, '#', ripcordItem,});

        // add the names of the items
        LanguageRegistry.addName(parachuteItem, entityName);
        LanguageRegistry.addName(ripcordItem, ripcordName);
        LanguageRegistry.addName(aadItem, aadName);
        LanguageRegistry.addName(hopnpopItem, hopnpopName);

        instance = this;
    }

    public String getVersion() {
        return Parachute.mcversion;
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

    public static double getAADAltitude() {
        return AADAltitude;
    }

    public static boolean getAADActive() {
        return AADActive;
    }

    public static double getFallThreshold() {
        return fallThreshold;
    }

    public void setAADActive(boolean active) {
        AADActive = active;
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
