package parachute.common;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.Side;
import cpw.mods.fml.common.registry.TickRegistry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.src.Packet;

public class CommonProxyParachute
{
	public void registerRenderTextures() {}
	
	public void registerRenderer() {}
	
	public void registerKeyHandler() {}
	
	public void registerServerTickHandler() {
		TickRegistry.registerTickHandler(new ServerTickHandler(), Side.SERVER);
	}
	
	public void sendCustomPacket(Packet packet) {
		try {
			// TODO: this method broadcasts packets to all players. It would
			// be better to send this packet to the parachute (player) that 
			// pressed the key.
			FMLClientHandler.instance().sendPacket(packet);
		} catch (NullPointerException e) {
			System.err.println("NPE in sendCustomPacket: " + e.toString());
			return;
		}
    }
	
}
