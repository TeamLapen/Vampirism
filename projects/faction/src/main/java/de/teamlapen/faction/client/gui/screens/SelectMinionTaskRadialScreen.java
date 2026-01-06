package de.teamlapen.faction.client.gui.screens;

import de.teamlapen.faction.FactionsMod;
import de.teamlapen.faction.api.factions.lord.ILordPlayer;
import de.teamlapen.faction.api.util.FIdentifier;
import de.teamlapen.faction.api.world.entities.minion.IMinionTask;
import de.teamlapen.faction.client.gui.GuiRenderer;
import de.teamlapen.faction.client.gui.radialmenu.IRadialMenuSlot;
import de.teamlapen.faction.client.gui.radialmenu.RadialMenu;
import de.teamlapen.faction.client.gui.radialmenu.RadialMenuSlot;
import de.teamlapen.faction.client.gui.screens.radial.DualSwitchingRadialMenu;
import de.teamlapen.faction.common.config.FactionConfig;
import de.teamlapen.faction.common.core.FactionKeys;
import de.teamlapen.faction.common.factions.FactionPlayerHandler;
import de.teamlapen.faction.common.network.packets.server.ServerboundSelectMinionTaskPacket;
import de.teamlapen.faction.common.network.packets.server.ServerboundSimpleInputEvent;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SelectMinionTaskRadialScreen extends DualSwitchingRadialMenu<SelectMinionTaskRadialScreen.Entry> {

    public static final Map<Identifier, Entry> CUSTOM_ENTRIES = Stream.of(new SelectMinionTaskRadialScreen.Entry(FIdentifier.mod("call_single"), Component.translatable("text.factionapi.minion.call_single"), FIdentifier.mod("textures/minion_tasks/recall_single.png"), (SelectMinionTaskRadialScreen::callSingle)),
            new SelectMinionTaskRadialScreen.Entry(FIdentifier.mod("call_all"), Component.translatable("text.factionapi.minion.call_all"), FIdentifier.mod("textures/minion_tasks/recall.png"), (SelectMinionTaskRadialScreen::callAll)),
            new SelectMinionTaskRadialScreen.Entry(FIdentifier.mod("respawn"), Component.translatable("text.factionapi.minion.respawn"), FIdentifier.mod("textures/minion_tasks/respawn.png"), (SelectMinionTaskRadialScreen::callRespawn))).collect(Collectors.toMap(e -> e.id, e -> e));

    private SelectMinionTaskRadialScreen(Collection<Entry> entries, KeyMapping keyMapping) {
        super(getRadialMenu(entries), keyMapping, SelectActionRadialScreen::show);
    }

    public static void show() {
        show(FactionKeys.MINION);
    }

    public static void show(KeyMapping mapping) {
        FactionPlayerHandler.get(Minecraft.getInstance().player).getLordPlayer().filter(x -> x.getLordLevel() > 0).ifPresent(lord -> {
            Collection<Entry> tasks = getTasks(lord);
            if (tasks.isEmpty()) {
                Minecraft.getInstance().player.displayClientMessage(Component.translatable("text.factionapi.no_minion_tasks"), true);
                Minecraft.getInstance().setScreen(null);
            } else {
                Minecraft.getInstance().setScreen(new SelectMinionTaskRadialScreen(tasks, mapping));
            }
        });
    }

    @Override
    public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
    }

    private static List<Entry> getTasks(ILordPlayer<?> lord) {
        if (lord.getLordLevel() == 0) return List.of();
        return FactionConfig.client().minionTaskOrder.get(lord.getFaction()).stream()
                .filter(x -> Optional.ofNullable(x.getTask()).map(s -> s.value().isAvailable(lord)).orElse(true))
                .collect(Collectors.toList());
    }

    private static RadialMenu<Entry> getRadialMenu(Collection<Entry> playerHandler) {
        List<IRadialMenuSlot<Entry>> parts = playerHandler.stream().map(entry -> (IRadialMenuSlot<Entry>) new RadialMenuSlot<>(entry.text, entry)).toList();
        return new RadialMenu<>(i -> parts.get(i).primarySlotIcon().onSelected.run(), parts, SelectMinionTaskRadialScreen::drawActionPart, 0);
    }

    private static void drawActionPart(Entry t, GuiGraphics graphics, int posX, int posY, int size, boolean transparent) {
        GuiRenderer.blit(graphics, t.getIconLoc(), posX, posY, 16, 16, 16, 16);
    }


    private static void callAll() {
        FactionsMod.proxy.sendToServer(new ServerboundSelectMinionTaskPacket(-1, ServerboundSelectMinionTaskPacket.RECALL));

    }

    private static void callRespawn() {
        FactionsMod.proxy.sendToServer(new ServerboundSelectMinionTaskPacket(-1, ServerboundSelectMinionTaskPacket.RESPAWN));
    }

    private static void callSingle() {
        FactionsMod.proxy.sendToServer(new ServerboundSimpleInputEvent(ServerboundSimpleInputEvent.Event.SHOW_MINION_CALL_SELECTION));
    }

    private static void sendTask(Holder<IMinionTask<?, ?>> task) {
        FactionsMod.proxy.sendToServer(new ServerboundSelectMinionTaskPacket(-1, task.getKey().identifier()));
    }

    public static class Entry {

        private final Identifier id;
        private final Component text;
        private final Identifier loc;
        private final Runnable onSelected;
        private final Holder<IMinionTask<?, ?>> task;

        public Entry(@NotNull Holder<IMinionTask<?, ?>> task) {
            this(task.getKey().identifier(), task.value().getName(), task.getKey().identifier().withPath(path -> "textures/minion_tasks/" + path + ".png"), (() -> sendTask(task)), task);
        }

        public Entry(@NotNull Identifier id, @NotNull Component text, @NotNull Identifier icon, @NotNull Runnable onSelected, @Nullable Holder<IMinionTask<?, ?>> task) {
            this.id = id;
            this.text = text;
            this.loc = icon;
            this.onSelected = onSelected;
            this.task = task;
        }

        public Entry(@NotNull Identifier id, @NotNull Component text, @NotNull Identifier icon, @NotNull Runnable onSelected) {
            this(id, text, icon, onSelected, null);
        }

        @NotNull
        public Identifier getIconLoc() {
            return loc;
        }

        @NotNull
        public Identifier getId() {
            return id;
        }

        @NotNull
        public Component getText() {
            return text;
        }

        @Nullable
        public Holder<IMinionTask<?, ?>> getTask() {
            return this.task;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof Entry other) {
                return id.equals(other.id);
            }
            return false;
        }

        @Override
        public int hashCode() {
            return id.hashCode();
        }
    }

}