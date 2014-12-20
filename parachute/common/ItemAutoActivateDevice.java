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

import static com.parachute.common.Parachute.aadName;
import static com.parachute.common.Parachute.modid;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.util.BlockPos;

public class ItemAutoActivateDevice extends Item {

	// initial value is false (inactive) from config file
	public static boolean active = Parachute.getAADActive();
	private static final double fallThreshold = Parachute.getFallThreshold();
	private static final double altitude = Parachute.getAADAltitude();

	public ItemAutoActivateDevice()
	{
		super();
		maxStackSize = 1;
		setMaxDamage(2);
		setCreativeTab(CreativeTabs.tabTools); // place in the tools tab in creative mode
	}

	@Override
	public ItemStack onItemRightClick(ItemStack itemStack, World world, EntityPlayer entityPlayer)
	{
		if (!world.isRemote) {
			active = !active;
			itemStack.setItemDamage(active ? 1 : 0);
			itemStack.setStackDisplayName(active ? "Active AAD" : "Inactive AAD");
		}
		return itemStack;
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

	public static boolean getAutoActivateAltitude(EntityPlayer player)
	{
		boolean altitudeReached = false;

		int x = MathHelper.floor_double(player.posX);
		int y = MathHelper.floor_double(player.posY - altitude);
		int z = MathHelper.floor_double(player.posZ);
		BlockPos blockPos = new BlockPos(x, y, z);

		if (!player.worldObj.isAirBlock(blockPos) && player.fallDistance > fallThreshold) {
			altitudeReached = true;
		}
		return altitudeReached;
	}

	@Override
	public boolean getIsRepairable(ItemStack itemstack1, ItemStack itemstack2)
	{
		return Items.redstone == itemstack2.getItem() ? true : super.getIsRepairable(itemstack1, itemstack2);
	}
	
	@Override
	public ModelResourceLocation getModel(ItemStack stack, EntityPlayer player, int useRemaining)
	{
		if (active) {
			return new ModelResourceLocation(modid + ":" + aadName, "inventory");
		} else {
			return new ModelResourceLocation(modid + ":" + aadName + "_off", "inventory");
		}
	}
	
	@Override
	public boolean showDurabilityBar(ItemStack stack)
    {
        return false;
    }

}
