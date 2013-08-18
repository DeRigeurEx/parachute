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
    private Icon[] aadIcon = new Icon[4];
    
    public boolean active = true; // AAD is active by default
    public static int responseTime = 2; // 1 = immediate (at deployment), 2 = short delay (5 meters), 3 = long delay (10 meters)
    
    public ItemAutoActivateDevice(int id) {
        super(id);
		maxStackSize = 1;
		setCreativeTab(CreativeTabs.tabTools); // place in the tools tab in creative mode
    }
    
    @Override
    public ItemStack onItemRightClick(ItemStack itemStack, World world, EntityPlayer entityPlayer) {
        PlayerInfo pi = PlayerManagerParachute.getInstance().getPlayerInfoFromPlayer(entityPlayer);
        if (!world.isRemote && pi != null) {
            responseTime++;
            if (responseTime > 3) {
                responseTime = 0;
            }
            active = (responseTime > 0);
            pi.setAAD(active);
            // change the item icon based on the damage.
            itemStack.setItemDamage(responseTime);
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
        aadIcon[3] = iconReg.registerIcon(Parachute.modid.toLowerCase() + ":AADeviceOn3"); // AAD delay = 15 meters
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
        // clamp damage at 3
        return aadIcon[(damage > 3) ? 3 : damage];
    }
    
    public int getResponseTime() {
        return responseTime;
    }
    
    public static int getDelay() {
        int delay = 0;
        switch (responseTime) {
            case 1:
                delay = 1;
                break;
            case 2:
                delay = 5;
                break;
            case 3:
                delay = 15;
                break;
        }
        return delay;
    }
    
}
