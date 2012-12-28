package parachute.common;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.IGuiHandler;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.network.packet.Packet;
import net.minecraft.server.MinecraftServer;

public class CommonProxyParachute// implements IGuiHandler
{
	public void registerRenderTextures() {}
	
	public void registerRenderer() {}
	
	public void registerKeyHandler() {}
	
	public void registerServerTickHandler() {
		TickRegistry.registerTickHandler(new ServerTickHandler(), Side.SERVER);
	}
	
//	public void sendCustomPacket(Packet packet) {
//		try {
////			FMLClientHandler.instance().sendPacket(packet);
//			PacketDispatcher.sendPacketToServer(packet);
//		} catch (NullPointerException e) {
//			System.err.println("NPE in sendCustomPacket: " + e.toString());
//			return;
//		}
//    }

//	@Override
//	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
//		// TODO add container code here
//		System.out.println("getServerGuiElement");
//		return null;
//	}
//
//	@Override
//	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
//		// TODO Auto-generated method stub
//		System.out.println("getClientGuiElement");
//		return null;
//	}
}
