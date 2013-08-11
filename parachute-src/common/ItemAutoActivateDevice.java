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

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;


public class ItemAutoActivateDevice extends Item{
    public ItemAutoActivateDevice(int id){
        super(id);
		maxStackSize = 1;
		setCreativeTab(CreativeTabs.tabTools); // place in the tools tab in creative mode
    }

    @SideOnly(Side.CLIENT)
	@Override
	public void registerIcons(IconRegister iconReg) {
        super.registerIcons(iconReg);
		itemIcon = iconReg.registerIcon("parachutemod:AADevice");
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
}
