package de.teamlapen.vampirism.client.gui.screens;

import de.teamlapen.lib.lib.client.gui.screens.radialmenu.IRadialMenuSlot;
import de.teamlapen.lib.lib.client.gui.screens.radialmenu.RadialMenu;
import de.teamlapen.lib.lib.client.gui.screens.radialmenu.RadialMenuSlot;
import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.entity.factions.IFactionPlayerHandler;
import de.teamlapen.vampirism.api.entity.minion.IMinionTask;
import de.teamlapen.vampirism.client.ClientConfigHelper;
import de.teamlapen.vampirism.client.core.ModKeys;
import de.teamlapen.vampirism.client.gui.screens.radial.DualSwitchingRadialMenu;
import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import de.teamlapen.vampirism.network.ServerboundSelectMinionTaskPacket;
import de.teamlapen.vampirism.network.ServerboundSimpleInputEvent;
import de.teamlapen.vampirism.util.RegUtil;
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

    public static Map<ResourceLocation, Entry> CUSTOM_ENTRIES = Stream.of(new SelectMinionTaskRadialScreen.Entry(new ResourceLocation(REFERENCE.MODID, "call_single"), Component.translatable("text.vampirism.minion.call_single"), new ResourceLocation(REFERENCE.MODID, "textures/minion_tasks/recall_single.png"), (SelectMinionTaskRadialScreen::callSingle)),
            new SelectMinionTaskRadialScreen.Entry(new ResourceLocation(REFERENCE.MODID, "call_all"), Component.translatable("text.vampirism.minion.call_all"), new ResourceLocation(REFERENCE.MODID, "textures/minion_tasks/recall.png"), (SelectMinionTaskRadialScreen::callAll)),
            new SelectMinionTaskRadialScreen.Entry(new ResourceLocation(REFERENCE.MODID, "respawn"), Component.translatable("text.vampirism.minion.respawn"), new ResourceLocation(REFERENCE.MODID, "textures/minion_tasks/respawn.png"), (SelectMinionTaskRadialScreen::callRespawn))).collect(Collectors.toMap(e -> e.id, e -> e));

    private SelectMinionTaskRadialScreen(Collection<Entry> entries, KeyMapping keyMapping) {
        super(getRadialMenu(entries), keyMapping, SelectActionRadialScreen::show);
    }

    public static void show() {
        show(ModKeys.MINION);
    }

    public static void show(KeyMapping mapping) {
        FactionPlayerHandler.getOpt(Minecraft.getInstance().player).filter(p -> p.getLordLevel() > 0).ifPresent(p -> {
            Collection<Entry> tasks = getTasks(p);
            if (tasks.isEmpty() ) {
                Minecraft.getInstance().player.displayClientMessage(Component.translatable("text.vampirism.no_minion_tasks"), true);
                Minecraft.getInstance().setScreen(null);
            } else {
                Minecraft.getInstance().setScreen(new SelectMinionTaskRadialScreen(tasks, mapping));
            }
        });
    }

    private static List<Entry> getTasks(IFactionPlayerHandler playerHandler) {
        if (playerHandler.getLordLevel() == 0) return List.of();
        return playerHandler.getCurrentFactionPlayer().map(player -> {
            return ClientConfigHelper.getMinionTaskOrder(playerHandler.getCurrentFaction()).stream().filter(entry -> {
                        return Optional.ofNullable(entry.getTask()).map(s -> s.isAvailable(player.getFaction(), playerHandler)).orElse(true);
            }).collect(Collectors.toList());
                }
        ).orElseGet(List::of);
    }

    private static RadialMenu<Entry> getRadialMenu(Collection<Entry> playerHandler) {
        List<IRadialMenuSlot<Entry>> parts = playerHandler.stream().map(entry -> (IRadialMenuSlot<Entry>) new RadialMenuSlot<>(entry.text, entry)).toList();
        return new RadialMenu<>(i -> parts.get(i).primarySlotIcon().onSelected.run(), parts, SelectMinionTaskRadialScreen::drawActionPart, 0);
    }

    private static void drawActionPart(Entry t, GuiGraphics graphics, int posX, int posY, int size, boolean transparent) {
        graphics.blit(t.getIconLoc(), posX, posY, 0, 0, 0, 16, 16, 16, 16);
    }


    private static void callAll() {
        VampirismMod.proxy.sendToServer(new ServerboundSelectMinionTaskPacket(-1, ServerboundSelectMinionTaskPacket.RECALL));

    }

    private static void callRespawn() {
        VampirismMod.proxy.sendToServer(new ServerboundSelectMinionTaskPacket(-1, ServerboundSelectMinionTaskPacket.RESPAWN));
    }

    private static void callSingle() {
        VampirismMod.proxy.sendToServer(new ServerboundSimpleInputEvent(ServerboundSimpleInputEvent.Type.SHOW_MINION_CALL_SELECTION));
    }

    private static void sendTask(IMinionTask<?, ?> task) {
        VampirismMod.proxy.sendToServer(new ServerboundSelectMinionTaskPacket(-1, RegUtil.id(task)));
    }

    public static class Entry {

        private final ResourceLocation id;
        private final Component text;
        private final ResourceLocation loc;
        private final Runnable onSelected;
        private final IMinionTask<?,?> task;

        public Entry(@NotNull IMinionTask<?, ?> task) {
            this(RegUtil.id(task), task.getName(), new ResourceLocation(RegUtil.id(task).getNamespace(), "textures/minion_tasks/" + RegUtil.id(task).getPath() + ".png"), (() -> sendTask(task)), task);
        }

        public Entry(@NotNull ResourceLocation id, @NotNull Component text, @NotNull ResourceLocation icon, @NotNull Runnable onSelected, @Nullable IMinionTask<?,?> task) {
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
        public IMinionTask<?,?> getTask() {
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