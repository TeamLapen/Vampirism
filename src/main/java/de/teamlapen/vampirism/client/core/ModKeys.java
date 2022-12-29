package de.teamlapen.vampirism.client.core;

import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import de.teamlapen.vampirism.api.entity.player.actions.IAction;
import de.teamlapen.vampirism.client.gui.SelectActionScreen;
import de.teamlapen.vampirism.client.gui.SelectMinionTaskScreen;
import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import de.teamlapen.vampirism.network.CSimpleInputEvent;
import de.teamlapen.vampirism.network.CStartFeedingPacket;
import de.teamlapen.vampirism.network.CToggleActionPacket;
import de.teamlapen.vampirism.player.VampirismPlayerAttributes;
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
public class ModKeys { //TODO 1.17 revamp to skip using the KEY enum for getting the keybindings

    private static final Logger LOGGER = LogManager.getLogger(ModKeys.class);
    /**
     * Time between multiple action button presses in ms
     */
    private static final long ACTION_BUTTON_COOLDOWN = 500;

    private static final String CATEGORY = "keys.vampirism.category";
    private static final String SUCK_BLOOD = "keys.vampirism.suck";
    //    private static final String AUTO_BLOOD = "keys.vampirism.auto";
    private static final String TOGGLE_ACTIONS = "keys.vampirism.action";
    private static final String SELECT_SKILLS = "keys.vampirism.select_skills";
    private static final String SWITCH_VISION = "keys.vampirism.vision";
    private static final String ACTIVATE_ACTION1 = "keys.vampirism.action1";
    private static final String ACTIVATE_ACTION2 = "keys.vampirism.action2";
    private static final String ACTIVATE_ACTION3 = "keys.vampirism.action3";

    private static final String MINION_TASK = "keys.vampirism.minion_task";

    private static final KeyBinding SUCK = new KeyBinding(SUCK_BLOOD, KeyConflictContext.IN_GAME, InputMappings.Type.KEYSYM, GLFW.GLFW_KEY_V, CATEGORY);
    private static final KeyBinding ACTION = new KeyBinding(TOGGLE_ACTIONS, InputMappings.Type.KEYSYM, GLFW.GLFW_KEY_R, CATEGORY);//Middle Mouse -98
    /**
     * TODO 1.17 refractor to be VAMPIRISM_MENU. Same for other related constants
     */
    private static final KeyBinding SKILL = new KeyBinding(SELECT_SKILLS, InputMappings.Type.KEYSYM, GLFW.GLFW_KEY_P, CATEGORY);
    private static final KeyBinding VISION = new KeyBinding(SWITCH_VISION, KeyConflictContext.IN_GAME, InputMappings.Type.KEYSYM, GLFW.GLFW_KEY_N, CATEGORY);
    private static final KeyBinding ACTION1 = new KeyBinding(ACTIVATE_ACTION1, KeyConflictContext.IN_GAME, KeyModifier.ALT, InputMappings.Type.KEYSYM, GLFW.GLFW_KEY_1, CATEGORY);
    private static final KeyBinding ACTION2 = new KeyBinding(ACTIVATE_ACTION2, KeyConflictContext.IN_GAME, KeyModifier.ALT, InputMappings.Type.KEYSYM, GLFW.GLFW_KEY_2, CATEGORY);
    private static final KeyBinding ACTION3 = new KeyBinding(ACTIVATE_ACTION3, KeyConflictContext.IN_GAME, KeyModifier.ALT, InputMappings.Type.KEYSYM, GLFW.GLFW_KEY_3, CATEGORY);

    private static final KeyBinding MINION = new KeyBinding(MINION_TASK, KeyConflictContext.IN_GAME, InputMappings.UNKNOWN, CATEGORY);

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
            case ACTION1:
                return ACTION1;
            case ACTION2:
                return ACTION2;
            case ACTION3:
                return ACTION3;
            case MINION:
                return MINION;
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
        ClientRegistry.registerKeyBinding(ACTION1);
        ClientRegistry.registerKeyBinding(ACTION2);
        ClientRegistry.registerKeyBinding(ACTION3);
        ClientRegistry.registerKeyBinding(MINION);
    }

    private boolean suckKeyDown = false;
    private long lastAction1Trigger = 0;
    private long lastAction2Trigger = 0;
    private long lastAction3Trigger = 0;


    private ModKeys() {

    }

    @SubscribeEvent
    public void handleInputEvent(InputEvent event) {
        KEY keyPressed = getPressedKeyBinding(); // Only call isPressed once, so
        // get value here!
        if (!suckKeyDown && keyPressed == KEY.SUCK) {
            RayTraceResult mouseOver = Minecraft.getInstance().hitResult;
            suckKeyDown = true;
            PlayerEntity player = Minecraft.getInstance().player;
            if (mouseOver != null && !player.isSpectator() && VampirePlayer.getOpt(player).map(vp -> vp.getLevel() > 0 && !vp.getActionHandler().isActionActive(VampireActions.BAT.get())).orElse(false)) {
                if (mouseOver instanceof EntityRayTraceResult) {
                    VampirismMod.dispatcher.sendToServer(new CStartFeedingPacket(((EntityRayTraceResult) mouseOver).getEntity().getId()));
                } else if (mouseOver instanceof BlockRayTraceResult) {
                    BlockPos pos = ((BlockRayTraceResult) mouseOver).getBlockPos();
                    VampirismMod.dispatcher.sendToServer(new CStartFeedingPacket(pos));
                } else {
                    LOGGER.warn("Unknown mouse over type while trying to feed");
                }
            }
        } else if (keyPressed == KEY.ACTION) {
            if (Minecraft.getInstance().player.isAlive() && !Minecraft.getInstance().player.isSpectator()) {
                IPlayableFaction<?> faction = VampirismPlayerAttributes.get(Minecraft.getInstance().player).faction;
                if (faction != null) {
                    Minecraft.getInstance().setScreen(new SelectActionScreen(faction.getColor(), false));
                }
            }
        } else if (keyPressed == KEY.SKILL) {
            VampirismMod.dispatcher.sendToServer(new CSimpleInputEvent(CSimpleInputEvent.Type.VAMPIRISM_MENU));
        } else if (keyPressed == KEY.VISION) {
            VampirismMod.dispatcher.sendToServer(new CSimpleInputEvent(CSimpleInputEvent.Type.TOGGLE_VAMPIRE_VISION));
        } else if (keyPressed == KEY.ACTION1) {
            long t = System.currentTimeMillis();
            if (t - lastAction1Trigger > ACTION_BUTTON_COOLDOWN) {
                lastAction1Trigger = System.currentTimeMillis();
                PlayerEntity player = Minecraft.getInstance().player;
                if (player.isAlive()) {
                    FactionPlayerHandler.getOpt(player).ifPresent(factionHandler -> factionHandler.getCurrentFactionPlayer().ifPresent(factionPlayer -> toggleBoundAction(factionPlayer, factionHandler.getBoundAction(1))));
                }
            }

        } else if (keyPressed == KEY.ACTION2 && !Minecraft.getInstance().player.isSpectator()) {
            long t = System.currentTimeMillis();
            if (t - lastAction2Trigger > ACTION_BUTTON_COOLDOWN) {
                lastAction2Trigger = System.currentTimeMillis();
                PlayerEntity player = Minecraft.getInstance().player;
                if (player.isAlive()) {
                    FactionPlayerHandler.getOpt(player).ifPresent(factionHandler -> factionHandler.getCurrentFactionPlayer().ifPresent(factionPlayer -> toggleBoundAction(factionPlayer, factionHandler.getBoundAction(2))));
                }
            }

        } else if (keyPressed == KEY.ACTION3 && !Minecraft.getInstance().player.isSpectator()) {
            long t = System.currentTimeMillis();
            if (t - lastAction3Trigger > ACTION_BUTTON_COOLDOWN) {
                lastAction3Trigger = System.currentTimeMillis();
                PlayerEntity player = Minecraft.getInstance().player;
                if (player.isAlive()) {
                    FactionPlayerHandler.getOpt(player).ifPresent(factionHandler -> factionHandler.getCurrentFactionPlayer().ifPresent(factionPlayer -> toggleBoundAction(factionPlayer, factionHandler.getBoundAction(3))));
                }
            }

        } else if (keyPressed == KEY.MINION && !Minecraft.getInstance().player.isSpectator()) {
            if (FactionPlayerHandler.getOpt(Minecraft.getInstance().player).map(FactionPlayerHandler::getLordLevel).orElse(0) > 0) {
                Minecraft.getInstance().setScreen(new SelectMinionTaskScreen());
            }
        }
        if (suckKeyDown && !SUCK.isDown()) {
            suckKeyDown = false;
            VampirismMod.dispatcher.sendToServer(new CSimpleInputEvent(CSimpleInputEvent.Type.FINISH_SUCK_BLOOD));
        }
    }

    /**
     * @return the KeyBinding that is currently pressed
     */
    private KEY getPressedKeyBinding() {
        if (SUCK.isDown()) {
            return KEY.SUCK;
        } else if (ACTION.isDown()) {
            return KEY.ACTION;
        } else if (SKILL.isDown()) {
            return KEY.SKILL;
        } else if (VISION.isDown()) {
            return KEY.VISION;
        } else if (ACTION1.isDown()) {
            return KEY.ACTION1;
        } else if (ACTION2.isDown()) {
            return KEY.ACTION2;
        } else if (ACTION3.isDown()) {
            return KEY.ACTION3;
        } else if (MINION.isDown()) {
            return KEY.MINION;
        }
        return KEY.UNKNOWN;
    }

    /**
     * Try to toggle the given action
     **/
    private void toggleBoundAction(@Nonnull IFactionPlayer player, @Nullable IAction action) {
        if (action == null) {
            player.getRepresentingPlayer().displayClientMessage(new TranslationTextComponent("text.vampirism.action.not_bound", "/vampirism bind-action"), true);
        } else {
            if (!action.getFaction().equals(player.getFaction())) {
                player.getRepresentingPlayer().displayClientMessage(new TranslationTextComponent("text.vampirism.action.only_faction", action.getFaction().getName()), true);
            } else {
                VampirismMod.dispatcher.sendToServer(CToggleActionPacket.createFromRaytrace(action.getRegistryName(), Minecraft.getInstance().hitResult));
            }
        }

    }

    public enum KEY {
        SUCK, UNKNOWN, ACTION, SKILL, VISION, ACTION1, ACTION2, ACTION3, MINION
    }
}
