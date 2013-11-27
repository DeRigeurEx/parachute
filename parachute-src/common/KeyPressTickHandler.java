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
// Copyright 2013 Michael Sheppard (crackedEgg)
//
package parachute.common;

import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.TickType;
import java.util.EnumSet;
import net.minecraft.entity.player.EntityPlayer;
//import net.minecraft.util.Vec3;
import org.lwjgl.input.Keyboard;

// used to intercept the space bar to make the parachute go up
// ridin' the thermals

public class KeyPressTickHandler implements ITickHandler {

    @Override
    public void tickStart(EnumSet<TickType> type, Object... tickData) {
        if (type.equals(EnumSet.of(TickType.PLAYER))) {
            onPlayerTick((EntityPlayer)tickData[0]);
		}
    }
    
    private void onPlayerTick(EntityPlayer p) {
        EntityPlayer player = (EntityPlayer)p;
        PlayerInfo pi = PlayerManagerParachute.getInstance().getPlayerInfoFromPlayer(player);
        if (pi != null) {
//            Vec3 vec = player.getLookVec();
//            System.out.println("Look Vector: " + vec.toString());
//            ParachutePacketHandler.sendLookVec(vec.yCoord);
            if (Keyboard.isKeyDown(Keyboard.KEY_SPACE)) { // grab space bar key while riding parachute
                ParachutePacketHandler.sendKeyPress(Keyboard.KEY_SPACE, true);
            } else {
                ParachutePacketHandler.sendKeyPress(Keyboard.KEY_SPACE, false);
            }
        }
    }

    @Override
    public void tickEnd(EnumSet<TickType> type, Object... tickData) {
        
    }

    @Override
    public EnumSet<TickType> ticks() {
        return EnumSet.of(TickType.PLAYER);
    }

    @Override
    public String getLabel() {
        return "ParachuteKeyPress";
    }
    
}
