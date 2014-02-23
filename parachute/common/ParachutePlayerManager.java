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
// Copyright 2011-2014 Michael Sheppard (crackedEgg)
//

package com.parachute.common;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.entity.player.EntityPlayer;

public class ParachutePlayerManager {
	
	private final List<PlayerInfo> Players;

    private static final ParachutePlayerManager instance = new ParachutePlayerManager();
	
    public ParachutePlayerManager() {
        Players = new ArrayList<PlayerInfo>();
    }
	
	public static ParachutePlayerManager instance() {
		return instance;
	}
	
	public void addPlayer(PlayerInfo pi) {
		ParachutePlayerManager.instance.Players.add(pi);
	}
	
	public void removePlayer(String playerName) {
		for (int i = 0; i < ParachutePlayerManager.instance().Players.size(); i++) {
			if (ParachutePlayerManager.instance().Players.get(i).Name.equals(playerName)) {
				ParachutePlayerManager.instance().Players.remove(i);
			}
		}
	}
	
    // must test for null EntityPlayer before calling this method
    public PlayerInfo getPlayerInfoFromPlayer(EntityPlayer player) {
		for (PlayerInfo pi : ParachutePlayerManager.instance.Players) {
			if (pi.Name.equals(player.getDisplayName())) {
				return pi;
			}
		}
        return null;
    }
}
