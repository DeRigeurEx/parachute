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

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class ItemAutoActivateDevice extends Item {

	private final IIcon[] aadIcon;
	private final int maxIconIdx = 1;

	// initial value is false (inactive) from config file
	public static boolean active = Parachute.getAADActive();
	private static final double fallThreshold = Parachute.getFallThreshold();
	private static final double altitude = Parachute.getAADAltitude();

	public ItemAutoActivateDevice()
	{
		super();
		this.aadIcon = new IIcon[2];
		maxStackSize = 1;
		setMaxDamage(maxIconIdx + 1);
		setCreativeTab(CreativeTabs.tabTools); // place in the tools tab in creative mode
	}

	@Override
	public ItemStack onItemRightClick(ItemStack itemStack, World world, EntityPlayer entityPlayer)
	{
		if (!world.isRemote) {
			active = !active;
			// change the item icon based on the damage.
			itemStack.setItemDamage(active ? 1 : 0);
			entityPlayer.addChatMessage(new ChatComponentText("AAD: " + (active ? "On" : "Off")));
            // TODO: figure out how to make the device have a finite life
			//       since I'm using the damage for icons.
		}
		return itemStack;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerIcons(IIconRegister iconReg)
	{
		super.registerIcons(iconReg);
		aadIcon[0] = iconReg.registerIcon(Parachute.modid.toLowerCase() + ":AADeviceOff");
		aadIcon[1] = iconReg.registerIcon(Parachute.modid.toLowerCase() + ":AADeviceOn");
		itemIcon = getIconFromDamage(active ? 1 : 0);
	}

	// search inventory for an auto activation device
	public static ItemStack inventoryContainsAAD(InventoryPlayer inventory)
	{
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
	public IIcon getIconFromDamage(int damage)
	{
		// clamp damage at maxIconIdx
		return aadIcon[(damage > maxIconIdx) ? maxIconIdx : damage];
	}

	public static boolean getAutoActivateAltitude(EntityPlayer player)
	{
		boolean altitudeReached = false;

		int x = MathHelper.floor_double(player.posX);
		int y = MathHelper.floor_double(player.posY - altitude);
		int z = MathHelper.floor_double(player.posZ);

		if (!player.worldObj.isAirBlock(x, y, z) && player.fallDistance > fallThreshold) {
			altitudeReached = true;
		}
		return altitudeReached;
	}

	@Override
	public boolean getIsRepairable(ItemStack itemstack1, ItemStack itemstack2)
	{
		return Items.redstone == itemstack2.getItem() ? true : super.getIsRepairable(itemstack1, itemstack2);
	}

}
