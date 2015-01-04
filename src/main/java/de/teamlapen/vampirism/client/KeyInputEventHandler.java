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

	public static KeyBinding SUCK = new KeyBinding(REFERENCE.KEYS.SUCK_BLOOD, Keyboard.KEY_F, REFERENCE.KEYS.CATEGORY);
	public static KeyBinding AUTO = new KeyBinding(REFERENCE.KEYS.AUTO_BLOOD, Keyboard.KEY_B, REFERENCE.KEYS.CATEGORY);

	private static KEY getPressedKeyBinding() {
		if (SUCK.isPressed()) {
			return KEY.SUCK;
		}
		else if (AUTO.isPressed()) {
			return KEY.AUTO;
		}
		return KEY.UNKNOWN;
	}

	@SubscribeEvent
	public void handleKeyInput(InputEvent.KeyInputEvent event) {
		KEY keyPressed = getPressedKeyBinding(); // Only call isPressed once, so get value here!
		if (keyPressed == KEY.SUCK) {
			MovingObjectPosition mouseOver = Minecraft.getMinecraft().objectMouseOver;
			if (mouseOver != null && mouseOver.entityHit != null) {
				VampirismMod.modChannel.sendToServer(new InputEventPacket(InputEventPacket.SUCKBLOOD, "" + mouseOver.entityHit.getEntityId()));
			}
			Logger.i(REFERENCE.MODID, "SUCK Key Pressed!");
		} else if (keyPressed == KEY.AUTO) {
			VampirismMod.modChannel.sendToServer(new InputEventPacket(InputEventPacket.TOGGLEAUTOFILLBLOOD, "0"));
			Logger.i(REFERENCE.MODID, "AUTO Key Pressed!");
		}
	}
}
