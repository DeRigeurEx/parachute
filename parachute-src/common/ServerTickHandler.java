package parachute.common;

import java.util.EnumSet;
import java.util.List;
import java.util.Random;

import net.minecraft.src.EntityPlayer;
import net.minecraft.src.InventoryPlayer;
import net.minecraft.src.ItemStack;
import net.minecraft.src.Packet15Place;
import net.minecraft.src.World;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.TickType;

public class ServerTickHandler implements ITickHandler {
	private final int maxFallDistance = 5;

	@Override
	public void tickStart(EnumSet<TickType> type, Object... tickData) {
		if (type.equals(EnumSet.of(TickType.PLAYER))) {
			onPlayerTick((EntityPlayer)tickData[0]);
		}
	}
	
	// set a fallDistance greater than maxFallDistance to offset the unmount falling distance
	// and call ItemParachute.deployParachute if the player is falling
	private void onPlayerTick(EntityPlayer player) {
//		PlayerInfo pInfo = PlayerManagerParachute.getInstance().getPlayerInfoFromPlayer(player);
//		if (pInfo == null) {
//			return;
//		}
//		boolean auto = pInfo.autoDeploy;
		boolean auto = Parachute.instance.getAutoDeploy();
		if (auto && player.fallDistance > maxFallDistance && !player.onGround && !player.isOnLadder()) {
			ItemStack itemstack = inventoryContainsParachute(player.inventory);
			if (itemstack != null) {
				((ItemParachute)itemstack.getItem()).deployParachute(itemstack, player.worldObj, player);
			}
		} // else fall to death!
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
