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
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;
import net.minecraft.world.World;


public class ItemAutoActivateDevice extends Item {
    private Icon[] aadIcon = new Icon[2];
    
    public boolean active = true; // AAD is active by default
    
    public ItemAutoActivateDevice(int id){
        super(id);
		maxStackSize = 1;
		setCreativeTab(CreativeTabs.tabTools); // place in the tools tab in creative mode
    }
    
    @Override
    public ItemStack onItemRightClick(ItemStack itemStack, World world, EntityPlayer entityPlayer) {
        PlayerInfo pi = PlayerManagerParachute.getInstance().getPlayerInfoFromPlayer(entityPlayer);
        if (!world.isRemote && pi != null) {
            active = !active;
            pi.setAAD(active);
            // this changes the item icon based on the damage.
            itemStack.setItemDamage(active == true ? 1 : 0);
        }
        
        return itemStack;
    }
    
    @SideOnly(Side.CLIENT)
	@Override
	public void registerIcons(IconRegister iconReg) {
        super.registerIcons(iconReg);
        aadIcon[0] = iconReg.registerIcon(Parachute.modid.toLowerCase() + ":AADeviceOff");
        aadIcon[1] = iconReg.registerIcon(Parachute.modid.toLowerCase() + ":AADeviceOn");
        itemIcon = aadIcon[active == true ? 1 : 0];
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
        return aadIcon[damage];
    }
    
}
