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

import com.parachute.client.RenderParachute;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.entity.Entity;
import net.minecraft.init.Items;
import net.minecraft.item.ItemArmor;

public class ItemParachute extends ItemArmor {

	private final int damageAmount = 1;

	public ItemParachute(ArmorMaterial armorMaterial, int renderIndex, int armorType)
	{
		super(armorMaterial, renderIndex, armorType);
		setMaxDamage(armorMaterial.getDurability(armorType));
		maxStackSize = 1;
		setCreativeTab(CreativeTabs.tabTransport); // place in the transportation tab in creative mode
		Parachute.instance.setType(Parachute.parachuteName);
	}

	public void deployParachute(World world, EntityPlayer entityplayer)
	{
		// only deploy if entityplayer exists and if player is falling and not already on a parachute.
		if (entityplayer != null && Parachute.isFalling(entityplayer) && entityplayer.ridingEntity == null) {
			float offset;
			if (Parachute.instance.isSmallCanopy()) {
				offset = 2.5F;  // small canopy
			} else {
				offset = 3.5F;  // large canopy
			}
			EntityParachute chute = new EntityParachute(world, entityplayer.posX, entityplayer.posY - offset, entityplayer.posZ);
			chute.playSound("step.cloth", 1.0F, 1.0F / (itemRand.nextFloat() * 0.4F + 0.8F));
			chute.rotationYaw = (float) (((MathHelper.floor_double((double) (entityplayer.rotationYaw / 90.0F) + 0.5D) & 3) - 1) * 90);
			if (world.isRemote) {
				RenderParachute.setParachuteColor(Parachute.instance.getChuteColor());
			} else {
				world.spawnEntityInWorld(chute);
			}
			entityplayer.mountEntity(chute);

			if (!entityplayer.capabilities.isCreativeMode) {
				ItemStack parachute = entityplayer.inventory.armorItemInSlot(Parachute.armorSlot);
				if (parachute != null) {
					parachute.damageItem(damageAmount, entityplayer);
				}
			}
		}
	}

	@Override
	public String getArmorTexture(ItemStack itemstack, Entity entity, int slot, String type)
	{
		if (itemstack.getItem() == Parachute.parachuteItem) {
			return Parachute.modid.toLowerCase() + ":textures/models/armor/parachute-pack.png";
		}
		return Parachute.modid.toLowerCase() + ":textures/models/armor/parachute-pack.png";
	}

	@Override
	public boolean getIsRepairable(ItemStack itemstack1, ItemStack itemstack2)
	{
		return Items.string == itemstack2.getItem() ? true : super.getIsRepairable(itemstack1, itemstack2);
	}

	// TODO create a custom parachute pack model
//    @Override
//    public ModelBiped getArmorModel(EntityLivingBase entityLiving, ItemStack itemStack, int armorSlot) {
//        return new ModelParachutePack();
//    }
//    @Override
//    public boolean hasColor(ItemStack itemStack) {
//        return false;
//    }
}
