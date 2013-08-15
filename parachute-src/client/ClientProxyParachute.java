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

package parachute.client;

import parachute.common.CommonProxyParachute;
import parachute.common.EntityParachute;
//import parachute.common.ParachuteKeyHandler;
//import cpw.mods.fml.client.registry.KeyBindingRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.relauncher.Side;
import parachute.common.KeyPressTickHandler;

public class ClientProxyParachute extends CommonProxyParachute {
    @Override
    public void registerRenderer() {
		RenderingRegistry.registerEntityRenderingHandler(EntityParachute.class, new RenderParachute());   
	}
    
//    @Override
//    public void registerKeyHandler() {
//    	KeyBindingRegistry.registerKeyBinding(new ParachuteKeyHandler());
//    }
    
    @Override
    public void registerPlayerTickHandler() {
        TickRegistry.registerTickHandler(new KeyPressTickHandler(), Side.CLIENT);
    }
    
    @Override
	public int addArmor(String armorName) {
		return RenderingRegistry.addNewArmourRendererPrefix(armorName);
	}
    
}

