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
// Copyright 2011-2015 Michael Sheppard (crackedEgg)
//
package com.parachute.common;

import com.parachute.client.RenderParachute;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraft.item.Item;

public class ItemHopAndPop extends Item {

	final private float volume = 1.0F;

	public ItemHopAndPop(ToolMaterial toolmaterial)
	{
		super();
		setMaxDamage(toolmaterial.getMaxUses());
		maxStackSize = 4;
		setCreativeTab(CreativeTabs.tabTransport); // place in the transportation tab in creative mode
		ConfigHandler.setType(ParachuteCommonProxy.hopnpopName);
	}

	@Override
	public ItemStack onItemRightClick(ItemStack itemstack, World world, EntityPlayer entityplayer)
	{
		return deployParachute(itemstack, world, entityplayer);
	}

	public ItemStack deployParachute(ItemStack itemstack, World world, EntityPlayer entityplayer)
	{
		// only deploy if entityplayer exists and if player is falling and not already on a parachute.
		if (entityplayer != null && ParachuteCommonProxy.isFalling(entityplayer) && entityplayer.ridingEntity == null) {
			float offset = 2.5F; // small parachute only

			EntityParachute chute = new EntityParachute(world, entityplayer.posX, entityplayer.posY + offset, entityplayer.posZ);
			chute.playSound("step.cloth", volume, pitch());
			chute.rotationYaw = entityplayer.rotationYaw - 90.0f; // set parachute facing player direction
			if (world.isRemote) {
				RenderParachute.setParachuteColor(ConfigHandler.getChuteColor());
			} else {
				world.spawnEntityInWorld(chute);
			}
			entityplayer.mountEntity(chute);
			ParachuteCommonProxy.setDeployed(true);

			if (!entityplayer.capabilities.isCreativeMode) {
				if (itemstack != null) {
					itemstack.damageItem(ConfigHandler.getHopAndPopDamageAmount(), entityplayer);
				}
			}
		}
		return itemstack;
	}

	private float pitch()
	{
		return 1.0F / (itemRand.nextFloat() * 0.4F + 0.8F);
	}

	@Override
	public boolean getIsRepairable(ItemStack itemstack1, ItemStack itemstack2)
	{
		return Items.string == itemstack2.getItem() ? true : super.getIsRepairable(itemstack1, itemstack2);
	}

}
