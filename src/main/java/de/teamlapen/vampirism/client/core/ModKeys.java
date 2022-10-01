package de.teamlapen.vampirism.client.core;

import com.google.common.collect.ImmutableMap;
import com.mojang.blaze3d.platform.InputConstants;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import de.teamlapen.vampirism.api.entity.player.actions.IAction;
import de.teamlapen.vampirism.client.gui.screens.SelectActionScreen;
import de.teamlapen.vampirism.client.gui.screens.SelectMinionTaskScreen;
import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import de.teamlapen.vampirism.entity.player.VampirismPlayerAttributes;
import de.teamlapen.vampirism.entity.player.vampire.VampirePlayer;
import de.teamlapen.vampirism.entity.player.vampire.actions.VampireActions;
import de.teamlapen.vampirism.network.ServerboundSimpleInputEvent;
import de.teamlapen.vampirism.network.ServerboundStartFeedingPacket;
import de.teamlapen.vampirism.network.ServerboundToggleActionPacket;
import de.teamlapen.vampirism.util.RegUtil;
import it.unimi.dsi.fastutil.ints.Int2LongArrayMap;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.client.settings.KeyModifier;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

import java.util.Map;

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
    private static final String TOGGLE_ACTIONS = "keys.vampirism.action";
    private static final String OPEN_VAMPIRISM_MENU = "keys.vampirism.select_skills";
    private static final String SWITCH_VISION = "keys.vampirism.vision";
    private static final String ACTIVATE_ACTION1 = "keys.vampirism.action1";
    private static final String ACTIVATE_ACTION2 = "keys.vampirism.action2";
    private static final String ACTIVATE_ACTION3 = "keys.vampirism.action3";
    private static final String ACTIVATE_ACTION4 = "keys.vampirism.action4";
    private static final String ACTIVATE_ACTION5 = "keys.vampirism.action5";
    private static final String ACTIVATE_ACTION6 = "keys.vampirism.action6";
    private static final String ACTIVATE_ACTION7 = "keys.vampirism.action7";
    private static final String ACTIVATE_ACTION8 = "keys.vampirism.action8";
    private static final String ACTIVATE_ACTION9 = "keys.vampirism.action9";
    private static final String MINION_TASK = "keys.vampirism.minion_task";

    public static final KeyMapping SUCK = new KeyMapping(SUCK_BLOOD, KeyConflictContext.IN_GAME, InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_V, CATEGORY);
    public static final KeyMapping ACTION = new KeyMapping(TOGGLE_ACTIONS, InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_R, CATEGORY);//Middle Mouse -98
    public static final KeyMapping VAMPIRISM_MENU = new KeyMapping(OPEN_VAMPIRISM_MENU, InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_P, CATEGORY);
    public static final KeyMapping VISION = new KeyMapping(SWITCH_VISION, KeyConflictContext.IN_GAME, InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_N, CATEGORY);
    public static final KeyMapping ACTION1 = new KeyMapping(ACTIVATE_ACTION1, KeyConflictContext.IN_GAME, KeyModifier.ALT, InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_1, CATEGORY);
    public static final KeyMapping ACTION2 = new KeyMapping(ACTIVATE_ACTION2, KeyConflictContext.IN_GAME, KeyModifier.ALT, InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_2, CATEGORY);
    public static final KeyMapping ACTION3 = new KeyMapping(ACTIVATE_ACTION3, KeyConflictContext.IN_GAME, KeyModifier.ALT, InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_3, CATEGORY);
    public static final KeyMapping ACTION4 = new KeyMapping(ACTIVATE_ACTION4, KeyConflictContext.IN_GAME, InputConstants.UNKNOWN, CATEGORY);
    public static final KeyMapping ACTION5 = new KeyMapping(ACTIVATE_ACTION5, KeyConflictContext.IN_GAME, InputConstants.UNKNOWN, CATEGORY);
    public static final KeyMapping ACTION6 = new KeyMapping(ACTIVATE_ACTION6, KeyConflictContext.IN_GAME, InputConstants.UNKNOWN, CATEGORY);
    public static final KeyMapping ACTION7 = new KeyMapping(ACTIVATE_ACTION7, KeyConflictContext.IN_GAME, InputConstants.UNKNOWN, CATEGORY);
    public static final KeyMapping ACTION8 = new KeyMapping(ACTIVATE_ACTION8, KeyConflictContext.IN_GAME, InputConstants.UNKNOWN, CATEGORY);
    public static final KeyMapping ACTION9 = new KeyMapping(ACTIVATE_ACTION9, KeyConflictContext.IN_GAME, InputConstants.UNKNOWN, CATEGORY);
    public static final KeyMapping MINION = new KeyMapping(MINION_TASK, KeyConflictContext.IN_GAME, InputConstants.UNKNOWN, CATEGORY);

    public static final Map<Integer, KeyMapping> ACTION_KEYS = Map.of(1,ACTION1, 2,ACTION2, 3,ACTION3, 4,ACTION4, 5,ACTION5, 6,ACTION6, 7,ACTION7, 8,ACTION8, 9,ACTION9);

    static void registerKeyMapping(@NotNull RegisterKeyMappingsEvent event) {
        event.register(ACTION);
        event.register(SUCK);
        event.register(VAMPIRISM_MENU);
        event.register(VISION);
        event.register(MINION);
        ACTION_KEYS.forEach((i, k) -> event.register(k));
    }

    private boolean suckKeyDown = false;

    private final Map<KeyMapping, Runnable> keyMappingActions;
    private final Minecraft mc;
    private final Int2LongArrayMap actionTriggerTime = new Int2LongArrayMap();

    public ModKeys() {
        ImmutableMap.Builder<KeyMapping, Runnable> keyMappingActions = ImmutableMap.builder();
        keyMappingActions.put(ACTION, this::openActionMenu);
        keyMappingActions.put(VAMPIRISM_MENU, this::openVampirismMenu);
        keyMappingActions.put(VISION, this::switchVision);
        keyMappingActions.put(MINION, this::openMinionTaskMenu);
        ACTION_KEYS.forEach((i, key) -> keyMappingActions.put(key, () -> toggleAction(i)));
        this.keyMappingActions = keyMappingActions.build();
        this.mc = Minecraft.getInstance();

    }

    @SubscribeEvent
    public void handleMouseButton(InputEvent.MouseButton.Pre event) {
        handleInputEvent(event);
    }

    @SubscribeEvent
    public void handleKey(InputEvent.Key event) {
        handleInputEvent(event);
    }

    public void handleInputEvent(InputEvent event) {
        if (SUCK.isDown()) {
            suck();
        } else {
            endSuck();
            for (Map.Entry<KeyMapping, Runnable> entry : this.keyMappingActions.entrySet()) {
                if (entry.getKey().isDown()) {
                    entry.getValue().run();
                    break;
                }
            }
        }
    }

    private void suck() {
        if (!suckKeyDown) {
            HitResult mouseOver = Minecraft.getInstance().hitResult;
            suckKeyDown = true;
            Player player = Minecraft.getInstance().player;
            if (mouseOver != null && !player.isSpectator() && VampirePlayer.getOpt(player).map(vp -> vp.getLevel() > 0 && !vp.getActionHandler().isActionActive(VampireActions.BAT.get())).orElse(false)) {
                if (mouseOver instanceof EntityHitResult) {
                    VampirismMod.dispatcher.sendToServer(new ServerboundStartFeedingPacket(((EntityHitResult) mouseOver).getEntity().getId()));
                } else if (mouseOver instanceof BlockHitResult) {
                    BlockPos pos = ((BlockHitResult) mouseOver).getBlockPos();
                    VampirismMod.dispatcher.sendToServer(new ServerboundStartFeedingPacket(pos));
                } else {
                    LOGGER.warn("Unknown mouse over type while trying to feed");
                }
            }
        }
    }

    private void endSuck() {
        if (suckKeyDown) {
            suckKeyDown = false;
            VampirismMod.dispatcher.sendToServer(new ServerboundSimpleInputEvent(ServerboundSimpleInputEvent.Type.FINISH_SUCK_BLOOD));
        }
    }

    private void openActionMenu() {
        if (mc.player.isAlive()  && !mc.player.isSpectator()) {
            IPlayableFaction<?> faction = VampirismPlayerAttributes.get(mc.player).faction;
            if (faction != null) {
                Minecraft.getInstance().setScreen(new SelectActionScreen(faction.getPlayerCapability(mc.player).orElseThrow(IllegalStateException::new)));
            }
        }
    }

    private void openVampirismMenu() {
        VampirismMod.dispatcher.sendToServer(new ServerboundSimpleInputEvent(ServerboundSimpleInputEvent.Type.VAMPIRISM_MENU));
    }

    private void switchVision() {
        VampirismMod.dispatcher.sendToServer(new ServerboundSimpleInputEvent(ServerboundSimpleInputEvent.Type.TOGGLE_VAMPIRE_VISION));
    }

    private void openMinionTaskMenu() {
        if(Minecraft.getInstance().player.isSpectator()) return;
        FactionPlayerHandler.getOpt(mc.player).filter(p -> p.getLordLevel() > 0).ifPresent(p -> {
            Minecraft.getInstance().setScreen(new SelectMinionTaskScreen(p));
        });
    }

    private void toggleAction(int id) {
        long t = System.currentTimeMillis();
        if (t - this.actionTriggerTime.getOrDefault(id,0) > ACTION_BUTTON_COOLDOWN) {
            this.actionTriggerTime.put(id, t);
            Player player = mc.player;
            if (player.isAlive()) {
                FactionPlayerHandler.getOpt(player).ifPresent(factionHandler -> factionHandler.getCurrentFactionPlayer().ifPresent(factionPlayer -> toggleBoundAction(factionPlayer, factionHandler.getBoundAction(id))));
            }
        }
    }

    /**
     * Try to toggle the given action
     **/
    private void toggleBoundAction(@NotNull IFactionPlayer<?> player, @Nullable IAction<?> action) {
        if (action == null) {
            player.getRepresentingPlayer().displayClientMessage(Component.translatable("text.vampirism.action.not_bound", "/vampirism bind-action"), true);
        } else {
            if (action.getFaction().map(faction -> !faction.equals(player.getFaction())).orElse(false)) {
                player.getRepresentingPlayer().displayClientMessage(Component.translatable("text.vampirism.action.only_faction", action.getFaction().get().getName()), true);
            } else {
                VampirismMod.dispatcher.sendToServer(ServerboundToggleActionPacket.createFromRaytrace(RegUtil.id(action), Minecraft.getInstance().hitResult));
            }
        }

    }
}
