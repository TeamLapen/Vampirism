package de.teamlapen.factions.common.core;

import com.google.common.collect.ImmutableMap;
import com.mojang.blaze3d.platform.InputConstants;
import de.teamlapen.factions.FactionsMod;
import de.teamlapen.factions.api.actions.IAction;
import de.teamlapen.factions.api.entities.player.IFactionPlayer;
import de.teamlapen.factions.api.factions.IFaction;
import de.teamlapen.factions.api.util.FResourceLocation;
import de.teamlapen.factions.client.IMinecraftAccessor;
import de.teamlapen.factions.client.gui.screens.SelectActionRadialScreen;
import de.teamlapen.factions.client.gui.screens.SelectMinionTaskRadialScreen;
import de.teamlapen.factions.client.gui.screens.skills.SkillsScreen;
import de.teamlapen.factions.common.actions.ActionKeys;
import de.teamlapen.factions.common.factions.FactionPlayerHandler;
import de.teamlapen.factions.common.network.packets.server.ServerboundSimpleInputEvent;
import de.teamlapen.factions.common.network.packets.server.ServerboundToggleActionPacket;
import de.teamlapen.factions.common.util.KeyBindings;
import it.unimi.dsi.fastutil.objects.Object2LongArrayMap;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.settings.KeyConflictContext;
import net.neoforged.neoforge.client.settings.KeyModifier;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

import java.util.Arrays;
import java.util.Map;

public class FactionKeys implements IMinecraftAccessor {

    public static final KeyMapping.Category CATEGORY = new KeyMapping.Category(FResourceLocation.mod("main"));

    public static final KeyMapping ACTION = new KeyMapping("keys.factions.action", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_R, CATEGORY);
    public static final KeyMapping MINION = new KeyMapping("keys.factions.minion_task", KeyConflictContext.IN_GAME, InputConstants.UNKNOWN, CATEGORY);
    public static final KeyMapping VAMPIRISM_MENU = new KeyMapping("keys.factions.faction_Screen", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_P, CATEGORY);
    public static final KeyMapping SKILL_SCREEN = new KeyMapping("keys.factions.skill_screen", KeyConflictContext.IN_GAME, InputConstants.UNKNOWN, CATEGORY);


    public static final Map<ActionKeys, KeyMapping> ACTION_KEYS;

    /**
     * Time between multiple action button presses in ms
     */
    private static final long ACTION_BUTTON_COOLDOWN = 500;

    private final Object2LongArrayMap<ActionKeys> actionTriggerTime = new Object2LongArrayMap<>();

    private final KeyBindings keyBindings;


    static  {
        ImmutableMap.Builder<ActionKeys, KeyMapping> builder = ImmutableMap.builder();
        Arrays.stream(ActionKeys.values()).forEach(x -> {
            if (x.getDefaultKey().isPresent()) {
                builder.put(x, new KeyMapping("keys.factions.action" + (x.ordinal() + 1), KeyConflictContext.IN_GAME, KeyModifier.ALT, InputConstants.Type.KEYSYM, x.getDefaultKey().getAsInt(), CATEGORY));
            } else {
                builder.put(x, new KeyMapping("keys.factions.action" + (x.ordinal() + 1), KeyConflictContext.IN_GAME, InputConstants.UNKNOWN, CATEGORY));
            }
        });
        ACTION_KEYS = builder.build();
    }

    public FactionKeys() {
        KeyBindings.Builder builder = KeyBindings.builder()
        .addBinding(ACTION, this::openActionMenu)
            .addBinding(MINION, this::openMinionTaskMenu)
            .addBinding(VAMPIRISM_MENU, this::openVampirismMenu)
            .addBinding(SKILL_SCREEN, this::openSkillScreen);

        ACTION_KEYS.forEach((i, key) -> builder.addBinding(key, () -> toggleAction(i), true));

        this.keyBindings = builder.build();
    }

    public void registerKeyMapping(RegisterKeyMappingsEvent event) {
        event.registerCategory(CATEGORY);

        event.register(ACTION);
        event.register(MINION);
        event.register(VAMPIRISM_MENU);
        event.register(SKILL_SCREEN);

        ACTION_KEYS.forEach((i, k) -> event.register(k));
    }

    @SubscribeEvent
    public void handleMouseButton(InputEvent.MouseButton.Post event) {
        this.keyBindings.handleInputEvent(event, event.getAction());
    }

    @SubscribeEvent
    public void handleKey(InputEvent.Key event) {
        this.keyBindings.handleInputEvent(event, event.getAction());
    }

    private void toggleAction(ActionKeys key) {
        long t = System.currentTimeMillis();
        if (t - this.actionTriggerTime.getOrDefault(key, 0) > ACTION_BUTTON_COOLDOWN) {
            this.actionTriggerTime.put(key, t);
            if (player().isAlive()) {
                FactionPlayerHandler handler = FactionPlayerHandler.get(player());
                toggleBoundAction(handler.factionPlayer(), handler.getBoundAction(key));
            }
        }
    }

    private void openVampirismMenu() {
        FactionsMod.proxy.sendToServer(new ServerboundSimpleInputEvent(ServerboundSimpleInputEvent.Event.VAMPIRISM_MENU));
    }

    private void openSkillScreen() {
        FactionPlayerHandler.get(player()).getCurrentSkillPlayer().ifPresent(factionPlayer -> {
            mc().setScreen(new SkillsScreen(factionPlayer, mc().screen));
        });
    }

    private void openActionMenu() {
        if (player().isAlive() && !player().isSpectator()) {
            SelectActionRadialScreen.show();
        }
    }

    private void openMinionTaskMenu() {
        if (Minecraft.getInstance().player.isSpectator()) return;
        if (FactionPlayerHandler.get(player()).getLordLevel() > 0) {
            SelectMinionTaskRadialScreen.show();
        }
    }

    private void toggleBoundAction(IFactionPlayer<?> player, @Nullable Holder<IAction<?>> action) {
        if (action == null) {
            player.asEntity().displayClientMessage(Component.translatable("text.vampirism.action.not_bound", "/vampirism bind-action"), true);
        } else {
            IAction<?> value = action.value();
            if (!IFaction.is(player.getFaction(), value.factions())) {
                player.asEntity().displayClientMessage(Component.translatable("text.vampirism.action.wrong_faction"), true);
            } else {
                FactionsMod.proxy.sendToServer(ServerboundToggleActionPacket.createFromRaytrace(action, Minecraft.getInstance().hitResult));
            }
        }
    }


}
