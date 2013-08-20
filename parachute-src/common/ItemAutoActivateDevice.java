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

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;
import net.minecraft.world.World;


public class ItemAutoActivateDevice extends Item {
    private Icon[] aadIcon = new Icon[5];
    
    private static final int immedDelay =  1;
    private static final int shortDelay =  5;
    private static final int longDelay  = 12;
    private static final int crazyDelay = 20;
    private final int maxIconIdx = 4;
    
    // initial value is false (inactive) from config file
    public static boolean active = Parachute.getAADActive();
    // 1 = immediate (at deployment), 2 = short delay (5 meters), 3 = long delay (15 meters)
    // initial value is zero from the config file
    public static int responseTime = setResponseTime(Parachute.getAADelay());
    
    public ItemAutoActivateDevice(int id) {
        super(id);
		maxStackSize = 1;
        setMaxDamage(maxIconIdx + 1);
		setCreativeTab(CreativeTabs.tabTools); // place in the tools tab in creative mode
    }
    
    @Override
    public ItemStack onItemRightClick(ItemStack itemStack, World world, EntityPlayer entityPlayer) {
        PlayerInfo pi = PlayerManagerParachute.getInstance().getPlayerInfoFromPlayer(entityPlayer);
        if (!world.isRemote && pi != null) {
            responseTime++;
            if (responseTime > maxIconIdx) {
                responseTime = 0;
            }
            active = (responseTime > 0);
            pi.setAAD(active);
            // change the item icon based on the damage.
            itemStack.setItemDamage(responseTime);
            // TODO: figure out how to make the device have a finite life
            //       since I'm using the damage for icons.
        }
        return itemStack;
    }
    
    @SideOnly(Side.CLIENT)
	@Override
	public void registerIcons(IconRegister iconReg) {
        super.registerIcons(iconReg);
        aadIcon[0] = iconReg.registerIcon(Parachute.modid.toLowerCase() + ":AADeviceOff"); // AAD off
        aadIcon[1] = iconReg.registerIcon(Parachute.modid.toLowerCase() + ":AADeviceOn1"); // AAD delay = 1 meter
        aadIcon[2] = iconReg.registerIcon(Parachute.modid.toLowerCase() + ":AADeviceOn2"); // AAD delay = 5 meters
        aadIcon[3] = iconReg.registerIcon(Parachute.modid.toLowerCase() + ":AADeviceOn3"); // AAD delay = 12 meters
        aadIcon[4] = iconReg.registerIcon(Parachute.modid.toLowerCase() + ":AADeviceOn4"); // AAD delay = 20 meters
        itemIcon = getIconFromDamage(active == true ? responseTime : 0);
	}
    
    // search inventory for an auto activation device
	public static ItemStack inventoryContainsAAD(InventoryPlayer inventory) {
		ItemStack itemstack = null;
		for (ItemStack s : inventory.mainInventory) {
			if (s != null && s.getItem() instanceof ItemAutoActivateDevice) {
				itemstack = s;
				break;
			}
		}
		return itemstack;
	}
    
    // this allows us to change the item icon for on and off
    @Override
    public Icon getIconFromDamage(int damage) {
        // clamp damage at 4
        return aadIcon[(damage > maxIconIdx) ? maxIconIdx : damage];
    }
    
    public static int getDelay() {
        int delay = 5;
        switch (responseTime) {
            case 1:
                delay = immedDelay;
                break;
            case 2:
                delay = shortDelay;
                break;
            case 3:
                delay = longDelay;
                break;
            case 4:
                delay = crazyDelay;
                break;
        }
        return delay;
    }
    
    public static int setResponseTime(int delay) {
        int rTime = 0;
        switch (delay) {
            case immedDelay:
                rTime = 1;
                break;
            case shortDelay:
                rTime = 2;
                break;
            case longDelay:
                rTime = 3;
                break;
            case crazyDelay:
                rTime = 4;
                break;
        }
        return rTime;
    }
    
}
