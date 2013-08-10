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

import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
//import net.minecraft.item.EnumToolMaterial;
//import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
//import parachute.client.ModelParachutePack;
import net.minecraft.entity.Entity;
import net.minecraft.item.EnumArmorMaterial;
import net.minecraft.item.ItemArmor;
//import static parachute.common.Parachute.armorSlot;

public class ItemParachute extends ItemArmor {
	
//	public ItemParachute(int id, EnumToolMaterial enumtoolmaterial) {
    public ItemParachute(int id, EnumArmorMaterial enumArmorMaterial, int renderIndex, int armorType) {
//        super(id);
		super(id, enumArmorMaterial, renderIndex, armorType);
        setMaxDamage(enumArmorMaterial.getDurability(armorType));
		maxStackSize = 16;
//		setMaxDamage(enumtoolmaterial.getMaxUses()); // this damage is for number of uses only
		setCreativeTab(CreativeTabs.tabTransport); // place in the transportation tab in creative mode
	}

//	public ItemParachute(int i) {
//		super(i);
//		maxStackSize = 16;
//		setMaxDamage(Parachute.NYLON.getMaxUses()); // this damage is for number of uses only
//		setCreativeTab(CreativeTabs.tabTransport); // place in the transportation tab in creative mode
//	}

//    @Override
//	public ItemStack onItemRightClick(ItemStack itemstack, World world, EntityPlayer entityplayer) {
//		return deployParachute(itemstack, world, entityplayer);
//	}
    
	public void /*ItemStack*/ deployParachute(/*ItemStack itemstack*/ World world, EntityPlayer entityplayer) {
		// don't deploy if entityplayer is null or if player is not falling or if already on a parachute.
		if (entityplayer == null || !isFalling(entityplayer) || entityplayer.ridingEntity != null) {
			return; //itemstack;
		}

		world.playSoundAtEntity(entityplayer, "step.cloth", 1.0F, 1.0F / (itemRand.nextFloat() * 0.4F + 0.8F));

		double x = entityplayer.prevPosX + (entityplayer.posX - entityplayer.prevPosX);
		double y = (entityplayer.prevPosY + (entityplayer.posY - entityplayer.prevPosY) + 1.62D) - (double) entityplayer.yOffset;
		double z = entityplayer.prevPosZ + (entityplayer.posZ - entityplayer.prevPosZ);

		float offset;
		if (Parachute.instance.getCanopyType()) {
			offset = 2.5F;  // small canopy
		} else {
			offset = 3.5F;  // large canopy
		}
		EntityParachute chute = new EntityParachute(world, (float) x, (float) y - offset, (float) z);
		chute.rotationYaw = (float)(((MathHelper.floor_double((double)(entityplayer.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3) - 1) * 90);
		if (!world.isRemote) {
			world.spawnEntityInWorld(chute);
		}
		entityplayer.mountEntity(chute);

		if (!entityplayer.capabilities.isCreativeMode) {
//			itemstack.damageItem(1, entityplayer);
            entityplayer.inventory.armorItemInSlot(Parachute.armorSlot).damageItem(1, entityplayer);
		}
		
//		return itemstack;
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	public void registerIcons(IconRegister iconReg) {
        super.registerIcons(iconReg);
		itemIcon = iconReg.registerIcon("parachutemod:Parachute");
	}
    
    @Override
	public String getArmorTexture(ItemStack itemstack, Entity entity, int slot, int layer) {
		if (itemstack.itemID == Parachute.parachuteItem.itemID) {
            return "parachutemod:textures/models/armor/parachute-pack.png";
		}
		return "parachutemod:textures/models/armor/parachute-pack.png";
	}
    
    // custom armor renderer
//    @Override
//    public ModelBiped getArmorModel(EntityLivingBase entityLiving, ItemStack itemStack, int armorSlot) {
//        return new ModelParachutePack();
//    }
	
	public boolean isFalling(EntityPlayer entity) {
		return (entity.fallDistance > 0.0F && !entity.onGround && !entity.isOnLadder());
	}
	
}
