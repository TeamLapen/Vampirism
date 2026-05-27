package de.teamlapen.faction.common.core;

import com.google.common.collect.ImmutableMap;
import com.mojang.blaze3d.platform.InputConstants;
import de.teamlapen.faction.FactionsMod;
import de.teamlapen.faction.api.factions.IFaction;
import de.teamlapen.faction.api.factions.actions.IAction;
import de.teamlapen.faction.api.util.FIdentifier;
import de.teamlapen.faction.api.world.entities.player.IFactionPlayer;
import de.teamlapen.faction.client.IMinecraftAccessor;
import de.teamlapen.faction.client.gui.screens.ILastScreenProvider;
import de.teamlapen.faction.client.gui.screens.SelectActionRadialScreen;
import de.teamlapen.faction.client.gui.screens.SelectMinionTaskRadialScreen;
import de.teamlapen.faction.client.gui.screens.skills.SkillsScreen;
import de.teamlapen.faction.common.factions.FactionPlayerHandler;
import de.teamlapen.faction.common.factions.actions.ActionKeys;
import de.teamlapen.faction.common.network.packets.server.ServerboundSimpleInputEvent;
import de.teamlapen.faction.common.network.packets.server.ServerboundToggleActionPacket;
import de.teamlapen.faction.common.util.KeyBindings;
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

    public static final KeyMapping.Category CATEGORY = new KeyMapping.Category(FIdentifier.mod("main"));

    public static final KeyMapping ACTION = new KeyMapping("key.factionapi.action", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_R, CATEGORY);
    public static final KeyMapping MINION = new KeyMapping("key.factionapi.minion_task", KeyConflictContext.IN_GAME, InputConstants.UNKNOWN, CATEGORY);
    public static final KeyMapping FACTION_MENU = new KeyMapping("key.factionapi.faction_menu", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_P, CATEGORY);
    public static final KeyMapping SKILL_SCREEN = new KeyMapping("key.factionapi.skill_screen", KeyConflictContext.IN_GAME, InputConstants.UNKNOWN, CATEGORY);
    public static final KeyMapping ITEM_DESCRIPTION = new KeyMapping("key.factionapi.item_description", KeyConflictContext.GUI, InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_LEFT_SHIFT, CATEGORY);


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
                builder.put(x, new KeyMapping("key.factionapi.action" + (x.ordinal() + 1), KeyConflictContext.IN_GAME, KeyModifier.ALT, InputConstants.Type.KEYSYM, x.getDefaultKey().getAsInt(), CATEGORY));
            } else {
                builder.put(x, new KeyMapping("key.factionapi.action" + (x.ordinal() + 1), KeyConflictContext.IN_GAME, InputConstants.UNKNOWN, CATEGORY));
            }
        });
        ACTION_KEYS = builder.build();
    }

    public FactionKeys() {
        KeyBindings.Builder builder = KeyBindings.builder()
        .addBinding(ACTION, this::openActionMenu)
            .addBinding(MINION, this::openMinionTaskMenu)
            .addBinding(FACTION_MENU, this::openFactionMenu)
            .addBinding(SKILL_SCREEN, this::openSkillScreen);

        ACTION_KEYS.forEach((i, key) -> builder.addBinding(key, () -> toggleAction(i), true));

        this.keyBindings = builder.build();
    }

    public void registerKeyMapping(RegisterKeyMappingsEvent event) {
        event.registerCategory(CATEGORY);

        event.register(ACTION);
        event.register(MINION);
        event.register(FACTION_MENU);
        event.register(SKILL_SCREEN);
        event.register(ITEM_DESCRIPTION);

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

    private void openFactionMenu() {
        FactionsMod.proxy.sendToServer(new ServerboundSimpleInputEvent(ServerboundSimpleInputEvent.Event.FACTION_MENU));
    }

    private void openSkillScreen() {
        FactionPlayerHandler.get(player()).getCurrentSkillPlayer().ifPresent(factionPlayer -> {
            mc().setScreen(new SkillsScreen(factionPlayer, ILastScreenProvider.current()));
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
            player.asEntity().sendOverlayMessage(Component.translatable("message.factionapi.action.not_bound", "/factions bind-action"));
        } else {
            IAction<?> value = action.value();
            if (!IFaction.is(player.getFaction(), value.factions())) {
                player.asEntity().sendOverlayMessage(Component.translatable("message.factionapi.action.wrong_faction"));
            } else {
                FactionsMod.proxy.sendToServer(ServerboundToggleActionPacket.createFromRaytrace(action, Minecraft.getInstance().hitResult));
            }
        }
    }


}
