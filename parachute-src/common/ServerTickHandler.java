package parachute.common;

import java.util.EnumSet;
import java.util.List;
import java.util.Random;

import net.minecraft.src.EntityPlayer;
import net.minecraft.src.InventoryPlayer;
import net.minecraft.src.ItemStack;
import net.minecraft.src.World;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.TickType;

public class ServerTickHandler implements ITickHandler {

	@Override
	public void tickStart(EnumSet<TickType> type, Object... tickData) {
		if (type.equals(EnumSet.of(TickType.PLAYER))) {
	         onPlayerTick((EntityPlayer)tickData[0]);
	     }
	}
	
	// set a fallDistance greater than 2 to offset the unmount falling distance
	private void onPlayerTick(EntityPlayer player) {
		if (player.fallDistance > 2 && !player.onGround && !player.isOnLadder()) {
			autoDeployParachute(player.worldObj, player);
		}
	}
	
	// if a parachute is found in the inventory then deploy it automagically
	public void autoDeployParachute(World world, EntityPlayer player) {
//		ItemStack itemstack = player.inventory.getCurrentItem();
//		if (itemstack.getItem() instanceof ItemParachute) {
		ItemStack itemstack = inventoryContainsParachute(player.inventory);
		if (itemstack != null) {
			ItemParachute chute = (ItemParachute)itemstack.getItem();
			chute.autoDeployParachute(itemstack, world, player);
		}
		// else fall to death!
	}
	
	// search inventory for a parachute
	public ItemStack inventoryContainsParachute(InventoryPlayer inventory) {
		ItemStack itemstack = null;
		for (ItemStack s : inventory.mainInventory) {
			if (s != null && s.getItem() instanceof ItemParachute) {
				itemstack = s;
				break;
			}
		}
		return itemstack;
	}

	@Override
	public void tickEnd(EnumSet<TickType> type, Object... tickData) {

	}

	@Override
	public EnumSet<TickType> ticks() {
		return EnumSet.of(TickType.PLAYER, TickType.SERVER);
	}

	@Override
	public String getLabel() {
		return null;
	}

}
