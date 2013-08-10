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

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.network.packet.Packet;

public class CommonProxyParachute {
	
	public void registerRenderer() {}
	
	public void registerKeyHandler() {}
    
    public int addArmor(String armorName) { return 0; }
	
	public void registerServerTickHandler() {
		TickRegistry.registerTickHandler(new ServerTickHandler(), Side.SERVER);
	}
	
	@Deprecated
	public void sendCustomPacket(Packet packet) {
		try {
			FMLClientHandler.instance().sendPacket(packet);
		} catch (NullPointerException e) {
			System.err.println("NPE in sendCustomPacket: " + e.toString());
		}
    }

}
