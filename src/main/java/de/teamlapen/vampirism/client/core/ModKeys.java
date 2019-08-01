package de.teamlapen.vampirism.client.core;

import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import de.teamlapen.vampirism.api.entity.player.actions.IAction;
import de.teamlapen.vampirism.client.gui.SelectActionScreen;
import de.teamlapen.vampirism.client.gui.SkillsScreen;
import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import de.teamlapen.vampirism.network.InputEventPacket;
import de.teamlapen.vampirism.player.vampire.VampirePlayer;
import de.teamlapen.vampirism.player.vampire.actions.VampireActions;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.client.settings.KeyModifier;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.glfw.GLFW;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Handles all key/input related stuff
 */
@OnlyIn(Dist.CLIENT)
public class ModKeys {

    private static final Logger LOGGER = LogManager.getLogger(ModKeys.class);

    private static final String CATEGORY = "keys.vampirism.category";
    private static final String SUCK_BLOOD = "keys.vampirism.suck";
    //    private static final String AUTO_BLOOD = "keys.vampirism.auto";
    private static final String TOGGLE_ACTIONS = "keys.vampirism.action";
    private static final String SELECT_SKILLS = "keys.vampirism.select_skills";
    private static final String SWITCH_VISION = "keys.vampirism.vision";
    private static final String BLOOD_POTION_CRAFTING = "keys.vampirism.blood_potion_crafting";
    private static final String ACTIVATE_ACTION1 = "keys.vampirism.action1";
    private static final String ACTIVATE_ACTION2 = "keys.vampirism.action2";

    private static KeyBinding SUCK = new KeyBinding(SUCK_BLOOD, KeyConflictContext.IN_GAME, InputMappings.Type.KEYSYM, GLFW.GLFW_KEY_V, CATEGORY);
    private static KeyBinding ACTION = new KeyBinding(TOGGLE_ACTIONS, InputMappings.Type.KEYSYM, GLFW.GLFW_KEY_R, CATEGORY);//Middle Mouse -98
    private static KeyBinding SKILL = new KeyBinding(SELECT_SKILLS, InputMappings.Type.KEYSYM, GLFW.GLFW_KEY_P, CATEGORY);
    private static KeyBinding VISION = new KeyBinding(SWITCH_VISION, KeyConflictContext.IN_GAME, InputMappings.Type.KEYSYM, GLFW.GLFW_KEY_N, CATEGORY);
    private static KeyBinding BLOOD_POTION = new KeyBinding(BLOOD_POTION_CRAFTING, KeyConflictContext.IN_GAME, KeyModifier.CONTROL, InputMappings.Type.KEYSYM, GLFW.GLFW_KEY_B, CATEGORY);
    private static KeyBinding ACTION1 = new KeyBinding(ACTIVATE_ACTION1, KeyConflictContext.IN_GAME, KeyModifier.ALT, InputMappings.Type.KEYSYM, GLFW.GLFW_KEY_1, CATEGORY);
    private static KeyBinding ACTION2 = new KeyBinding(ACTIVATE_ACTION2, KeyConflictContext.IN_GAME, KeyModifier.ALT, InputMappings.Type.KEYSYM, GLFW.GLFW_KEY_2, CATEGORY);


    @Nonnull
    public static KeyBinding getKeyBinding(@Nonnull KEY key) {
        switch (key) {
            case SUCK:
                return SUCK;
            case ACTION:
                return ACTION;
            case SKILL:
                return SKILL;
            case VISION:
                return VISION;
            case BLOOD_POTION:
                return BLOOD_POTION;
            case ACTION1:
                return ACTION1;
            case ACTION2:
                return ACTION2;
            default:
                LOGGER.error("Keybinding {} does not exist", key);
                return ACTION;
        }
    }

    public static void register() {
        MinecraftForge.EVENT_BUS.register(new ModKeys());
        ClientRegistry.registerKeyBinding(ACTION);
        ClientRegistry.registerKeyBinding(SUCK);
        ClientRegistry.registerKeyBinding(SKILL);
        ClientRegistry.registerKeyBinding(VISION);
        ClientRegistry.registerKeyBinding(BLOOD_POTION);
        ClientRegistry.registerKeyBinding(ACTION1);
        ClientRegistry.registerKeyBinding(ACTION2);
    }

    private boolean suckKeyDown = false;

    private ModKeys() {

    }

    @SubscribeEvent
    public void handleInputEvent(InputEvent event) {
        KEY keyPressed = getPressedKeyBinding(); // Only call isPressed once, so
        // get value here!
        if (!suckKeyDown && keyPressed == KEY.SUCK) {
            RayTraceResult mouseOver = Minecraft.getInstance().objectMouseOver;
            suckKeyDown = true;
            PlayerEntity player = Minecraft.getInstance().player;
            if (mouseOver != null && !player.isSpectator() && FactionPlayerHandler.get(player).isInFaction(VReference.VAMPIRE_FACTION) && !VampirePlayer.get(player).getActionHandler().isActionActive(VampireActions.bat)) {
                if (mouseOver instanceof EntityRayTraceResult) {
                    VampirismMod.dispatcher.sendToServer(new InputEventPacket(InputEventPacket.SUCKBLOOD, "" + ((EntityRayTraceResult) mouseOver).getEntity().getEntityId()));
                } else if (mouseOver instanceof BlockRayTraceResult) {
                    BlockPos pos = ((BlockRayTraceResult) mouseOver).getPos();
                    VampirismMod.dispatcher.sendToServer(new InputEventPacket(InputEventPacket.DRINK_BLOOD_BLOCK, "" + pos.getX() + ":" + pos.getY() + ":" + pos.getZ()));
                } else {
                    VampirismMod.dispatcher.sendToServer(new InputEventPacket(InputEventPacket.SUCKBLOOD, "" + -1));
                }
            }
        } else if (keyPressed == KEY.ACTION) {
            PlayerEntity player = Minecraft.getInstance().player;
            if (FactionPlayerHandler.get(player).getCurrentFaction() != null) {
                Minecraft.getInstance().displayGuiScreen(new SelectActionScreen());
            }
        } else if (keyPressed == KEY.SKILL) {
            PlayerEntity player = Minecraft.getInstance().player;
            if (FactionPlayerHandler.get(player).getCurrentFaction() != null) {
                Minecraft.getInstance().displayGuiScreen(new SkillsScreen());
            }
        } else if (keyPressed == KEY.VISION) {
            VampirismMod.dispatcher.sendToServer(new InputEventPacket(InputEventPacket.VAMPIRE_VISION_TOGGLE, ""));
        } else if (keyPressed == KEY.BLOOD_POTION) {
            VampirismMod.dispatcher.sendToServer(new InputEventPacket(InputEventPacket.OPEN_BLOOD_POTION, ""));
        } else if (keyPressed == KEY.ACTION1) {
            FactionPlayerHandler factionHandler = FactionPlayerHandler.get(Minecraft.getInstance().player);
            toggleBoundAction(factionHandler.getCurrentFactionPlayer(), factionHandler.getBoundAction1());
        } else if (keyPressed == KEY.ACTION2) {
            FactionPlayerHandler factionHandler = FactionPlayerHandler.get(Minecraft.getInstance().player);
            toggleBoundAction(factionHandler.getCurrentFactionPlayer(), factionHandler.getBoundAction2());
        }
        if (suckKeyDown && !SUCK.isKeyDown()) {
            suckKeyDown = false;
            VampirismMod.dispatcher.sendToServer(new InputEventPacket(InputEventPacket.ENDSUCKBLOOD, ""));
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
        } else if (ACTION1.isPressed()) {
            return KEY.ACTION1;
        } else if (ACTION2.isPressed()) {
            return KEY.ACTION2;
        }
        return KEY.UNKNOWN;
    }

    /**
     * Try to toggle the given action
     *
     * @param player if null nothing happens
     */
    private void toggleBoundAction(@Nullable IFactionPlayer player, @Nullable IAction action) {
        if (player != null) {
            if (action == null) {
                player.getRepresentingPlayer().sendStatusMessage(new TranslationTextComponent("text.vampirism.action.not_bound", "/vampirism bind-action"), true);
            } else {
                if (!action.getFaction().equals(player.getFaction())) {
                    player.getRepresentingPlayer().sendStatusMessage(new TranslationTextComponent("text.vampirism.action.only_faction", action.getFaction().getName()), true);
                } else {
                    VampirismMod.dispatcher.sendToServer(new InputEventPacket(InputEventPacket.TOGGLEACTION, "" + action.getRegistryName()));
                }
            }
        }
    }

    public enum KEY {
        SUCK, UNKNOWN, ACTION, SKILL, VISION, BLOOD_POTION, ACTION1, ACTION2
    }
}
