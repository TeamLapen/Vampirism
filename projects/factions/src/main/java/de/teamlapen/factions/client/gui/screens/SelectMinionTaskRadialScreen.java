package de.teamlapen.factions.client.gui.screens;

import de.teamlapen.factions.FactionsMod;
import de.teamlapen.factions.api.entities.minion.IMinionTask;
import de.teamlapen.factions.api.factions.IFactionPlayerHandler;
import de.teamlapen.factions.api.factions.ILordPlayer;
import de.teamlapen.factions.api.util.FResourceLocation;
import de.teamlapen.factions.client.config.ClientConfigHelper;
import de.teamlapen.factions.client.gui.GuiRenderer;
import de.teamlapen.factions.client.gui.radialmenu.IRadialMenuSlot;
import de.teamlapen.factions.client.gui.radialmenu.RadialMenu;
import de.teamlapen.factions.client.gui.radialmenu.RadialMenuSlot;
import de.teamlapen.factions.client.gui.screens.radial.DualSwitchingRadialMenu;
import de.teamlapen.factions.common.core.FactionKeys;
import de.teamlapen.factions.common.factions.FactionPlayerHandler;
import de.teamlapen.factions.common.network.packets.server.ServerboundSelectMinionTaskPacket;
import de.teamlapen.factions.common.network.packets.server.ServerboundSimpleInputEvent;
import de.teamlapen.factions.common.util.RegUtil;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SelectMinionTaskRadialScreen extends DualSwitchingRadialMenu<SelectMinionTaskRadialScreen.Entry> {

    public static Map<ResourceLocation, Entry> CUSTOM_ENTRIES = Stream.of(new SelectMinionTaskRadialScreen.Entry(FResourceLocation.mod("call_single"), Component.translatable("text.factions.minion.call_single"), FResourceLocation.mod("textures/minion_tasks/recall_single.png"), (SelectMinionTaskRadialScreen::callSingle)),
            new SelectMinionTaskRadialScreen.Entry(FResourceLocation.mod("call_all"), Component.translatable("text.factions.minion.call_all"), FResourceLocation.mod("textures/minion_tasks/recall.png"), (SelectMinionTaskRadialScreen::callAll)),
            new SelectMinionTaskRadialScreen.Entry(FResourceLocation.mod("respawn"), Component.translatable("text.factions.minion.respawn"), FResourceLocation.mod("textures/minion_tasks/respawn.png"), (SelectMinionTaskRadialScreen::callRespawn))).collect(Collectors.toMap(e -> e.id, e -> e));

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
                Minecraft.getInstance().player.displayClientMessage(Component.translatable("text.factions.no_minion_tasks"), true);
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
        return ClientConfigHelper.getMinionTaskOrder(lord.getFaction()).stream().filter(entry -> {
            return Optional.ofNullable(entry.getTask()).map(s -> s.isAvailable(lord)).orElse(true);
        }).collect(Collectors.toList());
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

    private static void sendTask(IMinionTask<?, ?> task) {
        FactionsMod.proxy.sendToServer(new ServerboundSelectMinionTaskPacket(-1, RegUtil.id(task)));
    }

    public static class Entry {

        private final ResourceLocation id;
        private final Component text;
        private final ResourceLocation loc;
        private final Runnable onSelected;
        private final IMinionTask<?, ?> task;

        public Entry(@NotNull IMinionTask<?, ?> task) {
            this(RegUtil.id(task), task.getName(), FResourceLocation.loc(RegUtil.id(task).getNamespace(), "textures/minion_tasks/" + RegUtil.id(task).getPath() + ".png"), (() -> sendTask(task)), task);
        }

        public Entry(@NotNull ResourceLocation id, @NotNull Component text, @NotNull ResourceLocation icon, @NotNull Runnable onSelected, @Nullable IMinionTask<?, ?> task) {
            this.id = id;
            this.text = text;
            this.loc = icon;
            this.onSelected = onSelected;
            this.task = task;
        }

        public Entry(@NotNull ResourceLocation id, @NotNull Component text, @NotNull ResourceLocation icon, @NotNull Runnable onSelected) {
            this(id, text, icon, onSelected, null);
        }

        @NotNull
        public ResourceLocation getIconLoc() {
            return loc;
        }

        @NotNull
        public ResourceLocation getId() {
            return id;
        }

        @NotNull
        public Component getText() {
            return text;
        }

        @Nullable
        public IMinionTask<?, ?> getTask() {
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