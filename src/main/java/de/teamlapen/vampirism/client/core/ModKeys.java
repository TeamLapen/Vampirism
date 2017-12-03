package de.teamlapen.vampirism.client.core;

import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import de.teamlapen.vampirism.network.InputEventPacket;
import de.teamlapen.vampirism.network.ModGuiHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.client.settings.KeyModifier;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

/**
 * Handles all key/input related stuff
 */
@SideOnly(Side.CLIENT)
public class ModKeys {

    private static final String CATEGORY = "keys.vampirism.category";
    private static final String SUCK_BLOOD = "keys.vampirism.suck";
    //    private static final String AUTO_BLOOD = "keys.vampirism.auto";
    private static final String TOGGLE_ACTIONS = "keys.vampirism.action";
    private static final String SELECT_SKILLS = "keys.vampirism.select_skills";
    private static final String SWITCH_VISION = "keys.vampirism.vision";
    private static final String BLOOD_POTION_CRAFTING = "keys.vampirism.blood_potion_crafting";

    private static KeyBinding SUCK = new KeyBinding(SUCK_BLOOD, KeyConflictContext.IN_GAME, Keyboard.KEY_V, CATEGORY);
    private static KeyBinding ACTION = new KeyBinding(TOGGLE_ACTIONS, Keyboard.KEY_R, CATEGORY);//Middle Mouse -98
    private static KeyBinding SKILL = new KeyBinding(SELECT_SKILLS, Keyboard.KEY_P, CATEGORY);
    private static KeyBinding VISION = new KeyBinding(SWITCH_VISION, KeyConflictContext.IN_GAME, Keyboard.KEY_N, CATEGORY);
    private static KeyBinding BLOOD_POTION = new KeyBinding(BLOOD_POTION_CRAFTING, KeyConflictContext.IN_GAME, KeyModifier.CONTROL, Keyboard.KEY_B, CATEGORY);

    /**
     * @param key
     * @return the key code which is currently bound to the given KEY_Action
     */
    public static int getKeyCode(KEY key) {
        switch (key) {
            case SUCK:
                return SUCK.getKeyCode();
            case ACTION:
                return ACTION.getKeyCode();
            case SKILL:
                return SKILL.getKeyCode();
            case VISION:
                return VISION.getKeyCode();
            case BLOOD_POTION:
                return BLOOD_POTION.getKeyCode();
            default:
                return 0;
        }
    }

    /**
     * @param k if the number is negative it is interpreted as mousekey and 100 is added
     * @return Whether the key is down or not
     */
    public static boolean isKeyDown(int k) {
        if (k >= 0) {
            return Keyboard.isKeyDown(k);
        } else {
            return Mouse.isButtonDown(k + 100);
        }
    }


    public static void register() {
        MinecraftForge.EVENT_BUS.register(new ModKeys());
        ClientRegistry.registerKeyBinding(ACTION);
        ClientRegistry.registerKeyBinding(SUCK);
        ClientRegistry.registerKeyBinding(SKILL);
        ClientRegistry.registerKeyBinding(VISION);
        ClientRegistry.registerKeyBinding(BLOOD_POTION);
    }

    private ModKeys() {

    }

    @SubscribeEvent
    public void handleInputEvent(InputEvent event) {
        KEY keyPressed = getPressedKeyBinding(); // Only call isPressed once, so
        // get value here!
        if (keyPressed == KEY.SUCK) {
            RayTraceResult mouseOver = Minecraft.getMinecraft().objectMouseOver;
            if (mouseOver != null && !Minecraft.getMinecraft().player.isSpectator()) {
                if (mouseOver.entityHit != null) {
                    VampirismMod.dispatcher.sendToServer(new InputEventPacket(InputEventPacket.SUCKBLOOD, "" + mouseOver.entityHit.getEntityId()));
                } else if (mouseOver.typeOfHit == RayTraceResult.Type.BLOCK) {
                    BlockPos pos = mouseOver.getBlockPos();
                    VampirismMod.dispatcher.sendToServer(new InputEventPacket(InputEventPacket.DRINK_BLOOD_BLOCK, "" + pos.getX() + ":" + pos.getY() + ":" + pos.getZ()));
                }
            }
        } else if (keyPressed == KEY.ACTION) {
            EntityPlayer player = Minecraft.getMinecraft().player;
            if (FactionPlayerHandler.get(player).getCurrentFaction() != null) {
                player.openGui(VampirismMod.instance, ModGuiHandler.ID_ACTION, player.getEntityWorld(), player.getPosition().getX(), player.getPosition().getY(), player.getPosition().getZ());
            }
        } else if (keyPressed == KEY.SKILL) {
            EntityPlayer player = Minecraft.getMinecraft().player;
            if (FactionPlayerHandler.get(player).getCurrentFaction() != null) {
                player.openGui(VampirismMod.instance, ModGuiHandler.ID_SKILL, player.getEntityWorld(), player.getPosition().getX(), player.getPosition().getY(), player.getPosition().getZ());
            }
        } else if (keyPressed == KEY.VISION) {
            VampirismMod.dispatcher.sendToServer(new InputEventPacket(InputEventPacket.VAMPIRE_VISION_TOGGLE, ""));
        } else if (keyPressed == KEY.BLOOD_POTION) {
            VampirismMod.dispatcher.sendToServer(new InputEventPacket(InputEventPacket.OPEN_BLOOD_POTION, ""));
        }
    }

    /**
     * @return the KeyBinding that is currently pressed
     */
    private KEY getPressedKeyBinding() {
        if (SUCK.isPressed()) {
            return KEY.SUCK;
        } else if (ACTION.isPressed()) {
            return KEY.ACTION;
        } else if (SKILL.isPressed()) {
            return KEY.SKILL;
        } else if (VISION.isPressed()) {
            return KEY.VISION;
        } else if (BLOOD_POTION.isPressed()) {
            return KEY.BLOOD_POTION;
        }
        return KEY.UNKNOWN;
    }

    public enum KEY {
        SUCK, UNKNOWN, ACTION, SKILL, VISION, BLOOD_POTION
    }
}
