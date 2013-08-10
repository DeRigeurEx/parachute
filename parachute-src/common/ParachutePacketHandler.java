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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import parachute.client.RenderParachute;
import org.lwjgl.input.Keyboard;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.NetLoginHandler;
import net.minecraft.network.packet.NetHandler;
import net.minecraft.network.packet.Packet1Login;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.server.MinecraftServer;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.network.IConnectionHandler;
import cpw.mods.fml.common.network.IPacketHandler;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.network.Player;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.gui.GuiNewChat;
import net.minecraft.item.ItemStack;
//import net.minecraft.item.ItemStack;
//import net.minecraft.world.World;

public class ParachutePacketHandler implements IPacketHandler, IConnectionHandler {
	
	public static final byte KeyPress = 0;
	
	@Override
	// server handles key press custom packets from the player
	public void onPacketData(INetworkManager manager, Packet250CustomPayload packet, Player p) {
		DataInputStream dis = new DataInputStream(new ByteArrayInputStream(packet.data));
		byte keyCode;
		boolean keyDown;
		byte type;
		
		if (!packet.channel.equals(Parachute.channel)) { // check the channel - CYA
			System.out.println("Received a packet not intended for channel " + Parachute.channel);
			return;
		}
		
		try {
			EntityPlayer player = (EntityPlayer)p;
			if (player == null) {
				return;
			}
			
			type = dis.readByte();
			if (type == KeyPress) {
				PlayerInfo pi = PlayerManagerParachute.getInstance().getPlayerInfoFromPlayer(player);
				if (pi == null) {
					return;
				}
					
				keyCode = dis.readByte();
				keyDown = dis.readBoolean();
				
				if (keyCode == Keyboard.KEY_C) { // keycode: 46
					if (keyDown) {
						pi.setLiftMode(1); // ascend
					} else {
						pi.setLiftMode(0); // drift
					}
				}
				
				if (keyCode == Keyboard.KEY_X) { // keycode: 45
					if (keyDown) {
						pi.setLiftMode(2); // descend
					} else {
						pi.setLiftMode(0); // drift
					}
				}
				
				if (keyCode == Keyboard.KEY_PERIOD) { // keycode: 52
					if (keyDown) {
						RenderParachute.setParachuteColor(pi.changeColor());
					}
				}
                ItemStack aad = ItemAutoActivateDevice.inventoryContainsAAD(player.inventory);
                if (keyCode == Keyboard.KEY_Z && aad != null) { // auto deploy parachute
                    if (keyDown) {
                        pi.setAAD(pi.aad == true ? false : true);
                        GuiNewChat chat = FMLClientHandler.instance().getClient().ingameGUI.getChatGUI();
                        chat.printChatMessage("Auto Activation Device is " + (pi.aad ? "on" : "off"));
                    }
                }
                
//                if (keyCode == Keyboard.KEY_Z) { // deploy parachute
//                    if (keyDown) {
//                        World world = player.worldObj;
//                        if (!player.onGround && !player.isOnLadder()) {
//                            if (Parachute.playerIsWearingParachute(player)) {
//                                ItemStack itemstack = player.getCurrentArmor(Parachute.armorSlot);
//                                ((ItemParachute)itemstack.getItem()).deployParachute(/*itemstack, */world, player);
//                            }
//                        }
//                    }
//                }
			}
		} catch (IOException e) {
            throw new RuntimeException(e);
		}
	}
	
	@SideOnly(Side.CLIENT)
	// send key press events in a custom packet to the server
	public static void sendKeyPress(int keyCode, boolean keyDown) {
		Minecraft client = FMLClientHandler.instance().getClient();
		WorldClient world = client.theWorld;
		if (world == null || !world.isRemote) {
//            return;
		} else {
			try	{
				ByteArrayOutputStream bos = new ByteArrayOutputStream();
				DataOutputStream dos = new DataOutputStream(bos);
				Packet250CustomPayload packet = new Packet250CustomPayload();

				dos.writeByte(KeyPress);    // key press type packet
				dos.writeByte(keyCode);		// the keycode
				dos.writeBoolean(keyDown);  // true if key is pressed
				dos.close();

				packet.channel = Parachute.channel;
				packet.data = bos.toByteArray();
				packet.length = bos.size();
				packet.isChunkDataPacket = false;

				PacketDispatcher.sendPacketToServer(packet);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
	}

	@Override
	public void playerLoggedIn(Player p, NetHandler netHandler, INetworkManager manager) {
		PlayerManagerParachute.getInstance().Players.add(new PlayerInfo(((EntityPlayer)p).username, manager));
	}

	@Override
	public String connectionReceived(NetLoginHandler netHandler, INetworkManager manager) {
		return null;
	}

	@Override
	public void connectionOpened(NetHandler netClientHandler, String server, int port, INetworkManager manager) {
		
	}

	@Override
	public void connectionOpened(NetHandler netClientHandler, MinecraftServer server, INetworkManager manager) {
		
	}

	@Override
	public void connectionClosed(INetworkManager manager) {
		PlayerInfo PI = new PlayerInfo("", manager);
		for(int i = 0; i < PlayerManagerParachute.getInstance().Players.size()/* && PI != null*/; i++) {
			if(PlayerManagerParachute.getInstance().Players.get(i).networkManager == manager) {
				PlayerManagerParachute.getInstance().Players.remove(i);
			}  
		}
	}

	@Override
	public void clientLoggedIn(NetHandler clientHandler, INetworkManager manager, Packet1Login login) {
		PlayerManagerParachute.getInstance().Players.add(new PlayerInfo(clientHandler.getPlayer().username, manager));
	}
    
}
