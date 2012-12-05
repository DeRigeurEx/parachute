package parachute.common;

import java.util.EnumSet;

import org.lwjgl.input.Keyboard;

import net.minecraft.src.KeyBinding;
import cpw.mods.fml.client.registry.KeyBindingRegistry.KeyHandler;
import cpw.mods.fml.common.TickType;


public class ParachuteKeyHandler extends KeyHandler {
	static KeyBinding ascendBinding = new KeyBinding("Parachute Ascend", Keyboard.KEY_C);
	static KeyBinding descendBinding = new KeyBinding("Parachute Descend", Keyboard.KEY_X);
//	static KeyBinding autoBinding = new KeyBinding("Parachute Auto", Keyboard.KEY_Z);
	
    public ParachuteKeyHandler() {
        super(new KeyBinding[] {ascendBinding, descendBinding/*, autoBinding*/}, new boolean[] {true, true/*, false*/});
    }

    @Override
    public String getLabel() {
    	return "chutekeybindings";
    }

    @Override
    public void keyDown(EnumSet<TickType> types, KeyBinding kb, boolean tickEnd, boolean isRepeat) {
//    	if (ItemParachute.deployed) {
    		ParachutePacketHandler.sendKeyPress(kb.keyCode, true);
//    	}
    }

    @Override
    public void keyUp(EnumSet<TickType> types, KeyBinding kb, boolean tickEnd) {
//    	if (kb.keyCode == Keyboard.KEY_Z) {
//    		ParachutePacketHandler.sendKeyPress(kb.keyCode, true);
//    		return;
//    	}
//    	if (ItemParachute.deployed) {
    		ParachutePacketHandler.sendKeyPress(kb.keyCode, false);
//    	}
    }

    @Override
    public EnumSet<TickType> ticks() {
	    return EnumSet.of(TickType.CLIENT);
    }
}
