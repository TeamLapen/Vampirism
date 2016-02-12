package de.teamlapen.vampirism.client.core;

import de.teamlapen.lib.lib.util.IInitListener;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.network.InputEventPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.MovingObjectPosition;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLStateEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

/**
 * Handles all key/input related stuff
 */
public class ModKeys {

    private static final String CATEGORY = "keys.vampirism.category";
    private static final String SUCK_BLOOD = "keys.vampirism.suck";
    //    private static final String AUTO_BLOOD = "keys.vampirism.auto";
    private static final String TOGGLE_SKILLS = "keys.vampirism.skill";
//    private static final String SWITCH_VISION = "key.vampirism.vision";
//    private static final String MINION_CONTROL = "key.vampirism.minion_control";

    private static KeyBinding SUCK = new KeyBinding(SUCK_BLOOD, Keyboard.KEY_F, CATEGORY);
    private static KeyBinding SKILL = new KeyBinding(TOGGLE_SKILLS, -2, CATEGORY);

    private ModKeys() {

    }

    /**
     * @param key
     * @return the key code which is currently bound to the given KEY_Action
     */
    public static int getKeyCode(KEY key) {
        switch (key) {
            case SUCK:
                return SUCK.getKeyCode();
            case SKILL:
                return SKILL.getKeyCode();
            default:
                return 0;
        }
    }

    /**
     * @param k if the number is negative it is interpreted as mousekey and multiplied with -1
     * @return Whether the key is down or not
     */
    public static boolean isKeyDown(int k) {
        if (k >= 0) {
            return Keyboard.isKeyDown(k);
        } else {
            return Mouse.isButtonDown(k * -1);
        }
    }

    public static void onInitStep(IInitListener.Step step, FMLStateEvent event) {
        switch (step) {
            case PRE_INIT:
                preInit((FMLPreInitializationEvent) event);
                break;
        }

    }

    private static void preInit(FMLPreInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(new ModKeys());
    }

    /**
     * @return the KeyBinding that is currently pressed
     */
    private KEY getPressedKeyBinding() {
        if (SUCK.isPressed()) {
            return KEY.SUCK;
        } else if (SKILL.isPressed()) {
            return KEY.SKILL;
        }
        return KEY.UNKNOWN;
    }

    @SubscribeEvent
    public void handleInputEvent(InputEvent event) {
        KEY keyPressed = getPressedKeyBinding(); // Only call isPressed once, so
        // get value here!
        if (keyPressed == KEY.SUCK) {
            MovingObjectPosition mouseOver = Minecraft.getMinecraft().objectMouseOver;
            if (mouseOver != null && mouseOver.entityHit != null) {
                VampirismMod.dispatcher.sendToServer(new InputEventPacket(InputEventPacket.SUCKBLOOD, "" + mouseOver.entityHit.getEntityId()));
            }
        }
    }

    public enum KEY {
        SUCK, UNKNOWN, SKILL
    }
}
