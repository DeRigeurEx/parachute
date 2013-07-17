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

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;


public class PlayerManagerParachute
{
	public List<PlayerInfo> Players;
    
    private static final PlayerManagerParachute instance = new PlayerManagerParachute();
    
    public static final PlayerManagerParachute getInstance() {
        return instance;
    }
    
    private PlayerManagerParachute() {
        Players = new ArrayList();
    }
    
    // must test for null EntityPlayer before calling this method
    public PlayerInfo getPlayerInfoFromPlayer(EntityPlayer player) {
        for(PlayerInfo pi : Players) {
            if(pi.Name.equals(player.username))
                return pi;
        }
    	return null;
    }
}
