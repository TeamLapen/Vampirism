package de.teamlapen.vampirism.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.MovingObjectPosition;

import org.lwjgl.input.Keyboard;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.playervampire.SuckBloodPacket;
import de.teamlapen.vampirism.util.Logger;
import de.teamlapen.vampirism.util.REFERENCE;
import de.teamlapen.vampirism.util.REFERENCE.KEY;

public class KeyInputEventHandler {

	public static KeyBinding SUCK= new KeyBinding(REFERENCE.KEYS.SUCK_BLOOD,Keyboard.KEY_F,REFERENCE.KEYS.CATEGORY);
	
	@SubscribeEvent
	public void handleKeyInput(InputEvent.KeyInputEvent event){
		if(getPressedKeyBinding()==KEY.SUCK){
			MovingObjectPosition mouseOver=Minecraft.getMinecraft().objectMouseOver;
			if(mouseOver!=null&&mouseOver.entityHit!=null){
				VampirismMod.modChannel.sendToServer(new SuckBloodPacket(mouseOver.entityHit.getEntityId()));
			}
			
		}
	}
	
	private static KEY getPressedKeyBinding(){
		if(SUCK.isPressed()){
			return KEY.SUCK;
		}
		
		return KEY.UNKNOWN;
	}
}
