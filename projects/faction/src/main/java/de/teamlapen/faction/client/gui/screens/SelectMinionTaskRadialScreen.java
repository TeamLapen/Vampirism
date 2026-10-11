package de.teamlapen.faction.client.gui.screens;

import de.teamlapen.faction.FactionsMod;
import de.teamlapen.faction.api.factions.IFaction;
import de.teamlapen.faction.api.factions.lord.ILordPlayer;
import de.teamlapen.faction.api.util.FIdentifier;
import de.teamlapen.faction.api.world.entities.minion.tasks.IMinionTask;
import de.teamlapen.faction.api.world.entities.minion.tasks.IMinionTaskCategory;
import de.teamlapen.faction.client.gui.GuiRenderer;
import de.teamlapen.faction.client.gui.radialmenu.IRadialMenuSlot;
import de.teamlapen.faction.client.gui.radialmenu.RadialMenu;
import de.teamlapen.faction.client.gui.radialmenu.RadialMenuSlot;
import de.teamlapen.faction.client.gui.screens.radial.DualSwitchingRadialMenu;
import de.teamlapen.faction.common.config.FactionConfig;
import de.teamlapen.faction.common.core.FactionDataComponents;
import de.teamlapen.faction.common.core.FactionKeys;
import de.teamlapen.faction.common.core.FactionMinionTaskCategories;
import de.teamlapen.faction.common.core.ModRegistries;
import de.teamlapen.faction.common.factions.FactionPlayerHandler;
import de.teamlapen.faction.common.network.packets.server.ServerboundSelectMinionTaskPacket;
import de.teamlapen.faction.common.network.packets.server.ServerboundSimpleInputEvent;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SelectMinionTaskRadialScreen extends DualSwitchingRadialMenu<SelectMinionTaskRadialScreen.Entry> {

    /**
     * Prefix that distinguishes a {@link CategoryEntry} id from a task id
     */
    public static final String CATEGORY_PREFIX = "category/";

    public static final Map<Identifier, Entry> CUSTOM_ENTRIES = Stream.of(
            new Entry(FIdentifier.mod("call_all"), Component.translatable("minion_task.factionapi.call_all"), FIdentifier.mod("textures/minion_tasks/recall.png"), SelectMinionTaskRadialScreen::callAll, null, FactionMinionTaskCategories.RECALL, true),
            new Entry(FIdentifier.mod("call_single"), Component.translatable("minion_task.factionapi.call_single"), FIdentifier.mod("textures/minion_tasks/recall_single.png"), SelectMinionTaskRadialScreen::callSingle, null, FactionMinionTaskCategories.RECALL, false),
            new Entry(FIdentifier.mod("respawn"), Component.translatable("minion_task.factionapi.respawn"), FIdentifier.mod("textures/minion_tasks/respawn.png"), SelectMinionTaskRadialScreen::callRespawn, null, FactionMinionTaskCategories.RECALL, false))
            .collect(Collectors.toMap(e -> e.id, e -> e, (a, _) -> a, LinkedHashMap::new));

    private SelectMinionTaskRadialScreen(Collection<Entry> entries, KeyMapping keyMapping) {
        super(getRadialMenu(entries), keyMapping, SelectActionRadialScreen::show);
    }

    public static void show() {
        show(FactionKeys.MINION);
    }

    @SuppressWarnings("DataFlowIssue")
    public static void show(KeyMapping mapping) {
        FactionPlayerHandler.get(Minecraft.getInstance().player).getPlayerLord().filter(x -> x.getLordLevel() > 0).ifPresent(lord -> {
            Collection<Entry> tasks = getTasks(lord);
            if (tasks.isEmpty()) {
                Minecraft.getInstance().player.sendOverlayMessage(Component.translatable("gui.factionapi.minion_radial.no_minion_tasks"));
                Minecraft.getInstance().setScreen(null);
            } else {
                Minecraft.getInstance().setScreen(new SelectMinionTaskRadialScreen(tasks, mapping));
            }
        });
    }

    @Override
    public void extractBackground(@NonNull GuiGraphicsExtractor GuiGraphicsExtractor, int mouseX, int mouseY, float partialTick) {
    }

    /**
     * Resolves the configured order into the entries that are displayed. Category entries are resolved into their
     * currently available members, all other entries are filtered by their availability.
     */
    private static List<Entry> getTasks(ILordPlayer lord) {
        if (lord.getLordLevel() == 0) return List.of();
        List<Entry> entries = new ArrayList<>();
        for (Entry entry : FactionConfig.preferences().minionTaskOrder().getOrder(lord.getFaction())) {
            if (entry instanceof CategoryEntry category) {
                resolveCategory(category.getCategoryHolder(), lord).ifPresent(entries::add);
            } else if (isAvailable(entry, lord)) {
                entries.add(entry);
            }
        }
        return entries;
    }

    /**
     * @return All global command tasks that can be given by the given faction
     */
    public static Stream<Holder<IMinionTask<?>>> globalTasks(Holder<? extends IFaction<?>> faction) {
        return ModRegistries.MINION_TASKS.listElements()
                .filter(x -> x.components().has(FactionDataComponents.MINION_TASK_GLOBAL_COMMAND))
                .filter(x -> IFaction.is(faction, x.value().allowedFactions()))
                .map(x -> x);
    }

    /**
     * @return All entries of the given category that are currently available to the lord player, in registry order
     * followed by the custom entries
     */
    public static List<Entry> categoryMembers(Holder<IMinionTaskCategory> category, ILordPlayer lord) {
        return Stream.concat(
                        globalTasks(lord.getFaction()).map(Entry::new).filter(x -> isSameCategory(x.getCategory(), category)),
                        CUSTOM_ENTRIES.values().stream().filter(x -> isSameCategory(x.getCategory(), category)))
                .filter(x -> isAvailable(x, lord))
                .toList();
    }

    /**
     * Builds the entry that represents the given category. If only a single member is available, that member is
     * returned directly instead, so no pointless sub ring is displayed.
     */
    private static Optional<Entry> resolveCategory(Holder<IMinionTaskCategory> category, ILordPlayer lord) {
        List<Entry> members = categoryMembers(category, lord);
        if (members.isEmpty()) {
            return Optional.empty();
        }
        if (members.size() == 1) {
            return Optional.of(members.getFirst());
        }
        Entry defaultEntry = members.stream().filter(Entry::isCategoryDefault).findFirst().orElse(null);
        return Optional.of(new CategoryEntry(category, defaultEntry, members));
    }

    public static boolean isSameCategory(@Nullable Holder<IMinionTaskCategory> a, @Nullable Holder<IMinionTaskCategory> b) {
        if (a == null || b == null) return a == b;
        return a.value() == b.value();
    }

    private static boolean isAvailable(Entry entry, ILordPlayer lord) {
        return Optional.ofNullable(entry.getTask()).map(s -> s.value().isAvailable(lord)).orElse(true);
    }

    private static RadialMenu<Entry> getRadialMenu(Collection<Entry> entries) {
        List<IRadialMenuSlot<Entry>> parts = entries.stream().map(entry -> {
            List<IRadialMenuSlot.Item<Entry>> secondary = entry instanceof CategoryEntry category
                    ? category.getChildren().stream().map(child -> new IRadialMenuSlot.Item<>(child, child.getText())).toList()
                    : List.of();
            return (IRadialMenuSlot<Entry>) new RadialMenuSlot<>(entry.getText(), entry, secondary);
        }).collect(Collectors.toList());
        return new RadialMenu<>(parts, SelectMinionTaskRadialScreen::drawActionPart, SelectMinionTaskRadialScreen::select);
    }

    private static void drawActionPart(Entry t, GuiGraphicsExtractor graphics, int posX, int posY, int size, boolean transparent) {
        GuiRenderer.blit(graphics, t.getIconLoc(), posX, posY, 16, 16, 16, 16);
    }

    private static void select(Entry slot, @Nullable Entry parentSlot) {
        if (slot instanceof CategoryEntry category) {
            category.getPrimaryEntry().ifPresent(x -> select(x, slot));
        } else {
            slot.onSelected.run();
        }
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

    private static void sendTask(Holder<IMinionTask<?>> task) {
        FactionsMod.proxy.sendToServer(new ServerboundSelectMinionTaskPacket(-1, task.getKey().identifier()));
    }

    public static class Entry {

        private final Identifier id;
        private final Component text;
        private final Identifier loc;
        private final Runnable onSelected;
        private final Holder<IMinionTask<?>> task;
        private final @Nullable Holder<IMinionTaskCategory> category;
        private final boolean categoryDefault;

        public Entry(@NotNull Holder<IMinionTask<?>> task) {
            this(task.getKey().identifier(), task.value().getName(), task.getKey().identifier().withPath(path -> "textures/minion_tasks/" + path + ".png"), () -> sendTask(task), task, task.components().get(FactionDataComponents.MINION_TASK_CATEGORY), task.components().has(FactionDataComponents.MINION_TASK_CATEGORY_DEFAULT));
        }

        public Entry(@NotNull Identifier id, @NotNull Component text, @NotNull Identifier icon, @NotNull Runnable onSelected, @Nullable Holder<IMinionTask<?>> task) {
            this(id, text, icon, onSelected, task, null, false);
        }

        public Entry(@NotNull Identifier id, @NotNull Component text, @NotNull Identifier icon, @NotNull Runnable onSelected) {
            this(id, text, icon, onSelected, null, null, false);
        }

        public Entry(@NotNull Identifier id, @NotNull Component text, @NotNull Identifier icon, @NotNull Runnable onSelected, @Nullable Holder<IMinionTask<?>> task, @Nullable Holder<IMinionTaskCategory> category, boolean categoryDefault) {
            this.id = id;
            this.text = text;
            this.loc = icon;
            this.onSelected = onSelected;
            this.task = task;
            this.category = category;
            this.categoryDefault = categoryDefault;
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
        public Holder<IMinionTask<?>> getTask() {
            return this.task;
        }

        /**
         * @return The category this entry is grouped into or null if it is a top level entry
         */
        @Nullable
        public Holder<IMinionTaskCategory> getCategory() {
            return this.category;
        }

        public boolean isCategoryDefault() {
            return this.categoryDefault;
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

    /**
     * Represents a whole {@link IMinionTaskCategory} in the menu. Its members are displayed in a second ring that is
     * only revealed once the entry is hovered.
     * <p>
     * Instances that are used for the ordering configuration hold no children, those are resolved when the menu is
     * opened.
     */
    public static class CategoryEntry extends Entry {

        private final Holder<IMinionTaskCategory> categoryHolder;
        private final @Nullable Entry defaultEntry;
        private final List<Entry> children;
        private @Nullable Identifier resolvedIcon;

        public CategoryEntry(@NotNull Holder<IMinionTaskCategory> category) {
            this(category, null, List.of());
        }

        public CategoryEntry(@NotNull Holder<IMinionTaskCategory> category, @Nullable Entry defaultEntry, @NotNull List<Entry> children) {
            super(id(category), category.value().getName(), fallbackIcon(category), () -> {
            }, null, category, false);
            this.categoryHolder = category;
            this.defaultEntry = defaultEntry;
            this.children = List.copyOf(children);
        }

        /**
         * A category is displayed with the icon of its default task, so a lord immediately sees what selecting the
         * category itself does. Only if the category has no default task at all a dedicated category texture is used.
         */
        @Override
        public @NotNull Identifier getIconLoc() {
            if (this.defaultEntry != null) {
                return this.defaultEntry.getIconLoc();
            }
            if (this.resolvedIcon == null) {
                this.resolvedIcon = defaultTaskIcon(this.categoryHolder).orElseGet(super::getIconLoc);
            }
            return this.resolvedIcon;
        }

        /**
         * @return The icon of the category's default entry, preferring a task of the player's own faction
         */
        private static Optional<Identifier> defaultTaskIcon(Holder<IMinionTaskCategory> category) {
            Optional<Holder<? extends IFaction<?>>> faction = Optional.ofNullable(Minecraft.getInstance().player).map(FactionPlayerHandler::get).map(FactionPlayerHandler::getFaction);
            return faction.stream().flatMap(SelectMinionTaskRadialScreen::globalTasks).<Entry>map(Entry::new)
                    .filter(x -> x.isCategoryDefault() && isSameCategory(x.getCategory(), category))
                    .findFirst()
                    .or(() -> CUSTOM_ENTRIES.values().stream().filter(x -> x.isCategoryDefault() && isSameCategory(x.getCategory(), category)).findFirst())
                    .map(Entry::getIconLoc);
        }

        public static Identifier id(@NotNull Holder<IMinionTaskCategory> category) {
            return category.getKey().identifier().withPath(path -> CATEGORY_PREFIX + path);
        }

        private static Identifier fallbackIcon(@NotNull Holder<IMinionTaskCategory> category) {
            return category.getKey().identifier().withPath(path -> "textures/minion_tasks/" + CATEGORY_PREFIX + path + ".png");
        }

        @NotNull
        public Holder<IMinionTaskCategory> getCategoryHolder() {
            return this.categoryHolder;
        }

        @NotNull
        public List<Entry> getChildren() {
            return this.children;
        }

        /**
         * @return The entry that is activated when the category itself is selected
         */
        public Optional<Entry> getPrimaryEntry() {
            return Optional.ofNullable(this.defaultEntry);
        }
    }
}
