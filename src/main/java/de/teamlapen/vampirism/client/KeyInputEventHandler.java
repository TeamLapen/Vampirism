package de.teamlapen.vampirism.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MovingObjectPosition;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent;
import de.teamlapen.vampirism.GuiHandler;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.network.InputEventPacket;
import de.teamlapen.vampirism.util.REFERENCE;
import de.teamlapen.vampirism.util.REFERENCE.KEY;

public class KeyInputEventHandler {

	public static KeyBinding SUCK = new KeyBinding(REFERENCE.KEYS.SUCK_BLOOD, Keyboard.KEY_F, REFERENCE.KEYS.CATEGORY);

	public static KeyBinding AUTO = new KeyBinding(REFERENCE.KEYS.AUTO_BLOOD, Keyboard.KEY_B, REFERENCE.KEYS.CATEGORY);

	public static KeyBinding SKILL = new KeyBinding(REFERENCE.KEYS.TOGGLE_SKILLS, -98, REFERENCE.KEYS.CATEGORY);

	private static KEY getPressedKeyBinding() {
		if (SUCK.isPressed()) {
			return KEY.SUCK;
		} else if (AUTO.isPressed()) {
			return KEY.AUTO;
		} else if (SKILL.isPressed()) {
			return KEY.SKILL;
		}
		return KEY.UNKNOWN;
	}

	public static boolean isKeyDown(int k) {
		if (k >= 0) {
			return Keyboard.isKeyDown(k);
		} else {
			return Mouse.isButtonDown(k + 100);
		}
	}

	@SubscribeEvent
	public void handleInputEvent(InputEvent event) {
		KEY keyPressed = getPressedKeyBinding(); // Only call isPressed once, so
													// get value here!
		if (keyPressed == KEY.SUCK) {
			MovingObjectPosition mouseOver = Minecraft.getMinecraft().objectMouseOver;
			if (mouseOver != null && mouseOver.entityHit != null) {
				VampirismMod.modChannel.sendToServer(new InputEventPacket(InputEventPacket.SUCKBLOOD, "" + mouseOver.entityHit.getEntityId()));
			}
		} else if (keyPressed == KEY.AUTO) {
			VampirismMod.modChannel.sendToServer(new InputEventPacket(InputEventPacket.TOGGLEAUTOFILLBLOOD, "0"));
		} else if (keyPressed == KEY.SKILL) {
			EntityPlayer player = Minecraft.getMinecraft().thePlayer;
			player.openGui(VampirismMod.instance, GuiHandler.ID_SKILL, player.worldObj, player.chunkCoordX, player.chunkCoordY, player.chunkCoordZ);
		}
	}
}
