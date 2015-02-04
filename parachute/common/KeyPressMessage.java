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
// Copyright 2011-2015 Michael Sheppard (crackedEgg)
//
package com.parachute.common;

import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import org.lwjgl.input.Keyboard;

public class KeyPressMessage implements IMessage {

	private static final int ascendKey = Keyboard.KEY_SPACE;//ParachuteCommonProxy.getAscendKey();
	private boolean keyPressed;
	private int keyCode;

	public KeyPressMessage()
	{

	}

	public KeyPressMessage(int keyCode, boolean keyPressed)
	{
		this.keyCode = keyCode;
		this.keyPressed = keyPressed;
	}

	// the server does not respond with any messages so this isn't
	// being used; I included the method body for future possibilities.
	@Override
	public void fromBytes(ByteBuf bb)
	{
		keyCode = bb.readInt();
		keyPressed = bb.readBoolean();
	}

	// write the data to the stream
	@Override
	public void toBytes(ByteBuf bb)
	{
		bb.writeInt(keyCode);
		bb.writeBoolean(keyPressed);
	}

	public static class Handler implements IMessageHandler<KeyPressMessage, IMessage> {

		@Override
		public IMessage onMessage(KeyPressMessage msg, MessageContext ctx)
		{
			EntityPlayer entityPlayer = ctx.getServerHandler().playerEntity;
			if (entityPlayer != null) {
				if (msg.keyCode == ascendKey) {
					EntityParachute.setAscendMode(msg.keyPressed);
				}
			}
			return null;
		}
	}

}
