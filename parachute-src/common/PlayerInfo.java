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

import net.minecraft.network.INetworkManager;


public class PlayerInfo
{
	public String Name;
	public int mode; // 0 = drift, 1 = ascend
    public double coord; // player look vector y coord
    public boolean aad;  // true = AAD is activated, false = deactivated
    public INetworkManager networkManager;
    
    public PlayerInfo(String name, INetworkManager nm) {
        Name = name;
        networkManager = nm;
        aad = Parachute.getAADActive();
        mode = 0;
    }
    
}
