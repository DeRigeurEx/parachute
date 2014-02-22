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
package com.parachute.client;

import com.parachute.common.CommonProxyParachute;
import com.parachute.common.EntityParachute;
import cpw.mods.fml.client.registry.RenderingRegistry;

public class ClientProxyParachute extends CommonProxyParachute {

    @Override
    public void registerRenderer() {
        RenderingRegistry.registerEntityRenderingHandler(EntityParachute.class, new RenderParachute());
    }

    @Override
    public int addArmor(String armorName) {
        return RenderingRegistry.addNewArmourRendererPrefix(armorName);
    }

}
