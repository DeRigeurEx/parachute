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

import java.util.EnumSet;

import net.minecraft.client.settings.KeyBinding;
import org.lwjgl.input.Keyboard;
import cpw.mods.fml.client.registry.KeyBindingRegistry.KeyHandler;
import cpw.mods.fml.common.TickType;


public class ParachuteKeyHandler extends KeyHandler {
	static KeyBinding ascendBinding = new KeyBinding("Parachute Ascend", Keyboard.KEY_C);
	static KeyBinding descendBinding = new KeyBinding("Parachute Descend", Keyboard.KEY_X);
	static KeyBinding colorBinding = new KeyBinding("Parachute Color", Keyboard.KEY_PERIOD);
//    static KeyBinding deployBinding = new KeyBinding("Parachute Deployment", Keyboard.KEY_Z);
	
    public ParachuteKeyHandler() {
        super(new KeyBinding[] {ascendBinding, descendBinding, colorBinding/*, deployBinding*/},
        	new boolean[] {true, true, false/*, false*/});
    }

    @Override
    public String getLabel() {
    	return "chutekeybindings";
    }

    @Override
    public void keyDown(EnumSet<TickType> types, KeyBinding kb, boolean tickEnd, boolean isRepeat) {
    	if (tickEnd) {
//    		System.out.println("ParachuteKeyHandler: Sending KEYDOWN");
    		ParachutePacketHandler.sendKeyPress(kb.keyCode, true);
    	}
    }

    @Override
    public void keyUp(EnumSet<TickType> types, KeyBinding kb, boolean tickEnd) {
    	if (tickEnd) {
//    		System.out.println("ParachuteKeyHandler: Sending KEYUP");
    		ParachutePacketHandler.sendKeyPress(kb.keyCode, false);
    	}
    }

    @Override
    public EnumSet<TickType> ticks() {
	    return EnumSet.of(TickType.CLIENT);
    }
}
