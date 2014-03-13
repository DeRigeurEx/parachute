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

import net.minecraft.entity.player.EntityPlayer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import org.lwjgl.input.Keyboard;

public class ParachutePacket extends AbstractPacket {

	private int keyCode;
	private boolean keyDown;

	ParachutePacket()
	{
		// required empty constructor
	}

	ParachutePacket(int _keyCode, boolean _keyDown)
	{
		keyCode = _keyCode;
		keyDown = _keyDown;
	}

	@Override
	public void encodeInto(ChannelHandlerContext ctx, ByteBuf buffer)
	{
		buffer.writeInt(keyCode);      // the keycode
		buffer.writeBoolean(keyDown);  // true if key is pressed
	}

	@Override
	public void decodeInto(ChannelHandlerContext ctx, ByteBuf buffer)
	{
		keyCode = buffer.readInt();
		keyDown = buffer.readBoolean();
	}

	@Override
	public void handleClientSide(EntityPlayer player)
	{
		// empty
	}

	@Override
	public void handleServerSide(EntityPlayer player)
	{
		if (player != null) {
			PlayerInfo pInfo = ParachutePlayerManager.instance().getPlayerInfoFromPlayer(player);
			if (pInfo != null) {
				if (keyCode == Keyboard.KEY_SPACE) {
					pInfo.keyPressed = keyDown;
				}
			}
		}
	}

}
