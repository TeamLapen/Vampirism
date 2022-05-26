package de.teamlapen.vampirism.client.core;

import com.mojang.blaze3d.platform.InputConstants;
import de.teamlapen.lib.util.Color;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import de.teamlapen.vampirism.api.entity.player.actions.IAction;
import de.teamlapen.vampirism.client.gui.ActionSelectScreen;
import de.teamlapen.vampirism.client.gui.SelectMinionTaskScreen;
import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import de.teamlapen.vampirism.network.InputEventPacket;
import de.teamlapen.vampirism.player.VampirismPlayerAttributes;
import de.teamlapen.vampirism.player.vampire.VampirePlayer;
import de.teamlapen.vampirism.player.vampire.actions.VampireActions;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.ClientRegistry;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.client.settings.KeyModifier;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
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

    private static final Logger LOGGER = LogManager.getLogger();
    /**
     * Time between multiple action button presses in ms
     */
    private static final long ACTION_BUTTON_COOLDOWN = 500;

    private static final String CATEGORY = "keys.vampirism.category";
    private static final String SUCK_BLOOD = "keys.vampirism.suck";
    //    private static final String AUTO_BLOOD = "keys.vampirism.auto";
    private static final String TOGGLE_ACTIONS = "keys.vampirism.action";
    private static final String OPEN_VAMPIRISM_MENU = "keys.vampirism.select_skills";
    private static final String SWITCH_VISION = "keys.vampirism.vision";
    private static final String ACTIVATE_ACTION1 = "keys.vampirism.action1";
    private static final String ACTIVATE_ACTION2 = "keys.vampirism.action2";
    private static final String ACTIVATE_ACTION3 = "keys.vampirism.action3";

    private static final String MINION_TASK = "keys.vampirism.minion_task";

    public static final KeyMapping SUCK = new KeyMapping(SUCK_BLOOD, KeyConflictContext.IN_GAME, InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_V, CATEGORY);
    public static final KeyMapping ACTION = new KeyMapping(TOGGLE_ACTIONS, InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_R, CATEGORY);//Middle Mouse -98
    public static final KeyMapping VAMPIRISM_MENU = new KeyMapping(OPEN_VAMPIRISM_MENU, InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_P, CATEGORY);
    public static final KeyMapping VISION = new KeyMapping(SWITCH_VISION, KeyConflictContext.IN_GAME, InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_N, CATEGORY);
    public static final KeyMapping ACTION1 = new KeyMapping(ACTIVATE_ACTION1, KeyConflictContext.IN_GAME, KeyModifier.ALT, InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_1, CATEGORY);
    public static final KeyMapping ACTION2 = new KeyMapping(ACTIVATE_ACTION2, KeyConflictContext.IN_GAME, KeyModifier.ALT, InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_2, CATEGORY);
    public static final KeyMapping ACTION3 = new KeyMapping(ACTIVATE_ACTION3, KeyConflictContext.IN_GAME, KeyModifier.ALT, InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_3, CATEGORY);
    public static final KeyMapping MINION = new KeyMapping(MINION_TASK, KeyConflictContext.IN_GAME, InputConstants.UNKNOWN, CATEGORY);

    public static void register() {
        MinecraftForge.EVENT_BUS.register(new ModKeys());
        ClientRegistry.registerKeyBinding(ACTION);
        ClientRegistry.registerKeyBinding(SUCK);
        ClientRegistry.registerKeyBinding(VAMPIRISM_MENU);
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
        if (SUCK.isDown()) {
            if (!suckKeyDown) {
                HitResult mouseOver = Minecraft.getInstance().hitResult;
                suckKeyDown = true;
                Player player = Minecraft.getInstance().player;
                if (mouseOver != null && !player.isSpectator() && VampirePlayer.getOpt(player).map(vp -> vp.getLevel() > 0 && !vp.getActionHandler().isActionActive(VampireActions.bat.get())).orElse(false)) {
                    if (mouseOver instanceof EntityHitResult) {
                        VampirismMod.dispatcher.sendToServer(new InputEventPacket(InputEventPacket.SUCKBLOOD, "" + ((EntityHitResult) mouseOver).getEntity().getId()));
                    } else if (mouseOver instanceof BlockHitResult) {
                        BlockPos pos = ((BlockHitResult) mouseOver).getBlockPos();
                        VampirismMod.dispatcher.sendToServer(new InputEventPacket(InputEventPacket.DRINK_BLOOD_BLOCK, "" + pos.getX() + ":" + pos.getY() + ":" + pos.getZ()));
                    } else {
                        VampirismMod.dispatcher.sendToServer(new InputEventPacket(InputEventPacket.SUCKBLOOD, "" + -1));
                    }
                }
            }
        } else {
            if (suckKeyDown) {
                suckKeyDown = false;
                VampirismMod.dispatcher.sendToServer(new InputEventPacket(InputEventPacket.ENDSUCKBLOOD, ""));
            }

            if (ACTION.isDown()) {
                if (Minecraft.getInstance().player.isAlive()) {
                    IPlayableFaction<?> faction = VampirismPlayerAttributes.get(Minecraft.getInstance().player).faction;
                    if (faction != null) {
                        Minecraft.getInstance().setScreen(new ActionSelectScreen<>(new Color(faction.getColor()), false));
                    }
                }
            } else if (VAMPIRISM_MENU.isDown()) {
                VampirismMod.dispatcher.sendToServer(new InputEventPacket(InputEventPacket.OPEN_VAMPIRISM_MENU, ""));
            } else if (VISION.isDown()) {
                VampirismMod.dispatcher.sendToServer(new InputEventPacket(InputEventPacket.VAMPIRE_VISION_TOGGLE, ""));
            } else if (ACTION1.isDown()) {
                long t = System.currentTimeMillis();
                if (t - lastAction1Trigger > ACTION_BUTTON_COOLDOWN) {
                    lastAction1Trigger = System.currentTimeMillis();
                    Player player = Minecraft.getInstance().player;
                    if (player.isAlive()) {
                        FactionPlayerHandler.getOpt(player).ifPresent(factionHandler -> factionHandler.getCurrentFactionPlayer().ifPresent(factionPlayer -> toggleBoundAction(factionPlayer, factionHandler.getBoundAction(1))));
                    }
                }

            } else if (ACTION2.isDown()) {
                long t = System.currentTimeMillis();
                if (t - lastAction2Trigger > ACTION_BUTTON_COOLDOWN) {
                    lastAction2Trigger = System.currentTimeMillis();
                    Player player = Minecraft.getInstance().player;
                    if (player.isAlive()) {
                        FactionPlayerHandler.getOpt(player).ifPresent(factionHandler -> factionHandler.getCurrentFactionPlayer().ifPresent(factionPlayer -> toggleBoundAction(factionPlayer, factionHandler.getBoundAction(2))));
                    }
                }

            } else if (ACTION3.isDown()) {
                long t = System.currentTimeMillis();
                if (t - lastAction3Trigger > ACTION_BUTTON_COOLDOWN) {
                    lastAction3Trigger = System.currentTimeMillis();
                    Player player = Minecraft.getInstance().player;
                    if (player.isAlive()) {
                        FactionPlayerHandler.getOpt(player).ifPresent(factionHandler -> factionHandler.getCurrentFactionPlayer().ifPresent(factionPlayer -> toggleBoundAction(factionPlayer, factionHandler.getBoundAction(3))));
                    }
                }

            } else if (MINION.isDown()) {
                if (FactionPlayerHandler.getOpt(Minecraft.getInstance().player).map(FactionPlayerHandler::getLordLevel).orElse(0) > 0) {
                    Minecraft.getInstance().setScreen(new SelectMinionTaskScreen());
                }
            }
        }
    }

    /**
     * Try to toggle the given action
     **/
    private void toggleBoundAction(@Nonnull IFactionPlayer<?> player, @Nullable IAction<?> action) {
        if (action == null) {
            player.getRepresentingPlayer().displayClientMessage(new TranslatableComponent("text.vampirism.action.not_bound", "/vampirism bind-action"), true);
        } else {
            if (!action.getFaction().equals(player.getFaction())) {
                player.getRepresentingPlayer().displayClientMessage(new TranslatableComponent("text.vampirism.action.only_faction", action.getFaction().getName()), true);
            } else {
                VampirismMod.dispatcher.sendToServer(new InputEventPacket(InputEventPacket.TOGGLEACTION, "" + action.getRegistryName()));
            }
        }

    }
}
