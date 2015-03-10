package de.teamlapen.vampirism.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.MovingObjectPosition;

import org.lwjgl.input.Keyboard;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.network.InputEventPacket;
import de.teamlapen.vampirism.util.Logger;
import de.teamlapen.vampirism.util.REFERENCE;
import de.teamlapen.vampirism.util.REFERENCE.KEY;

public class KeyInputEventHandler {

	private static KEY getPressedKeyBinding() {
		if (SUCK.isPressed()) {
			return KEY.SUCK;
		} else if (AUTO.isPressed()) {
			return KEY.AUTO;
		}
		else if(VLORD.isPressed()){
			return KEY.VLORD;
		}
		return KEY.UNKNOWN;
	}
	public static KeyBinding SUCK = new KeyBinding(REFERENCE.KEYS.SUCK_BLOOD, Keyboard.KEY_F, REFERENCE.KEYS.CATEGORY);

	public static KeyBinding AUTO = new KeyBinding(REFERENCE.KEYS.AUTO_BLOOD, Keyboard.KEY_B, REFERENCE.KEYS.CATEGORY);
	
	public static KeyBinding VLORD = new KeyBinding(REFERENCE.KEYS.VLORD_BLOOD, Keyboard.KEY_G, REFERENCE.KEYS.CATEGORY);

	@SubscribeEvent
	public void handleKeyInput(InputEvent.KeyInputEvent event) {
		KEY keyPressed = getPressedKeyBinding(); // Only call isPressed once, so
													// get value here!
		if (keyPressed == KEY.SUCK) {
			MovingObjectPosition mouseOver = Minecraft.getMinecraft().objectMouseOver;
			if (mouseOver != null && mouseOver.entityHit != null) {
				VampirismMod.modChannel.sendToServer(new InputEventPacket(InputEventPacket.SUCKBLOOD, "" + mouseOver.entityHit.getEntityId()));
			}
		} else if (keyPressed == KEY.AUTO) {
			VampirismMod.modChannel.sendToServer(new InputEventPacket(InputEventPacket.TOGGLEAUTOFILLBLOOD, "0"));
		}
		else if(keyPressed==KEY.VLORD){
			VampirismMod.modChannel.sendToServer(new InputEventPacket(InputEventPacket.TOGGLEVLORD,"0"));
		}
	}
}
